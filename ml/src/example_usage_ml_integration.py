import requests

# example usage disease predictor
# url = "http://127.0.0.1:5000/predict_disease"
# headers = {"Content-Type": "application/json"}
# message1 = "I have such symptoms: cough, fever, chest pain, headache"
# message2 =  "I have such symptoms: cough, loss of smell, loss of taste"
# message3 = "I have such symptoms: runny nose, swelling, rash"
# data = {"message": message1}
#
# response = requests.post(url, json=data, headers=headers)
#
# if response.status_code == 200:
#     print("Chatbot reply:", response.json()["reply"])
# else:
#     print("Error:", response.json())


# example usage doctor type search
# url = "http://127.0.0.1:5001/doctor_search"
# headers = {"Content-Type": "application/json"}
# message = ['pneumonia', 'bronchitis', 'influenza']
# data = {"message": message}
# response = requests.post(url, json=data, headers=headers)
#
# if response.status_code == 200:
#     print("Chatbot reply:", response.json()["reply"])
# else:
#     print("Error:", response.json())


# example usage main service that orchestrates work
url = "http://127.0.0.1:5003/chatbot"
headers = {"Content-Type": "application/json"}
message = "I have such symptoms: runny nose, swelling, rash"
data = {"message": message}
response = requests.post(url, json=data, headers=headers)
print("Raw response:", response.text)  # Debugging-Ausgabe

try:
    json_response = response.json()
    print("Parsed JSON:", json_response)
except ValueError as e:
    print("JSON Parsing Error:", e)
if response.status_code == 200:
    print("Chatbot reply:", response.json()["reply"])
else:
    print("Error:", response.json())
