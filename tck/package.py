#!/bin/python3
"""********************************************************************************
 * Copyright (c) 2022 Ian Craggs
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
"build/hivemq-extension-test/sparkplug-tck/sparkplug-tck-3.0.0-SNAPSHOT.jar",
"build/hivemq-extension-test/sparkplug-tck/hivemq-extension.xml",
"hivemq-configuration/",
"report.py",
"README.md"
]

zipfilename = "Sparkplug_TCK_3.0.0.zip"
prefix = "Sparkplug/"

try:
    os.remove(zipfilename)
except:
    pass

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

with zipfile.ZipFile(zipfilename, "w", compression=zipfile.ZIP_DEFLATED, compresslevel=9) as tckzip:
    for entry in files:

        if entry.endswith("/"):
            files = glob.glob(entry+"**", recursive=True)
            for file in files:
                zipwrite(file, tckzip)
        else:
            zipwrite(entry, tckzip)

    tckzip.close()

    #print(tckzip.namelist())
