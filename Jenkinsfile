pipeline {

    /agent {
        docker {
            image 'maven:3.5.2-jdk-8-alpine'
            args '-u root'
        }
    }

    triggers {
        pollSCM '* * * * *'
    }*/

    stages {
        /*stage('clone') {
            steps {
                git 'https://github.com/maderomemo/spring-boot-jenkings-integration.git'
            }
        }
        stage('compile') {
            steps {
                sh 'mvn clean package -DskipTests -q'
            }
        }*/
        /*stage('unit tests') {
            steps {
                sh 'docker run -i --rm --name todo-api-test -v "$(pwd)":/usr/src/app -w /usr/src/app maven:3.5.2-jdk-8-alpine mvn test -P test -q'
            }
        }*/
        stage('build image') {
            steps {
                sh 'docker build -t todo-api .'
            }
        }
    }
}