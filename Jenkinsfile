#!groovy

pipeline {
    agent {
    	docker 'maven:3-alpine'
        label 'swarm'
    }
    stages {
        stage('Build') {
            steps {
                sh 'mvn -B clean package'
            }
        }
    }
}
