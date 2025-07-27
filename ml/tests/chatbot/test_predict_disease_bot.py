import pytest
from unittest.mock import patch

from src.chatbot.predict_disease_bot import app, symptom_extractor, disease_predictor


@pytest.fixture
def client():
    """
    Creates a test client for the Flask app.
    """
    with app.test_client() as client:
        yield client

@patch("src.disease_predictor.predict_disease_bot.symptom_extractor.extract_symptoms")
@patch("src.disease_predictor.predict_disease_bot.disease_predictor.predict_disease")
def test_chat_valid_request(mock_predict_disease, mock_extract_symptoms, client):
    """
    Test the /chat endpoint with a valid request.
    """
    mock_extract_symptoms.return_value = ["fever", "cough"]
    mock_predict_disease.return_value = ["flu", "common cold"]

    response = client.post("/chat", json={"message": "I have a fever and a cough"})

    assert response.status_code == 200
    assert response.json == {"reply": ["flu", "common cold"]}


@patch("src.disease_predictor.predict_disease_bot.symptom_extractor.extract_symptoms")
def test_chat_no_message(mock_extract_symptoms, client):
    """
    Test the /chat endpoint when no message is provided.
    """
    response = client.post("/chat", json={})

    assert response.status_code == 400
    assert response.json == {"error": "No message provided"}

@patch("src.disease_predictor.predict_disease_bot.symptom_extractor.extract_symptoms")
def test_chat_server_error(mock_extract_symptoms, client):
    """
    Test the /chat endpoint when an exception occurs.
    """
    mock_extract_symptoms.side_effect = Exception("Something went wrong")

    response = client.post("/chat", json={"message": "I have a fever and a cough"})

    assert response.status_code == 500
    assert response.json == {"error": "Something went wrong"}
