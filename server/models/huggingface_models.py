from transformers import LlamaForCausalLM, LlamaTokenizer, LlamaConfig
from transformers import AutoTokenizer, T5ForConditionalGeneration
from transformers import AutoModelForSeq2SeqLM, AutoTokenizer
from transformers import AutoModel
from transformers import RobertaTokenizer, T5ForConditionalGeneration
from transformers import AutoModelForCausalLM
from transformers import BitsAndBytesConfig
import argparse
import transformers
import torch

nf4_config = BitsAndBytesConfig(
    load_in_4bit=True,
    bnb_4bit_quant_type="nf4",
    bnb_4bit_use_double_quant=True,
    bnb_4bit_compute_dtype=torch.bfloat16
)


def read_from_file(path: str) -> str:
    opened_file = open(path, "r")
    data = opened_file.read()
    return data


def codet5_base_model(text: str):
    tokenizer = RobertaTokenizer.from_pretrained('Salesforce/codet5-base')
    model = T5ForConditionalGeneration.from_pretrained('Salesforce/codet5-base')
    input_ids = tokenizer(text, return_tensors="pt").input_ids
    generated_ids = model.generate(input_ids, max_length=8)
    print(tokenizer.decode(generated_ids[0], skip_special_tokens=True))


def codet5_small_model(text: str):
    tokenizer = RobertaTokenizer.from_pretrained('Salesforce/codet5-small')
    model = T5ForConditionalGeneration.from_pretrained('Salesforce/codet5-small')
    input_ids = tokenizer(text, return_tensors="pt").input_ids
    generated_ids = model.generate(input_ids, max_length=8)
    print(tokenizer.decode(generated_ids[0], skip_special_tokens=True))


def starcoder_model(text: str):
    checkpoint = "bigcode/starcoderbase-1b"
    tokenizer = AutoTokenizer.from_pretrained(checkpoint)
    model = AutoModelForCausalLM.from_pretrained(checkpoint)
    inputs = tokenizer.encode(text, return_tensors="pt")
    outputs = model.generate(inputs, max_length=8)
    print(tokenizer.decode(outputs[0]))


def healing(tokenizer, model, prefix, outputs):
    pass


def llama_model(text: str):
    model_name = "codellama/CodeLlama-7b-hf"
    tokenizer = AutoTokenizer.from_pretrained(model_name, use_fast=True)
    model = LlamaForCausalLM.from_pretrained(model_name, quantization_config=nf4_config)
    # model = LlamaForCausalLM.from_pretrained(model_name)

    pipeline = transformers.pipeline(
        "text-generation",
        model=model,
        tokenizer=tokenizer,
        torch_dtype=torch.float32,
        device_map="auto",
    )

    sequences = pipeline(
        text,
        do_sample=True,
        top_k=10,
        temperature=0.1,
        top_p=0.95,
        num_return_sequences=1,
        eos_token_id=tokenizer.eos_token_id,
        max_length=200,
    )
    for seq in sequences:
        print(f"Result: {seq['generated_text']}")


'''
def llama_model(text: str):
    model = "codellama/CodeLlama-7b-hf"
    tokenizer = AutoTokenizer.from_pretrained(model, use_fast=True)
    pipeline = transformers.pipeline(
        "text-generation",
        model=model,
        torch_dtype=torch.float32,
        device_map="auto",
    )

    sequences = pipeline(
        text,
        do_sample=True,
        top_k=10,
        temperature=0.1,
        top_p=0.95,
        num_return_sequences=1,
        eos_token_id=tokenizer.eos_token_id,
        max_length=200,
    )
    for seq in sequences:
        print(f"Result: {seq['generated_text']}")
'''

if __name__ == "__main__":
    # codet5_model("123")
    # llama_model("def print_hello_world():")
    # codet5_base_model("def print_hello_world():")
    # codet5_small_model("def print_hello_world():")
    # starcoder_model("def print_hello_world():")
    # In idea, it might be run using command like this: python huggingface_models.py --model codellama/CodeLlama-7b-hf
    parser = argparse.ArgumentParser()
    parser.add_argument("--model", type=str, default="codellama/CodeLlama-7b-hf")
    args = parser.parse_args()

