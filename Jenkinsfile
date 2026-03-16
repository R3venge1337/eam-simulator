pipeline {
    agent any

    tools {
        // Upewnij się, że nazwa 'maven-3.9.14' zgadza się z Global Tool Configuration w Jenkins
        maven 'maven-3.9.14'
        jdk 'java-25'
    }

    environment {
        // Nazwa Twojego obrazu Docker
        IMAGE_NAME = "r3venge1337/eam-simulator"
        // ID poświadczeń Docker Hub zapisanych w Jenkinsie
        DOCKER_CREDS = 'docker-hub-credentials'
    }

    stages {
        stage('Checkout') {
            steps {
                // Pobieranie kodu z GitHuba
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                // Budujemy wszystko z poziomu Rooota
                // -DskipTests przyspieszy build na początku, ale docelowo je włączymy
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Docker Build') {
            steps {
                script {
                    // Budujemy obraz, wskazując na Dockerfile w root
                    // Dockerfile musi kopiować JAR z eam-monolith/target/
                    sh "docker build -t ${IMAGE_NAME}:${BUILD_NUMBER} ."
                    sh "docker tag ${IMAGE_NAME}:${BUILD_NUMBER} ${IMAGE_NAME}:latest"
                }
            }
        }

       stage('Docker Push') {
                   steps {
                       script {
                           // Jawnie podajemy URL do Docker Hub i używamy ID Twoich poświadczeń
                           docker.withRegistry('https://index.docker.io/v1/', DOCKER_CREDS) {
                               def img = docker.image("${IMAGE_NAME}:${BUILD_NUMBER}")

                               // Pushuje tag z numerem buildu
                               img.push()

                               // Pushuje tag 'latest'
                               img.push('latest')
                           }
                       }
                   }
               }
               stage('Deploy to MicroK8s') {
                           steps {
                               script {
                                   // To założy, że Jenkins ma uprawnienia do microk8s kubectl
                                   sh "microk8s kubectl set image deployment/eam-simulator eam-simulator=${IMAGE_NAME}:${BUILD_NUMBER}"
                                   sh "microk8s kubectl rollout status deployment/eam-simulator"
                               }
                           }
                       }
    }

    post {
        success {
            echo "Sukces! Aplikacja eam-simulator została zbudowana i wypchnięta."
            // Tutaj w przyszłości dodamy powiadomienie dla GitHuba (Status Check)
        }
        failure {
            echo "Błąd! Sprawdź logi konsoli."
        }
    }
}