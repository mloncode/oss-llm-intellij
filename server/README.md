# Python server
Usage:

`python server.py [-p PORT] [-a HOSTNAME| address]`

Default address is `localhost`, default port is `8000`

Call to API using curl:

`curl -X POST [ADDRESS]:[PORT] --data "your_text_here"`

In response json in form `{"text": "another_text_here"}` will be returned.

Example:

`python server.py -p 8000 -a localhost`
