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
        sh 'docker build -t my-spring-app . --build-arg SSH_KEY=/var/lib/jenkins/workspace/JenkinsFirstTest/ssh_key.pem --build-arg BUILDKIT_INLINE_CACHE=1'
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
docker save my-spring-app -o my-spring-app.tar
scp -i /var/lib/jenkins/ssh_key my-spring-app.tar azureuser@74.249.98.141:~/my-spring-app.tar
ssh -i /var/lib/jenkins/ssh_key azureuser@74.249.98.141 'sudo docker stop my-spring-app || true'  # Stop the existing container
ssh -i /var/lib/jenkins/ssh_key azureuser@74.249.98.141 'sudo docker rm my-spring-app || true'  # Remove the existing container
ssh -i /var/lib/jenkins/ssh_key azureuser@74.249.98.141 'sudo docker load -i ~/my-spring-app.tar'
ssh -i /var/lib/jenkins/ssh_key azureuser@74.249.98.141 'sudo docker run -d -p 8081:8081 --name my-spring-app my-spring-app'
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
