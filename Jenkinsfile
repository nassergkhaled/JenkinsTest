pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }



        stage('Build Docker Image') {
            steps {
                sh 'docker build -t my-spring-app .'
            }
        }

       stage('Deploy to Azure VM') {
                   environment {
                       // Define environment variables using GitHub secrets
                       AZURE_VM_IP = credentials('AZURE_VM_IP')
                       AZURE_VM_USERNAME = credentials('AZURE_VM_USERNAME')
                       AZURE_SSH_KEY = credentials('AZURE_SSH_KEY')
                   }
                   steps {
                       // Copy the Docker image to the Azure VM and run it
                       sh """
                       ssh -i ${AZURE_SSH_KEY} ${AZURE_VM_USERNAME}@${AZURE_VM_IP} 'docker stop my-spring-app || true'
                       ssh -i ${AZURE_SSH_KEY} ${AZURE_VM_USERNAME}@${AZURE_VM_IP} 'docker rm my-spring-app || true'
                       docker save my-spring-app | gzip | \
                           ssh -i ${AZURE_SSH_KEY} ${AZURE_VM_USERNAME}@${AZURE_VM_IP} 'gunzip | docker load'
                       ssh -i ${AZURE_SSH_KEY} ${AZURE_VM_USERNAME}@${AZURE_VM_IP} 'docker run -d -p 8081:8081 --name my-spring-app my-spring-app'
                       """
                   }
               }
    }

    post {
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed. Please check the logs for details.'
        }
    }
}



