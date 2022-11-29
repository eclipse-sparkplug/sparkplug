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
    image: eclipsecbi/jiro-agent-centos-8
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
          sh './gradlew clean build'
        }
      }
    }
  }
}
