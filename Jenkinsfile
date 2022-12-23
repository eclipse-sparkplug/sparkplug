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
    image: cirruslink/sparkplug-build:latest
    command:
    - cat
    tty: true
    resources:
      limits:
        memory: "4Gi"
        cpu: "1"
      requests:
        memory: "4Gi"
        cpu: "1"
  - name: jnlp
    volumeMounts:
    - mountPath: "/home/jenkins/.gnupg"
      name: "jenkins-home-gnupg"
      readOnly: false
  volumes:
  - name: "jenkins-home-gnupg"
    emptyDir:
      medium: ""
"""
    }
  }

  stages {
    stage('build') {
      steps {
        container('sparkplug-build') {
          sh 'Xvfb :0 -screen 0 1600x1200x16 & export DISPLAY=:0'
          sh 'GRADLE_USER_HOME="/home/jenkins/.gradle" ./gradlew -Dorg.gradle.jvmargs="-Xmx1536m -Xms64m -Dfile.encoding=UTF-8 -Djava.awt.headless=true" clean build'
        }
      }
    }

    stage('sign') {
      steps {
        withCredentials([
          [$class: 'FileBinding', credentialsId: 'secret-subkeys.asc', variable: 'KEYRING'],
          [$class: 'StringBinding', credentialsId: 'gpg-passphrase', variable: 'KEYRING_PASSPHRASE']
        ]) {
          sh '''
            curl -o tck/build/hivemq-extension/sparkplug-tck-3.0.0-signed.jar -F file=@tck/build/hivemq-extension/sparkplug-tck-3.0.0.jar https://cbi.eclipse.org/jarsigner/sign
            export GPG_TTY=/dev/console

            gpg --batch --import "${KEYRING}"
            for fpr in $(gpg --list-keys --with-colons  | awk -F: \'/fpr:/ {print $10}\' | sort -u); do echo -e "5\ny\n" |  gpg --batch --command-fd 0 --expert --edit-key ${fpr} trust; done

            mkdir tck/build/hivemq-extension/working_tmp
            cd tck/build/hivemq-extension/working_tmp
            unzip ../sparkplug-tck-3.0.0.zip
            mv ../sparkplug-tck-3.0.0-signed.jar sparkplug-tck/sparkplug-tck-3.0.0.jar
            zip -r ../sparkplug-tck-3.0.0.zip sparkplug-tck
            cd ..
            gpg -v --no-tty --passphrase "${KEYRING_PASSPHRASE}" -c --batch sparkplug-tck-3.0.0.zip

            echo "no-tty" >> ~/.gnupg/gpg.conf
            gpg -vvv --no-permission-warning --output "sparkplug-tck-3.0.0.zip.sig" --batch --yes --pinentry-mode=loopback --passphrase="${KEYRING_PASSPHRASE}" --no-tty --detach-sig sparkplug-tck-3.0.0.zip
            cd ../../
            ./package.sh
            gpg -vvv --no-permission-warning --output "Eclipse-Sparkplug-TCK-3.0.0.zip.sig" --batch --yes --pinentry-mode=loopback --passphrase="${KEYRING_PASSPHRASE}" --no-tty --detach-sig Eclipse-Sparkplug-TCK-3.0.0.zip
            gpg -vvv --verify Eclipse-Sparkplug-TCK-3.0.0.zip.sig
          '''
        }
      }
    }

    stage('upload') {
      steps {
        sshagent(credentials: ['projects-storage.eclipse.org-bot-ssh']) {
          sh '''
            ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -o BatchMode=yes genie.sparkplug@projects-storage.eclipse.org rm -rf /home/data/httpd/download.eclipse.org/sparkplug/*
            ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -o BatchMode=yes genie.sparkplug@projects-storage.eclipse.org mkdir -p /home/data/httpd/download.eclipse.org/sparkplug/3.0.0
            scp -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -o BatchMode=yes tck/Eclipse-Sparkplug-TCK-3.0.0.zip genie.sparkplug@projects-storage.eclipse.org:/home/data/httpd/download.eclipse.org/sparkplug/3.0.0/
            scp -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -o BatchMode=yes tck/Eclipse-Sparkplug-TCK-3.0.0.zip.sig genie.sparkplug@projects-storage.eclipse.org:/home/data/httpd/download.eclipse.org/sparkplug/3.0.0/
          '''
        }
      }
    }
  }
}
