from flask import Flask, request, jsonify
from src.doctor_type_search.get_doctor_type import DoctorType

app = Flask(__name__)
doctor_type_search = DoctorType()

@app.route("/doctor_search", methods=["POST"])
def doctor_search():
    """
    Doctor type search

    :return: map with doctor names and disease.
    """
    try:
        data = request.get_json()
        user_message = data.get("message", "")

        if not user_message:
            return jsonify({"error": "No message provided"}), 400

        doctor_types_diseases = doctor_type_search.get_doctor_type(user_message)

        return jsonify({"reply": doctor_types_diseases})

    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5002, debug=True)
