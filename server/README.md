# Python server

Note: On macOS, MPS [requires](https://github.com/huggingface/transformers/issues/22502) Torch nightly.

Usage:

`python server.py -p 8080 --model`


Call the API using curl:

```sh
curl -X POST localhost:8080/generate --data "{ 'prompt': 'def ping_with_back_off():\n  ' }"
```

