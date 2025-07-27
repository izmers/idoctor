# ML services


## Overview
There are created 3 API services: chatbot, predict_disease_bot and 
doctor_type_search_bot. Chatbot orchestrates the work of 
predict_disease_bot and doctor_type_search_bot for returning a map with disease and possible doctor
that can help with it. Example output: {'bronchitis': 'pulmonologist', 'influenza': 'primary care physician', 'pneumonia': 'pulmonologist'}

## Set up instructions

1. Set up predict disease bot. Please run such commands:

```
cd src/disease_predictor
python3 predict_disease_bot.py
```

2. In another terminal set up doctor type search bot. Please run such commands:

```
cd src/doctor_type_search
python3 doctor_type_search_bot.py
```

3. Last one set up chatbot. Please run such commands:
```
cd src/chatbot
python3 chatbot.py
```

4. Now you can test functionality using example_usage_ml_integration.py. 
There is provided sample code how to use API.

Note: do not forget to set up Python path to be able to run the code