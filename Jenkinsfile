pipeline {
    agent any

    options {
        timeout(time: 1, unit: 'HOURS') // Adjust the timeout as needed
    }

    environment {
        AZURE_VM_IP_ADDRESS = "74.249.98.141" // Replace with the actual IP address
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Create SSH Directory and known_hosts') {
            steps {
                script {
                    def sshDir = "/var/lib/jenkins/.ssh"
                    def knownHostsFile = "${sshDir}/known_hosts"
                    
                    // Ensure the SSH directory exists
                    sh "mkdir -p ${sshDir}"
                    
                    // Create an empty known_hosts file
                    sh "touch ${knownHostsFile}"
                }
            }
        }

        stage('Add Azure VM Host Key to known_hosts') {
            steps {
                script {
                    def sshDir = "/var/lib/jenkins/.ssh"
                    def knownHostsFile = "${sshDir}/known_hosts"
                    def azureIp = env.AZURE_VM_IP_ADDRESS
                    
                    // Use ssh-keyscan to fetch the host key and append it to known_hosts
                    sh "ssh-keyscan ${azureIp} >> ${knownHostsFile}"
                }
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
                        def azureIp = env.AZURE_VM_IP_ADDRESS
                        
                        // Stop the Docker container
                        sh "ssh -i \$SSH_KEY_VM1 \$AZURE_VM_USERNAME@\$azureIp 'docker stop my-spring-app || true'"
                        
                        // Remove the Docker container
                        sh "ssh -i \$SSH_KEY_VM1 \$AZURE_VM_USERNAME@\$azureIp 'docker rm my-spring-app || true'"
                        
                        // Save and load the Docker image
                        sh "docker save my-spring-app | gzip | ssh -i \$SSH_KEY_VM1 \$AZURE_VM_USERNAME@\$azureIp 'gunzip | docker load'"
                        
                        // Run the Docker container
                        sh "ssh -i \$SSH_KEY_VM1 \$AZURE_VM_USERNAME@\$azureIp 'docker run -d -p 8081:8081 --name my-spring-app my-spring-app'"
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
