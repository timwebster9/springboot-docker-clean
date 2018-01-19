#!groovy

pipeline {
    agent {
        docker {
            image 'maven:3-alpine'
            label 'swarm'
        }
    }
    options {
        tcpForwardLog()
    }
    stages {
        stage('Build') {
            steps {
                sh 'mvn -B clean package'
            }
        }
    }
}
