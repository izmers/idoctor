import pytest
from spacy.matcher import PhraseMatcher
import spacy
from src.chatbot.parse_input import SymptomExtractor

# Mock variable for POSSIBLE_SYMPTOMS
import src.chatbot.variables as var
var.POSSIBLE_SYMPTOMS = ["fever", "cough", "headache", "fatigue"]

@pytest.fixture
def extractor():
    """
    Fixture to initialize the SymptomExtractor for tests.
    """
    return SymptomExtractor()

def test_initialization(extractor):
    """
    Test that the SymptomExtractor initializes correctly.
    """
    assert isinstance(extractor.nlp, spacy.language.Language)
    assert isinstance(extractor.matcher, PhraseMatcher)

def test_extract_symptoms_valid(extractor):
    """
    Test symptom extraction from a valid input text.
    """
    text = "I am having a fever and cough."
    expected = ["fever", "cough"]
    result = extractor.extract_symptoms(text)
    assert result == expected

def test_extract_symptoms_no_matches(extractor):
    """
    Test symptom extraction when no symptoms are present in the text.
    """
    text = "I am healthy."
    expected = []
    result = extractor.extract_symptoms(text)
    assert result == expected

def test_extract_symptoms_empty_text(extractor):
    """
    Test symptom extraction with empty input text.
    """
    text = ""
    expected = []
    result = extractor.extract_symptoms(text)
    assert result == expected

