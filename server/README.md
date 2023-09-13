# Python server
Usage:

`python server.py [-p PORT] [-a ADDRESS| addres]`

address must be in form `your.address.com`, e.g. without `http` prefix.
Default address is `localhost`, default port is `8000`

Call to API using curl:

`curl -X POST [ADDRESS]:[PORT] --data "your_text_here"`

In response json in form `{"text": "another_text_here"}` will be returned.
