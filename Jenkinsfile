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
    def sshKeyPath = "/var/lib/jenkins/workspace/JenkinsFirstTest/ssh_key.pem"
    def azureVmHost = "74.249.98.141"
    def azureUsername = env.AZURE_VM_USERNAME  // Assumes AZURE_VM_USERNAME is available in environment

    def dockerStopCmd = "docker stop my-spring-app || true"
    def dockerRmCmd = "docker rm my-spring-app || true"
    def dockerSaveCmd = "docker save my-spring-app | gzip"
    def dockerLoadCmd = "gunzip | docker load"
    def dockerRunCmd = "docker run -d -p 8081:8081 --name my-spring-app my-spring-app"

    // SSH command for stopping the Docker container
    sh "ssh -i ${sshKeyPath} ${azureUsername}@${azureVmHost} '${dockerStopCmd}'"

    // SSH command for removing the Docker container
    sh "ssh -i ${sshKeyPath} ${azureUsername}@${azureVmHost} '${dockerRmCmd}'"

    // SSH command for saving and loading Docker image
    sh "ssh -i ${sshKeyPath} ${azureUsername}@${azureVmHost} '${dockerSaveCmd} | ${dockerLoadCmd}'"

    // SSH command for running the Docker container
    sh "ssh -i ${sshKeyPath} ${azureUsername}@${azureVmHost} '${dockerRunCmd}'"
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
