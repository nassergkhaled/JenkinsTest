pipeline {
    agent any

    options {
        timeout(time: 1, unit: 'HOURS') // Adjust the timeout as needed
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
                withCredentials([
                    sshUserPrivateKey(credentialsId: '59142325-8542-4f3d-994e-ec4d8e1de660', keyFileVariable: 'SSH_KEY_VM1', passphraseVariable: 'SSH_PASSPHRASE'),
                    usernameColonPassword(credentialsId: '35f3ae36-d817-421f-9548-2a24a2223bc7', variable: 'AZURE_VM_USERNAME')
                ]) {
                    script {
                        sh """
                        echo '\$SSH_KEY_VM1' > ssh_key.pem
                        chmod 600 ssh_key.pem
                        ssh -i ssh_key.pem \$AZURE_VM_USERNAME@74.249.98.141 'docker stop my-spring-app || true'
                        ssh -i ssh_key.pem \$AZURE_VM_USERNAME@74.249.98.141 'docker rm my-spring-app || true'
                        docker save my-spring-app | gzip | \
                            ssh -i ssh_key.pem \$AZURE_VM_USERNAME@74.249.98.141 'gunzip | docker load'
                        ssh -i ssh_key.pem \$AZURE_VM_USERNAME@74.249.98.141 'docker run -d -p 8081:8081 --name my-spring-app my-spring-app'
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
