pipeline {
    agent any

    tools {
        maven 'maven-3.9.14'
        jdk 'java-25'
    }

    environment {
        IMAGE_NAME = "r3venge1337/eam-simulator"
        DOCKER_CREDS = 'docker-hub-credentials'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Docker Build') {
            steps {
                script {
                    sh "docker build -t ${IMAGE_NAME}:${BUILD_NUMBER} ."
                    sh "docker tag ${IMAGE_NAME}:${BUILD_NUMBER} ${IMAGE_NAME}:latest"
                }
            }
        }

       stage('Docker Push') {
                   steps {
                       script {
                           docker.withRegistry('https://index.docker.io/v1/', DOCKER_CREDS) {
                               def img = docker.image("${IMAGE_NAME}:${BUILD_NUMBER}")
                               img.push()
                               img.push('latest')
                           }
                       }
                   }
               }
              stage('Deploy to MicroK8s') {
                          steps {
                              script {
                                  sh "microk8s kubectl apply -f k8s/eam-all.yaml"
                                  sh "microk8s kubectl set image deployment/eam-simulator eam-simulator=${IMAGE_NAME}:${BUILD_NUMBER}"
                                  sh "microk8s kubectl rollout status deployment/eam-simulator"
                              }
                          }
                      }
    }

    post {
        success {
            echo "Sukces! Aplikacja eam-simulator została zbudowana i wypchnięta."
        }
        failure {
            echo "Błąd! Sprawdź logi konsoli."
        }
    }
}