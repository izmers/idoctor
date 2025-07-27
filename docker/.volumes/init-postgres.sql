CREATE TABLE diseases (
    id INT PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE symptoms (
    id INT PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE disease_symptoms (
    disease_id INT,
    symptom_id INT,
    FOREIGN KEY (disease_id) REFERENCES diseases(id),
    FOREIGN KEY (symptom_id) REFERENCES symptoms(id)
);
