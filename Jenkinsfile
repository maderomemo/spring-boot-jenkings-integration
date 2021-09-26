pipeline {
  agent any
    stages {
        stage('clone') {
            steps {
                git 'https://github.com/maderomemo/spring-boot-jenkings-integration.git'

            }
        }
        stage('compile') {
            sh 'docker run -i --rm --name todo-api-compile -v "%cd%":/usr/src/app -w /usr/src/app maven:3.5.2-jdk-8-alpine mvn clean package -DskipTests'
        }
        stage('unit tests') {
            sh 'docker run -i --rm --name todo-api-test -v "%cd%":/usr/src/app -w /usr/src/app maven:3.5.2-jdk-8-alpine mvn test -P test'
        }
    }
}