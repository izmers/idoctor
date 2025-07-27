from flask import Flask, request, jsonify
import requests


app = Flask(__name__)
disease_predictor_service_url = "http://127.0.0.1:5001/predict_disease"
doctor_type_search_service_url = "http://127.0.0.1:5002/doctor_search"
headers = {"Content-Type": "application/json"}

@app.route("/chatbot", methods=["POST"])
def chatbot():
    """
    Chatbot for getting predicted diseases and possible doctor types that
    can help

    :return: predicted diseases together with doctor types
    """

    try:
        data = request.get_json()
        user_message = data.get("message", "")

        if not user_message:
            return jsonify({"error": "No message provided"}), 400

        # Predict diseases
        data_input_disease_predictor = {"message": user_message}
        disease_predictor_response = requests.post(disease_predictor_service_url,
                                                   json=data_input_disease_predictor, headers=headers)

        # Get doctor types to cure diseases
        data_input_doctor_types = {"message": disease_predictor_response.json()["reply"]}
        doctor_types_diseases = requests.post(doctor_type_search_service_url,
                                              json=data_input_doctor_types, headers=headers)

        return jsonify({"reply": doctor_types_diseases.json()["reply"]})

    except Exception as e:
        return jsonify({"error": str(e)}), 500


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5003, debug=True)
