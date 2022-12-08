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
    resources:
      limits:
        memory: "2Gi"
        cpu: "1"
      requests:
        memory: "2Gi"
        cpu: "1"
"""
    }
  }
  stages {
    stage('Build') {
      steps {
        container('sparkplug-build') {
          wrap([$class: 'Xvnc', takeScreenshot: false, useXauthority: true]) {
            sh './gradlew clean build'
          }
        }
      }
    }
  }
}
