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
          sh 'Xvfb :0 -screen 0 1600x1200x16 & export DISPLAY=:0'
          sh 'GRADLE_USER_HOME="/home/jenkins/.gradle" ./gradlew -Dorg.gradle.jvmargs="-Xmx1536m -Xms64m -Dfile.encoding=UTF-8 -Djava.awt.headless=true" clean build'
        }
      }
    }
  }
}
