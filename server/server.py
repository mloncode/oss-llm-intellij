from functools import partial
from http.server import HTTPServer, BaseHTTPRequestHandler
import argparse
import json
import re

import torch
import transformers
from transformers import BitsAndBytesConfig, AutoModelForCausalLM, AutoTokenizer


models = {}

class RequestHandler(BaseHTTPRequestHandler):
    def __init__(self, model_pipeline,  *args, **kwargs):
        self.pipeline = model_pipeline
        super().__init__(*args, **kwargs)

    def _set_headers(self):
        self.send_response(200)
        self.send_header("Content-type", "application/json")
        self.end_headers()

    def do_POST(self): #TODO: add a route /generate
        self._set_headers()
        content_len = int(self.headers.get("Content-Length", 0))
        post_body = self.rfile.read(content_len)
        json_data = json.loads(post_body)
        text = json_data["prompt"]
        max_new_tokens = json_data.get("max_new_tokens", 200)

        outputs = self.pipeline(
            text,
            do_sample=True,
            temperature=0.1,
            top_p=0.95,
            num_return_sequences=1,
            max_new_tokens=max_new_tokens,
            # max_length=200,
        )

        # model = models[json_data["model"]]
        # processed_texts = model(text, json_data["max_new_tokens"])
        results = {"results" : [o["generated_text"][len(text):] for o in outputs]}
        self.wfile.write(json.dumps(results).encode("utf-8"))

    def do_GET(self):
        self._set_headers()
        models_json = json.dumps({"models": list(models.keys())})
        self.wfile.write(models_json.encode("utf-8"))


def run(args):
    # load a model
    kwargs = {}
    if (re.search("codellama", args.model) or re.search("starcoder(base)?", args.model)) and not torch.backends.mps.is_available():
        nf4_config = BitsAndBytesConfig(
            load_in_4bit=True,
            bnb_4bit_quant_type="nf4",
            bnb_4bit_use_double_quant=True,
            bnb_4bit_compute_dtype=torch.bfloat16
        )
        kwargs["quantization_config"] = nf4_config

    print(f"Loading {args.model}")
    model = AutoModelForCausalLM.from_pretrained(args.model, **kwargs)
    tokenizer = AutoTokenizer.from_pretrained(args.model)
    global models
    models[args.model] = model

    pipeline = transformers.pipeline(
        "text-generation",
        model=model,
        torch_dtype=torch.bfloat16,
        device="mps" if torch.backends.mps.is_available() else "auto",
        tokenizer=tokenizer,
        eos_token_id=tokenizer.eos_token_id
    )
    # print(f"loaded to device {model.hf_device_map}")

    server_address = (args.host, args.port)
    httpd = HTTPServer(server_address, partial(RequestHandler, pipeline))
    print(f"Starting http server on {args.host}:{args.port}")
    httpd.serve_forever()


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Run a python OSS LLM inference server")
    parser.add_argument(
        "-p",
        dest="port",
        type=int,
        help="Specify the port",
        default=8000,
        required=False,
    )
    parser.add_argument(
        "-a",
        dest="host",
        type=str,
        help="Specify the address",
        default="localhost",
        required=False,
    )
    parser.add_argument("--model", type=str, default="bigcode/starcoderbase-1b", help="HF checkpoint to use")
    args = parser.parse_args()
    run(args)
