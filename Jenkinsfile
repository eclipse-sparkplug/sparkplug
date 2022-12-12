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
    stage('build') {
      steps {
        container('sparkplug-build') {
          sh 'Xvfb :0 -screen 0 1600x1200x16 & export DISPLAY=:0'
          sh 'GRADLE_USER_HOME="/home/jenkins/.gradle" ./gradlew -Dorg.gradle.jvmargs="-Xmx1536m -Xms64m -Dfile.encoding=UTF-8 -Djava.awt.headless=true" clean packageTck'
        }
      }
    }

    stage('upload') {
      steps {
        sshagent(credentials: ['projects-storage.eclipse.org-bot-ssh']) {
          sh '''
            scp -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -o BatchMode=yes tck/Eclipse-Sparkplug-TCK-3.0.0-signed.zip genie.sparkplug@projects-storage.eclipse.org:/home/data/httpd/download.eclipse.org/sparkplug/
          '''
        }
      }
    }
  }
}