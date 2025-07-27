import re
import src.disease_predictor.variables as var


class SymptomExtractor:
    def __init__(self):
        """
        Initializes the SymptomExtractor.
        """
        self.symptoms = set(var.POSSIBLE_SYMPTOMS)  # Convert to a set for faster lookups

    def extract_symptoms(self, text):
        """
        Extracts symptoms from user input.

        Args:
            text (str): The input text containing symptoms.

        Returns:
            list: Extracted symptoms.
        """
        words = re.findall(r'\b\w+\b', text.lower())  # Extract words from text
        extracted_symptoms = [symptom for symptom in self.symptoms if symptom in words]
        return extracted_symptoms
