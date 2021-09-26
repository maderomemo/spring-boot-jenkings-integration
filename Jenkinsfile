pipeline {
  agent any
    stages {
        stage('clone') {
            steps {
                git 'https://github.com/maderomemo/spring-boot-jenkings-integration.git'

            }
        }
        stage('compile and test') {
            steps {
                sh 'docker run -i --rm --name todo-api-compile -v "$(pwd)":/usr/src/app -w /usr/src/app maven:3.5.2-jdk-8-alpine mvn clean package -P test'
            }
        }
        stage('build image') {
            steps {
                sh 'docker build -t todo-api .'
            }
        }
    }
}