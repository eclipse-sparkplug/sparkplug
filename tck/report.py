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

import glob, sys

def getTestIds(curline, iterator):
    start_found = False
    testIds = []
    while curline:
        curline = curline.strip().strip(",\t")
        if curline.find("(") != -1:
            start_found = True
            ids = curline.split("(")[1].split(",")
            testIds.extend(ids)
        elif not start_found:
            pass
        elif curline.find(");") != -1:
            curline = curline.strip(");")
            testIds.extend(curline.split(","))
            break
        else:
            testIds.extend(curline.split(","))
        curline = next(iterator, None)
    rc = set([])
    for id in testIds:
        rc.add(id.strip())
    if len(rc) != len(testIds):
        for id in testIds:
            if testIds.count(id) > 1:
                print("duplicate", id)
    return rc

def process(filename):
    infile = open(filename)
    lines = infile.readlines()
    iterator = iter(lines)
    curline = next(iterator)
    while curline:
        curline = curline.strip()

        if curline.find("List<String> testIds =") != -1:
            return getTestIds(curline, iterator)

        curline = next(iterator, None)
    infile.close()
    return None



if __name__ == "__main__":

    brokerids = set([])
    hostids = set([])
    edgeids = set([])

    files = glob.glob("**/*Test.java", recursive=True)

    for file in files:
        if not file.endswith("TCKTest.java"):
            ids = process(file)
            if file.find("test/host") != -1:
                hostids.union(ids)
                
    # we have all the testIds for each source file



