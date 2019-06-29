# Project_Scala

## Run Database
- Method = Get
- url = localhost:8080/startDB

## Test Survey
Ajouter une réponse
- Method = Post
- url = localhost:8080/addAnswer
- body.raw avec format application/json = {"id_user": "98765","response": "0","id_survey": "4321"}

Ajouter une réponse
- Method = Post
- url = localhost:8080/addSurvey
- body.raw avec format application/json = {"survey": "test","answer1": "a","answer2": "x"}

Récupérer un comptage de chaque réponse groupé par question
- Method = Get
- url = localhost:8080/getResume

Obtenir le comptage de chaque réponse d'une question spécifique 
- Method = Post
- url = localhost:8080/getSpeResume
- body.raw avec format application/json = {"id_survey": "1"}
