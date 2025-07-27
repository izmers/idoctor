import openai
import json

# Set up OpenAI API key
openai.api_key = "..."


class DoctorType:
    def __init__(self, model_name="gpt-3.5-turbo", temperature=0):
        """
        Initializes the DoctorType with the specified OpenAI model and temperature.

        Args:
            model_name (str): The name of the OpenAI model to use.
            temperature (float): The sampling temperature for responses.
        """
        self.model_name = model_name
        self.temperature = temperature

    def construct_prompt(self, input_diseases):
        """
        Constructs a prompt for the disease prediction based on user input.

        Args:
            input_diseases (str): The input diseases in the list.

        Returns:
            str: A formatted prompt for the model.
        """
        return f"""
                You are a health assistant AI trained to identify doctor type that will help to cure the disease.

                Instructions:
                1. Analyze the list of diseases.
                2. Identify one doctor type for each disease.
                3. Format your output strictly as a map doctor type and disease name (e.g., {{\"COVID-19\": \"infectious disease specialist\",
                 \"common cold\": \"primary care physician\", \"sinusitis\": \"otolaryngologist\"}}).
                4. Return one map that contains disease as a key and doctor as a value
                
                Diseases: {", ".join(input_diseases)}
                
                Example Output:
                Doctor Types with Diseases: {{\"COVID-19\": \"infectious disease specialist\",
                 \"common cold\": \"primary care physician\", \"sinusitis\": \"otolaryngologist\"}} 

                """

    def get_doctor_type(self, input_diseases):
        """
        Gets doctor types for the diseases using the specified OpenAI model.

        Args:
            input_diseases (str): The input list containing diseases names.

        Returns:
            str: The model's response or an error message if an exception occurs.
        """
        try:
            prompt = self.construct_prompt(input_diseases)

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