from http.server import HTTPServer, BaseHTTPRequestHandler
import argparse
import json

models = [{"model_name": "StarCoder"}, {"model_name": "codellama"}, {"model_name": "codet5"}]


class RequestHandler(BaseHTTPRequestHandler):
    def _set_headers(self):
        self.send_response(200)
        self.send_header("Content-type", "application/json")
        self.end_headers()

    def do_POST(self):
        self._set_headers()
        content_len = int(self.headers.get("Content-Length", 0))
        post_body = self.rfile.read(content_len)
        text_received = json.loads(post_body)["prompt"]
        json_bytes = json.dumps(
            {"results": [{"text": text_received}, {"text": text_received + "v2"}]}
        ).encode("utf-8")
        self.wfile.write(json_bytes)

    def do_GET(self):
        self._set_headers()
        models_json = json.dumps({"models": models})
        self.wfile.write(models_json.encode("utf-8"))


def run(port, addr):
    server_address = (addr, port)
    httpd = HTTPServer(server_address, RequestHandler)

    print(f"Starting httpd server on {addr}:{port}")
    httpd.serve_forever()


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Run a python server.")
    parser.add_argument(
        "-p",
        dest="port",
        type=int,
        help="Specify the port, default is 8000",
        default=8000,
        required=False,
    )
    parser.add_argument(
        "-a",
        dest="address",
        type=str,
        help="Specify the address, default is localhost",
        default="localhost",
        required=False,
    )
    args = parser.parse_args()
    run(port=args.port, addr=args.address)
