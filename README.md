# intellistart-java-2022-Propositum

[![build](https://github.com/Wectro20/intellistart-java-2022-propositum/actions/workflows/maven.yml/badge.svg)](https://github.com/Wectro20/intellistart-java-2022-propositum/actions/workflows/maven.yml)

## Overview
* [Project information](#project-information)
* [How to Run](#how-to-run)
* [API](#api)
* [SQL Diagram](#sql-diagram)


### Project information
RESTful API for interview planning application, which simplifies the process of time management and coordinating between interviewers and candidates.
#### Application functionality
- Creating Interviewer's slots
- Creating Candidate's slots
- Creating booking for Interviewer/Candidate slots which already exists
### How to Run

#### How to Run manualy

This application is packaged as a jar which has Tomcat 8 embedded. No Tomcat or JBoss installation is necessary. You run it using the ```java -jar``` command.

* Clone this repository
* Make sure you are using JDK 1.11 and Maven 3.x
* Make sure you are using MySQL 8.0
* Create Mysql database 
``` 
create database "your_database_name"
```
*  Change mysql username, password and datasource as per your installation
   - open `src/main/resources/application.properties`
   - change `spring.datasource.username` , `spring.datasource.password` and `spring.datasource.url` as per your mysql installation
   - add your own jwt in `jwt:` must be 30 or more characters (lower/uppercase) without using special symbols

* You can build the project and run the tests by running ```mvn clean package```
* Once successfully built, you can run the project by this method:
```
java -jar target/interview-planning-0.0.1-SNAPSHOT.jar
```
The app will start running at http://localhost:8080.

#### How to Run using docker
* Clone this repository
* Make sure you are having docker
* Add your JWT in `.env` file - must be 30 or more characters (lower/uppercase) without using special symbols,</br> also you can change docker application properties there (if needed)
* Build the image
``` 
docker-compose build
```
* Run your image
``` 
docker-compose up
```
The app will start running at http://localhost:8080.

### API
#### Explore Rest APIs
The app defines following CRUD APIs.
* Authentication & authorization
For authentication or authorization you had to have Facebook token</br>
`POST /authenticate`</br>
  Necessary data parameter: 
    ```
    {
        "token": "YOUR_TOKEN"
  }
  ```
  Gained JWT should be put in request header as a parameter </br>`Authorization` with value `Bearer "GAINED_JWT"`
* More requests you can check in the postman collection

### SQL Diagram
![SQL_Diagram](https://user-images.githubusercontent.com/56608205/194121497-8d8dc2c9-5a2f-4256-a097-8c3e42e82968.jpg)
