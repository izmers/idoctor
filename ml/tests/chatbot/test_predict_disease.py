import pytest
from unittest.mock import patch, MagicMock
import json
from src.chatbot.predict_disease import DiseasePredictor


@pytest.fixture
def predictor():
    """
    Fixture to initialize the DiseasePredictor.
    """
    return DiseasePredictor(model_name="gpt-3.5-turbo", temperature=0)


def test_initialization(predictor):
    """
    Test that the DiseasePredictor initializes correctly.
    """
    assert predictor.model_name == "gpt-3.5-turbo"
    assert predictor.temperature == 0


def test_construct_prompt(predictor):
    """
    Test that the construct_prompt method creates the correct prompt.
    """
    input_symptoms = ["fever", "cough", "headache"]
    expected_prompt = f"""
                You are a health assistant AI trained to identify potential diseases based on symptoms and risk factors provided by the user.

                Instructions:
                1. Analyze the symptoms described by the user.
                2. Identify up to 5 possible diseases that match the symptoms.
                3. Format your output strictly as a JSON array of disease names (e.g., ["common cold", "flu", "bronchitis", "sinusitis", "streptococcal pharyngitis"]).
                4. Prioritize the most likely diseases based on the symptoms and arrange them in order of relevance.

                User symptoms: fever, cough, headache

                Example Output:
                Predicted Disease: ["common cold", "flu", "bronchitis", "sinusitis", "streptococcal pharyngitis"]

                """
    result = predictor.construct_prompt(input_symptoms)

    assert ''.join(result.split()) == ''.join(expected_prompt.split())


@patch("openai.ChatCompletion.create")
def test_predict_disease_valid_response(mock_openai, predictor):
    """
    Test the predict_disease method with a valid response from the OpenAI API.
    """
    input_symptoms = ["fever", "cough"]
    mock_response = MagicMock()
    mock_response.choices = [
        MagicMock(message={"content": json.dumps(["common cold", "flu", "bronchitis"])})
    ]
    mock_openai.return_value = mock_response

    result = predictor.predict_disease(input_symptoms)
    print(result)
    assert result == ["common cold", "flu", "bronchitis"]


@patch("openai.ChatCompletion.create")
def test_predict_disease_error_response(mock_openai, predictor):
    """
    Test the predict_disease method when the OpenAI API raises an exception.
    """
    input_symptoms = ["fever", "cough"]
    mock_openai.side_effect = Exception("API error")

    result = predictor.predict_disease(input_symptoms)
    assert "An error occurred: API error" in result


def test_predict_disease_invalid_output(predictor):
    """
    Test the predict_disease method when the API returns an invalid JSON.
    """
    with patch("openai.ChatCompletion.create", return_value={
        "choices": [{"message": {"content": "Invalid JSON"}}]
    }):
        input_symptoms = ["fever", "cough"]
        result = predictor.predict_disease(input_symptoms)
        assert "An error occurred" in result
