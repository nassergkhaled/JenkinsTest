pipeline {
    agent any

    options {
        timeout(time: 1, unit: 'HOURS') // Adjust the timeout as needed
    }

    environment {
        AZURE_VM_IP = credentials('AZURE_VM_IP_CREDENTIAL')
    }

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

        stage('Deploy to Remote Azure VM') {
            steps {
                // Use Jenkins credentials for SSH key and Azure VM username
                withCredentials([
                    sshUserPrivateKey(credentialsId: '59142325-8542-4f3d-994e-ec4d8e1de660', keyFileVariable: 'SSH_KEY_VM1'),
                    usernameColonPassword(credentialsId: '35f3ae36-d817-421f-9548-2a24a2223bc7', variable: 'AZURE_VM_USERNAME')
                ]) {
                    script {
                        def azureIp = credentials('AZURE_VM_IP_CREDENTIAL')
                        sh """
                        ssh -i \$SSH_KEY_VM1 \$AZURE_VM_USERNAME@\$azureIp 'docker stop my-spring-app || true'
                        ssh -i \$SSH_KEY_VM1 \$AZURE_VM_USERNAME@\$azureIp 'docker rm my-spring-app || true'
                        docker save my-spring-app | gzip | \
                            ssh -i \$SSH_KEY_VM1 \$AZURE_VM_USERNAME@\$azureIp 'gunzip | docker load'
                        ssh -i \$SSH_KEY_VM1 \$AZURE_VM_USERNAME@\$azureIp 'docker run -d -p 8081:8081 --name my-spring-app my-spring-app'
                        """
                    }
                }
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
