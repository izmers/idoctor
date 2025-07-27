import openai
import json

# Set up OpenAI API key
openai.api_key = "..."


class DiseasePredictor:
    def __init__(self, model_name="gpt-3.5-turbo", temperature=0):
        """
        Initializes the DiseasePredictor with the specified OpenAI model and temperature.

        Args:
            model_name (str): The name of the OpenAI model to use.
            temperature (float): The sampling temperature for responses.
        """
        self.model_name = model_name
        self.temperature = temperature

    def construct_prompt(self, input_symptoms):
        """
        Constructs a prompt for the disease prediction based on user input.

        Args:
            input_symptoms (str): The input symptoms in the list.

        Returns:
            str: A formatted prompt for the model.
        """
        return f"""
                You are a health assistant AI trained to identify potential diseases based on symptoms and risk factors provided by the user.

                Instructions:
                1. Analyze the symptoms described by the user.
                2. Identify up to 3 possible diseases that match the symptoms.
                3. Format your output strictly as a JSON array of disease names (e.g., ["common cold", "flu", "bronchitis"]).
                4. Prioritize the most likely diseases based on the symptoms and arrange them in order of relevance.
                
                User symptoms: {", ".join(input_symptoms)}
                
                Example Output:
                Predicted Disease: ["common cold", "flu", "bronchitis]

                """

    def predict_disease(self, input_symptoms):
        """
        Predicts the disease based on the input symptoms using the specified OpenAI model.

        Args:
            input_symptoms (str): The input list containing user input.

        Returns:
            str: The model's response or an error message if an exception occurs.
        """
        try:
            prompt = self.construct_prompt(input_symptoms)

            messages = [{"role": "user", "content": prompt}]
            response = openai.ChatCompletion.create(
                model=self.model_name,
                messages=messages,
                temperature=self.temperature,
            )

            output = response.choices[0].message["content"]
            return json.loads(output)

        except Exception as e:
            return f"An error occurred: {e}"

