pipeline {
  agent any
    stages {
        stage('compile and save files') {
            bat 'docker run -i --rm --name maven-image -v "%cd%":/usr/src/app -w /usr/src/app maven:3.5.2-jdk-8-alpine mvn clean package -DskipTests'
        }
        stage('unit tests') {
            bat 'docker run -i --rm --name my-maven-test -w /usr/src/app maven:3.5.2-jdk-8-alpine mvn test -P test'
        }
    }
}