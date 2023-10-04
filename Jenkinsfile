pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and Test') {
            steps {
                sh 'mvn clean install'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t my-spring-app .'
            }
        }

        stage('Deploy to EC2') {
            environment {
                // Define your EC2 instance credentials and details here
                AWS_ACCESS_KEY_ID = credentials('AKIAWIFT7KU6ZNBK2KPD')
                AWS_SECRET_ACCESS_KEY = credentials('2gnt9S8JivR3M2s3emn3n907Dv1n66CYkOgap7Fz')
                EC2_INSTANCE_IP = '54.163.188.194'
            }
            steps {
                // Copy the Docker image to the EC2 instance and run it
                sh """
                export AWS_ACCESS_KEY_ID="${AWS_ACCESS_KEY_ID}"
                export AWS_SECRET_ACCESS_KEY="${AWS_SECRET_ACCESS_KEY}"
                export AWS_DEFAULT_REGION='us-east-1'
                docker save my-spring-app | gzip | \
                    ssh -o StrictHostKeyChecking=no ec2-user@${EC2_INSTANCE_IP} 'gunzip | docker load'
                ssh -o StrictHostKeyChecking=no ec2-user@${EC2_INSTANCE_IP} 'docker run -d -p 8081:8081 my-spring-app'
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
