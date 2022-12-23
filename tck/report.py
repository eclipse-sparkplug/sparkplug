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

import glob, sys, datetime

reqfilename = "src/main/java/org/eclipse/sparkplug/tck/test/common/Requirements.java"

brokerids = set([])
hostids = set([])
edgeids = set([])

brokerresults = {}
hostresults = {}
edgeresults = {}

def getDescriptions():
    reqs = {}
    reqfile = open(reqfilename)
    lines = reqfile.readlines()
    id_string = None
    for curline in lines:
        curline = curline.strip()
        if curline.find("String ID_") != -1:
            id_string = curline.split()[4]
        elif id_string != None and curline.find("String "+ id_string.upper().replace("-", "_")):
            desc_string = curline.split("\"")[1].split(maxsplit=1)[1]
            #print(id_string, desc_string)
            reqs[id_string] = desc_string
            id_string = None
    reqfile.close()
    return reqs

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
        if id.strip() != "":
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

def classify_assertion(assertionid):
    edge_hints = ["EDGE", "DEVICE","NDATA", "DDATA", "DBIRTH", "NBIRTH", "DDEATH", "NDEATH", "RBE"]
    for edge_hint in edge_hints:
        if assertionid.find(edge_hint) != -1:
            return "edge"

    host_hints = ["HOST", "STATE"]
    for host_hint in host_hints:
        if assertionid.find(host_hint) != -1:
            return "host"

    #print("Assertion ", assertionid, "classified as both")
    return None

def setResult(results, assertion_id, test, timestamp, result):
    oldresult = results[assertion_id]
    if oldresult != None and oldresult[2].find("FAIL") != -1:
        #print("Not overwriting failing test result", oldresult, "\nwith", result, "\n")
        pass
    else:
        results[assertion_id] = test, timestamp, result

def process_test(profile, test, timestamp, lines):
    curline = lines.pop(0)
    while curline.find("OVERALL") == -1:
        assertion_id, result = curline.strip(";").split(maxsplit=1)
        if assertion_id.startswith("Monitor:"):
            assertion_id = assertion_id[len("Monitor:"):]
        assertion_id = "ID_"+assertion_id.strip(":").upper().replace("-", "_")
        result = result.strip(";\n")
        if profile == "edge":
            if assertion_id not in edgeids:
                # probably a monitor check
                if assertion_id not in hostids:
                    print("Error: assertion not in hostids or edgeids:", assertion_id, profile, test)
                else:
                    setResult(hostresults, assertion_id, test, timestamp, result)
            else:
                setResult(edgeresults, assertion_id, test, timestamp, result)
        elif profile == "host":
            if assertion_id not in hostids:
                # probably a monitor check
                if assertion_id not in edgeids:
                    print("Error: assertion not in hostids or edgeids:", assertion_id, profile, test)
                else:
                    setResult(edgeresults, assertion_id, test, timestamp, result)
            else:
                setResult(hostresults, assertion_id, test, timestamp, result)
        elif profile == "broker":
            if assertion_id not in brokerids:
                print("Error: assertion not in brokerids:", assertion_id, profile, test)
            else:
                setResult(brokerresults, assertion_id, test, timestamp, result)
        curline = lines.pop(0)


def process_logfile(lines):
    print("Processing logfile...")
    tests = {}
    info = []
    eye = "Summary Test Results for"
    while len(lines) > 0:
        curline = lines.pop(0).strip()
        if curline.find(eye) != -1:
            date, time, rest = curline.split(maxsplit=2)
            after = curline.find(eye) + len(eye)
            profile, test = curline[after:].split(maxsplit=1)
            test = test.replace(" ", "")
            if test in tests.keys():
                logline = "<h4>Warning: test %s %s logged more than once. Any previously failing assertion will not be overwritten by a later success.</h4>" % (profile, test)
                if logline not in info:
                    info.append(logline)
                tests[test].append(date+" "+time)
            else:
                tests[test] = [date+" "+time]
            process_test(profile.lower(), test, date+" "+time, lines)
    return info

def stats(key_list, results):
    count = len(key_list)
    optional_count = 0
    optional_passes = 0
    passes = 0
    fails = 0
    optional_fails = 0
    for result in key_list:
        optional, group = isOptional(result, descs[result])
        if optional:
            optional_count += 1
        if results[result] != None:
            if results[result][-1] == "PASS":
                passes += 1
                if optional:
                    optional_passes += 1
            elif results[result][-1].startswith("FAIL"):
                fails += 1
                if optional:
                    optional_fails += 1
    percent = int(passes/count * 100.0)
    try:
        percent_without_optional = int((passes - optional_passes)/(count - optional_count) * 100.0)
    except:
        percent_without_optional = 0
    return count, passes, fails, percent, optional_count, optional_passes, percent_without_optional

def getType(desc):
    assertion_type = ""
    if desc.find("MUST") != -1:
        assertion_type = "MUST"
    elif desc.find("SHOULD") != -1:
        assertion_type = "SHOULD"
    elif desc.find("MAY") != -1:
        assertion_type = "MAY"
    else:
        print("Error classifying assertion type:", desc)
        sys.exit()
    return assertion_type

def isOptional(assertion_id, desc):
    optional = False
    group = None
    if assertion_id.find("MULTPLE") != -1:
        optional = True
        group = "Multiple Brokers"
    elif assertion_id.find("REORDERING") != -1:
        optional = True
        group = "Message Reordering"
    elif assertion_id.find("TEMPLATE") != -1:
        optional = True
        group = "Templates"
    elif assertion_id.find("PROPERTY") != -1:
        optional = True
        group = "Properties"
    elif assertion_id.find("DATASET") != -1:
        optional = True
        group = "Datasets"
    elif assertion_id.find("ALIAS") != -1:
        optional = True
        group = "Aliases"
    elif assertion_id.find("AWARE") != -1:
        optional = True
        group = "Aware"
    elif desc.find("MUST") == -1:
        optional = True
    return optional, group

def export_html(profile, results, reqs):
    lines = []
    lines.append("<h2>Sparkplug Profile: %s </h2>" % (profile,))
    sorted_list = list(results.keys())
    sorted_list.sort()

    groups = {}
    new_list = []
    for key in sorted_list:
        optional, group =  isOptional(key, descs[key])
        if group != None:
            if group in groups.keys():
                groups[group].add(key)
            else:
                groups[group] = set([key])
        else:
            new_list.append(key)
    sorted_list = new_list

    for group in groups.keys():
        #print("Group", group, groups[group])
        lines.append("<h4>%s Group</h4>" % (group, ))
        count, passed, fails, percent, optional_count, optional_passed, percent_without_optional = stats(groups[group], results)
        lines.append("<h4>Assertion count: %d Number passed: %d Number failed: %d Percent passed: %d%% </h4>" % (count, passed, fails, percent))
        lines.append("<table border=1 width=100%>")
        lines.append( "<tr><th>%s</th><th>%s</th><th>%s</th><th>%s</th><th>%s</th></tr>" % ("Assertion ID", "Assertion Type", "Test", "Time", "Result"))
        group_keys = list(groups[group])
        group_keys.sort()
        for key in group_keys:    
            assertion_type = getType(descs[key])
            key_out = "tck-"+key.lower().replace("_", "-")
            if results[key] == None:
                curline = "<tr><td style=\"text-align: left\">%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>" % (key_out, assertion_type, "", "", "")
            else:
                curline = "<tr><td style=\"text-align: left\">%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>" % (key_out, assertion_type, results[key][0], results[key][1], results[key][2])
            lines.append(curline)
        lines.append("</table>")

    lines.append("<h4>Main Group</h4>")
    count, passed, fails, percent, optional_count, optional_passed, percent_without_optional = stats(sorted_list, results)
    lines.append("<h4>Assertion count: %d Number passed: %d Number failed: %d Percent passed: %d%% </h4>" % (count, passed, fails, percent))
    lines.append("<h4>Optional assertion count: %d Optional number passed: %d Percent passed without optional: %d%% </h4>" % (optional_count, optional_passed, percent_without_optional))

    lines.append("<table border=1 width=100%>")
    lines.append( "<tr><th>%s</th><th>%s</th><th>%s</th><th>%s</th><th>%s</th></tr>" % ("Assertion ID", "Assertion Type", "Test", "Time", "Result"))
    for key in sorted_list:
        assertion_type = getType(descs[key])
        optional, group =  isOptional(key, descs[key])
        if optional:
            assertion_type += " optional"
        key_out = "tck-"+key.lower().replace("_", "-")
        if results[key] == None:
            curline = "<tr><td style=\"text-align: left\">%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>" % (key_out, assertion_type, "", "", "")
        else:
            curline = "<tr><td style=\"text-align: left\">%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>" % (key_out, assertion_type, results[key][0], results[key][1], results[key][2])
        lines.append(curline)
    lines.append("</table>")
    return lines

if __name__ == "__main__":

    if len(sys.argv) < 2:
        print("Test result file must be the first argument")
        sys.exit()

    descs = getDescriptions()

    logfilename = sys.argv[1]
    try:
        logfile = open(logfilename)
        loglines = logfile.readlines()
        logfile.close()
    except:
        print("Can't open logfile", logfilename)
        sys.exit()

    files = glob.glob("**/*Test.java", recursive=True)
    files.append(glob.glob("**/Monitor.java", recursive=True)[0])

    for file in files:
        if not file.endswith("TCKTest.java"):
            print("Processing", file)
            ids = process(file)
            #print(ids)
            if file.find("test/broker") != -1:
                brokerids = brokerids.union(ids)
            elif file.find("test/host") != -1:
                hostids = hostids.union(ids)
            elif file.find("test/edge") != -1:
                edgeids = edgeids.union(ids)
            elif file.find("test/Monitor") != -1:
                monitorids = ids
                for monitorid in monitorids:
                    profile = classify_assertion(monitorid)
                    if profile == "edge":
                        edgeids.add(monitorid)
                    elif profile == "host":
                        hostids.add(monitorid)
                    else: # fits into both
                        edgeids.add(monitorid)
                        hostids.add(monitorid)

    # we have all the testIds for each source file
    #print(len(brokerids), len(hostids), len(edgeids))

    for brokerid in brokerids:
        brokerresults[brokerid] = None
    for hostid in hostids:
        hostresults[hostid] = None
    for edgeid in edgeids:
        edgeresults[edgeid] = None

    warnings = process_logfile(loglines)

    pre = """
    <!DOCTYPE html>
    <html>
    <head>
    <style>
    table, th, td {
        border: 1px solid black;
        border-collapse: collapse;
        text-align: center;
    }
    </style>
    </head>
    """

    post = """
    </body>
    </html>
    """

    now = datetime.datetime.now()
    outlines = ["<h1>Eclipse&trade; Sparkplug&trade; TCK Results summary</h1>"]
    outlines.extend("Date: " + now.strftime("%d/%m/%Y %H:%M:%S"))
    outlines.extend(warnings)
    outlines.extend(export_html("Broker", brokerresults, descs))
    outlines.extend(export_html("Host", hostresults, descs))
    outlines.extend(export_html("Edge", edgeresults, descs))
    outlines = [pre] + outlines + [post]

    outfilename = "summary.html"
    outfile = open(outfilename, "w")
    outfile.writelines(outlines)
    outfile.close()

    print("Results summary written to", outfilename)


