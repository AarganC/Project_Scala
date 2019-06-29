# Project_Scala

## Run Database
- Method = Get
- url = localhost:8080/startDB

## Test Giveaways
Créer un giveaway
- Method = Post
- url = localhost:8080/subscribeGiveaway
- body.raw avec format application/json = {"id_user": "4","id_giveaway": "10"}

S'inscrire à un giveaway pour un utilisateur
- Method = Post
- url = localhost:8080/subscribeGiveaway
- body.raw avec format application/json = {"id_user": "1","id_giveaway": "2"}

Tirage au sort du gagnant (aléatoirement pondéré par le montant donné par
l’utilisateur..., s’assurer que l’utilisateur n’est pas ban)
- Method = Post
- url = localhost:8080/giveawayDraw
- body.raw avec format application/json = 

## Test Blacklist
Blacklister un user
- Method = Put
- url = localhost:8080/blacklistUser
- body.raw avec format application/json = {"id_user": "2"}

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

## Test Tips
Récupérer la liste de tous les donateurs (liste de users)
- Method = Get
- url = localhost:8080/getAllUser

Réaliser un don
- Method = Post
- url = localhost:8080/AddTips
- body.raw avec format application/json = {"id_user": "1","somme": "25"}

Annuler un don
- Method = Post
- url = localhost:8080/DeleteTips
- body.raw avec format application/json = {"id_tips": "1"}

Faire la somme de tous les dons
- Method = Get
- url = localhost:8080/getSumDonation

Faire la somme de tous les dons par utilisateur
- Method = Get
- url = localhost:8080/getSumDonationUser

Récupérer la liste de tous les abonnées (liste de users)
- Method = Get
- url = localhost:8080/getSumDonationUser

## PostMan
Vous pouvez récupérer nos commandes postman sur l'url suivant :
https://www.getpostman.com/collections/95b7c5e165e77f62eb4c

## SBT Build Fat Jar or Uber Jar
```sbt clean assembly```

## Run the API
```java -cp target/scala-2.12/Project_Scala-assembly-0.1.jar Main```
