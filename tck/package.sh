#!/bin/sh
###################################################################################
# Copyright (c) 2022 Wes Johnson
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License 2.0 which is available at
# http://www.eclipse.org/legal/epl-2.0.
#
# SPDX-License-Identifier: EPL-2.0
#
# Contributors:
#   Ian Craggs - initial implementation in Python (package.py)
#   Wes Johnson - initial shell implementation derived from package.py
###################################################################################

FILES="build/coverage-report/coverage-sparkplug.html
build/coverage-report/images/stickynote.png
build/coverage-report/images/blank.png
eftckl-v10
build/hivemq-extension/sparkplug-tck-3.0.1-SNAPSHOT.zip
build/hivemq-extension/sparkplug-tck-3.0.1-SNAPSHOT.zip.sig
hivemq-configuration/logback.xml
hivemq-configuration/config.xml
UserGuide.html
README.md
README.html"


ZIP_FILE_NAME=Eclipse-Sparkplug-TCK-3.0.1-SNAPSHOT.zip
PREFIX=build/SparkplugTCK/

# Delete the old version
rm -f ${ZIP_FILE_NAME}

# Update the UserGuide.html doc
asciidoc UserGuide.adoc

# Clean out and create the staging directory
rm -fr ${PREFIX}
mkdir ${PREFIX}

# get the webconsole directory except the node_modules subdir
WEBCONSOLE_FILES=`find webconsole | grep -v node_modules | grep -v layouts | grep -v dist | grep -v status | grep -v static | grep -v middleware | grep -v store | grep -v plugins | grep -v "\.nuxt" | grep -v "\.gitignore" | grep -v "\.editorconfig"`

for FILE in $FILES ; do
    rsync -R $FILE ${PREFIX}
done

for FILE in $WEBCONSOLE_FILES ; do
    if [ x${FILE} == "xwebconsole" ] ; then
        continue
    fi

    rsync -R $FILE ${PREFIX}
done

BUILD_FILES=`ls -1 ${PREFIX}build`
for FILE in $BUILD_FILES ; do
    mv ${PREFIX}build/$FILE ${PREFIX}
done
rmdir ${PREFIX}build

cd build
zip -r ../${ZIP_FILE_NAME} SparkplugTCK/
