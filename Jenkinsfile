pipeline {
  agent {
    kubernetes {
      label 'sparkplug-agent-pod'
      yaml """
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: sparkplug-build
    image: cirruslink/sparkplug-build
    command:
    - cat
    tty: true
"""
    }
  }
  stages {
    stage('Build') {
      steps {
        container('sparkplug-build') {
          sh 'gradlew clean build'
        }
      }
    }
  }
}
