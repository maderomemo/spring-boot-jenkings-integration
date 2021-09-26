pipeline {
  agent any
    stages {
        stage('clone') {
            steps {
                git 'https://github.com/maderomemo/spring-boot-jenkings-integration.git'

            }
        }
        stage('compile') {
            steps {
                sh 'docker run -i --rm --name todo-api-compile -v "$(pwd)":/usr/src/app -w /usr/src/app maven:3.5.2-jdk-8-alpine mvn clean package -DskipTests'
            }
        }
        stage('unit tests') {
            steps {
                sh 'docker run -i --rm --name todo-api-test -v "$(pwd)":/usr/src/app -w /usr/src/app maven:3.5.2-jdk-8-alpine mvn test -P test'
            }
        }
    }
}