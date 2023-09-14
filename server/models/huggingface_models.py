from transformers import LlamaForCausalLM, LlamaTokenizer
from transformers import AutoTokenizer, T5ForConditionalGeneration
from transformers import AutoModelForSeq2SeqLM, AutoTokenizer
from transformers import AutoModel
from transformers import RobertaTokenizer, T5ForConditionalGeneration
from transformers import AutoModelForCausalLM
import transformers
import torch

# Does not work locally because of the size of the models.

'''
def llama_model(text: str) -> str:
    tokenizer = AutoTokenizer.from_pretrained("daryl149/llama-2-7b-chat-hf")
    model = AutoModel.from_pretrained("daryl149/llama-2-7b-chat-hf")


    # tokenizer = LlamaTokenizer.from_pretrained("/output/path")
    # model = LlamaForCausalLM.from_pretrained("/output/path")
    tokenized = tokenizer.encode([text])
    output = model.generate(**tokenized)
    return output


def codet5_model(text: str) -> str:
    checkpoint = "Salesforce/instructcodet5p-16b"
    device = torch.device("cuda:0") if torch.cuda.is_available() else torch.device("cpu")
    tokenizer = AutoTokenizer.from_pretrained(checkpoint)
    model = AutoModelForSeq2SeqLM.from_pretrained(checkpoint,
                                                  torch_dtype=torch.float16,
                                                  low_cpu_mem_usage=True,
                                                  trust_remote_code=True).to(device)

    encoding = tokenizer("def print_hello_world():", return_tensors="pt").to(device)
    encoding['decoder_input_ids'] = encoding['input_ids'].clone()
    outputs = model.generate(**encoding, max_length=15)
    print(tokenizer.decode(outputs[0], skip_special_tokens=True))
'''


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
    device = torch.device("cuda:0") if torch.cuda.is_available() else torch.device("cpu")
    tokenizer = AutoTokenizer.from_pretrained(checkpoint)
    model = AutoModelForCausalLM.from_pretrained(checkpoint).to(device)
    inputs = tokenizer.encode(text, return_tensors="pt").to(device)
    outputs = model.generate(inputs)
    print(tokenizer.decode(outputs[0]))


def llama_model(text: str):
    model = "codellama/CodeLlama-7b-hf"
    tokenizer = AutoTokenizer.from_pretrained(model)
    pipeline = transformers.pipeline(
        "text-generation",
        model=model,
        torch_dtype=torch.float16,
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


if __name__ == "__main__":
    # codet5_model("123")
    # llama_model("def print_hello_world():")
    # codet5_base_model("def print_hello_world():")
    # codet5_small_model("def print_hello_world():")
    # starcoder_model("def print_hello_world():")
    llama_model("def print_hello_world():")
