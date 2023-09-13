import requests

url = 'http://localhost:8000'
data = {'key': 'value'}
headers = {'Content-type': 'application/json'}

response = requests.post(url, json=data, headers=headers)

print(response.status_code)
print(response.text)
