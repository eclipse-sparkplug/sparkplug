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

def getSpecAssertion(iterator):
    curline = next(iterator)
    curline = next(iterator)
    return curline.strip().strip(")\t").split()[2]

def getSetResultIfNotFail(curline, iterator):
    item = None
    idpos = curline.find("ID_")
    if idpos != -1:
        item = curline[idpos:].split()[0].strip(",")
    else:
        curline = next(iterator, None)
        if curline.find("ID_") == -1:
            curline = next(iterator, None)
        item = curline.split(",")[0].strip().strip(",")
        #print("888", item)
    return item

def process(filename):
    print("Processing file", filename)
    infile = open(filename)
    lines = infile.readlines()

    results = set([])
    testIds = []
    specassertions = set([])
    iterator = iter(lines)
    curline = next(iterator)
    while curline:
        curline = curline.strip()

        if curline.find("List<String> testIds =") != -1:
            testIds = getTestIds(curline, iterator)

        elif curline.find("SpecAssertion(") != -1:
            specassertions.add(getSpecAssertion(iterator))

        elif curline.find("testResults.put(") != -1:
            results.add(curline.split("(")[1].split(",")[0])

        elif curline.find("setResultIfNotFail(") != -1:
            result = getSetResultIfNotFail(curline, iterator)
            if result not in testIds:
                print("result", result, "not in testids")
            else:
                results.add(result)  

        elif curline.find("setShouldResultIfNotFail(") != -1:
            result = getSetResultIfNotFail(curline, iterator)
            if result not in testIds:
                print("result", result, "not in testids")
            else:
                results.add(result)  

        elif curline.find("setShouldResult(testResults") != -1:
            result = getSetResultIfNotFail(curline, iterator)
            if result not in testIds:
                print("result", result, "not in testids")
            else:
                results.add(result)  

        elif curline.find("setResult(testResults") != -1:
            result = getSetResultIfNotFail(curline, iterator)
            if result not in testIds:
                print("result", result, "not in testids")
            else:
                results.add(result)  

        curline = next(iterator, None)
    infile.close()

    if len(specassertions) != len(testIds):
        print(filename, "testids not matched")

        items = []
        for item in specassertions:
            if item not in testIds:
                items.append(item)
        if items != []:
            print("Assertions missing from testIds", items)

        items = []
        for item in testIds:
            if item not in specassertions:
                items.append(item)
        if items != []:
            print("TestIds missing from assertions", items)

    if len(results) != len(specassertions):
        missing = []
        for id in specassertions:
            if id not in results:
                missing.append(id)
        if missing != []:
            print("Results missing", missing)

        missing = []
        for id in results:
            if id not in specassertions:
                missing.append(id)
        if missing != []:
            print("Specassertions missing", missing)

files = glob.glob("**/*Test.java", recursive=True)

for file in files:
    if not file.endswith("TCKTest.java"):
        process(file)





