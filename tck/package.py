#!/bin/python3
"""********************************************************************************
 * Copyright (c) 2022, 2023 Ian Craggs
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Ian Craggs - initial implementation
 ********************************************************************************"""

import zipfile, glob, os, sys

files = \
["build/coverage-report/",
"eftckl-v10",
"build/hivemq-extension/sparkplug-tck-3.0.1-SNAPSHOT.zip",
"build/hivemq-extension/sparkplug-tck-3.0.1-SNAPSHOT.zip.sig",
"hivemq-configuration/",
"report.py",
"UserGuide.html",
"README.md",
"README.html",
"src/main/java/org/eclipse/sparkplug/tck/test/broker/CompliantBrokerTest.java",
"src/main/java/org/eclipse/sparkplug/tck/test/broker/test/BrokerAwareFeatureTester.java",
"src/main/java/org/eclipse/sparkplug/tck/test/broker/test/BrokerConformanceFeatureTester.java",
"src/main/java/org/eclipse/sparkplug/tck/test/broker/test/results/ComplianceTestResult.java",
"src/main/java/org/eclipse/sparkplug/tck/test/broker/test/results/AwareTestResult.java",
"src/main/java/org/eclipse/sparkplug/tck/test/broker/test/results/SharedSubscriptionTestResult.java",
"src/main/java/org/eclipse/sparkplug/tck/test/broker/test/results/TopicLengthTestResults.java",
"src/main/java/org/eclipse/sparkplug/tck/test/broker/test/results/QosTestResult.java",
"src/main/java/org/eclipse/sparkplug/tck/test/broker/test/results/AsciiCharsInClientIdTestResults.java",
"src//main/java/org/eclipse/sparkplug/tck/test/broker/test/results/WildcardSubscriptionsTestResult.java",
"src//main/java/org/eclipse/sparkplug/tck/test/broker/test/results/ClientIdLengthTestResults.java",
"src//main/java/org/eclipse/sparkplug/tck/test/broker/test/results/PayloadTestResults.java",
"src//main/java/org/eclipse/sparkplug/tck/test/broker/AwareBrokerTest.java",
"src//main/java/org/eclipse/sparkplug/tck/test/host/MessageOrderingTest.java",
"src//main/java/org/eclipse/sparkplug/tck/test/host/SessionTerminationTest.java",
"src//main/java/org/eclipse/sparkplug/tck/test/host/SessionEstablishmentTest.java",
"src//main/java/org/eclipse/sparkplug/tck/test/host/EdgeSessionTerminationTest.java",
"src//main/java/org/eclipse/sparkplug/tck/test/host/SendCommandTest.java",
"src//main/java/org/eclipse/sparkplug/tck/test/host/MultipleBrokerTest.java",
"src//main/java/org/eclipse/sparkplug/tck/test/edge/SendDataTest.java",
"src//main/java/org/eclipse/sparkplug/tck/test/edge/SessionTerminationTest.java",
"src//main/java/org/eclipse/sparkplug/tck/test/edge/SendComplexDataTest.java",
"src//main/java/org/eclipse/sparkplug/tck/test/edge/SessionEstablishmentTest.java",
"src//main/java/org/eclipse/sparkplug/tck/test/edge/ReceiveCommandTest.java",
"src//main/java/org/eclipse/sparkplug/tck/test/edge/PrimaryHostTest.java",
"src//main/java/org/eclipse/sparkplug/tck/test/edge/MultipleBrokerTest.java",
"src//main/java/org/eclipse/sparkplug/tck/test/common/Requirements.java",
"src//main/java/org/eclipse/sparkplug/tck/test/TCKTest.java",
"src//main/java/org/eclipse/sparkplug/tck/test/Monitor.java"
]

zipfilename = "Eclipse-Sparkplug-TCK-3.0.1-SNAPSHOT.zip"
prefix = "SparkplugTCK/"

try:
    os.remove(zipfilename)
except:
    pass

# generate signature for HiveMQ extension file - this needs to be run with the correct gpg identity
os.system("gpg --batch --yes --detach-sign build/hivemq-extension/sparkplug-tck-3.0.1-SNAPSHOT.zip")

# Update the UserGuide.html doc
os.system("asciidoc UserGuide.adoc")

# update the tck jar notices directory
jarfilename = "build/hivemq-extension/sparkplug-tck-3.0.1-SNAPSHOT.jar"

# get the webconsole directory except the node_modules subdir
webconsole_files = glob.glob("webconsole/*")
ignores = ["node_modules", "layouts", "dist", "static", "middleware", "store", "plugins"]

for file in webconsole_files:
    for ignore in ignores:
        if file.find(ignore) != -1:
            break
    else:
        if file.find(".") == -1:
            file = file + "/"
        files.append(file)

def zipwrite(entry, tckzip):
    # remove the build prefix if there is any
    if entry.startswith("build/"):
        arcname = entry[6:]
    else:
        arcname = entry
    tckzip.write(entry, prefix + arcname)

with zipfile.ZipFile(zipfilename, "w", compression=zipfile.ZIP_DEFLATED) as tckzip:
    for entry in files:

        if type(entry) == type((0,)):
            entry, newname = entry

        if entry.endswith("/"):
            files = glob.glob(entry+"**", recursive=True)
            for file in files:
                zipwrite(file, tckzip)
        else:
            zipwrite(entry, tckzip)

    tckzip.close()

    #print(tckzip.namelist())
