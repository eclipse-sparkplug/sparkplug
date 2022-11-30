#*******************************************************************************
# Copyright (c) 2020 Eclipse Foundation and others.
# This program and the accompanying materials are made available
# under the terms of the Eclipse Public License 2.0
# which is available at http://www.eclipse.org/legal/epl-v20.html,
# or the MIT License which is available at https://opensource.org/licenses/MIT.
# SPDX-License-Identifier: EPL-2.0 OR MIT
#*******************************************************************************
FROM eclipsecbi/jiro-agent-centos-8-jdk11 AS builder

USER root

RUN dnf install -y java-11-openjdk-devel \
    graphviz

RUN wget https://services.gradle.org/distributions/gradle-7.6-bin.zip -P /tmp
RUN mkdir /opt/gradle
RUN unzip -d /opt/gradle /tmp/gradle-7.6-bin.zip
RUN echo "export PATH=$PATH:/opt/gradle/gradle-7.6/bin" >> /home/jenkins/.bashrc

USER 10001
