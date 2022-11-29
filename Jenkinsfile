pipeline {
  agent {
    kubernetes {
      label 'my-agent-pod'
      yaml """
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: maven
    image: maven:alpine
    command:
    - cat
    tty: true
  - name: php
    image: php:7.2.10-alpine
    command:
    - cat
    tty: true
  - name: hugo
    image: eclipsecbi/hugo:0.81.0
    command:
    - cat
    tty: true
"""
    }
  }
  stages {
    stage('Run maven') {
      steps {
        container('maven') {
          sh 'mvn -version'
        }
        container('php') {
          sh 'php -version'
        }
        container('hugo') {
          sh 'hugo -version'
        }
      }
    }
  }
}
