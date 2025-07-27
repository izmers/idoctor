from flask import Flask, request, jsonify
from src.disease_predictor.predict_disease import DiseasePredictor
from src.disease_predictor.parse_input import SymptomExtractor

app = Flask(__name__)
symptom_extractor = SymptomExtractor()
disease_predictor = DiseasePredictor()

@app.route("/predict_disease", methods=["POST"])
def predict_disease():
    """
    Disease predictor

    :return: predicted diseases.
    """
    try:
        data = request.get_json()
        user_message = data.get("message", "")

        if not user_message:
            return jsonify({"error": "No message provided"}), 400

        symptoms = symptom_extractor.extract_symptoms(user_message)
        predicted_diseases = disease_predictor.predict_disease(symptoms)

        return jsonify({"reply": predicted_diseases})

    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5001, debug=True)
