import xml.dom.minidom

inputFile = "../specification/build/tck-audit/tck-audit.xml"
outputFile = "src/main/java/org/eclipse/sparkplug/tck/test/common/Requirements.java"

outfile = open(outputFile, "w")

outfile.write("""
/**
 * Copyright (c) 2022 Anja Helmbrecht-Schaar, Ian Craggs
 * <p>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 * <p>
 * Contributors:
 * Anja Helmbrecht-Schaar - initial implementation and documentation
 */
package org.eclipse.sparkplug.tck.test.common;

/**
 * Class that contains all Requirement IDs and Description, that have to check.
 */
public class Requirements {

    // @SpecAssertions works only with constants like string but not enum or arrays

""")

with xml.dom.minidom.parse(inputFile) as dom:

    assertions = 0
    def traverse(node, assertion_id):
        global assertions
        
        if node.nodeName == "section":
            #print([node.attributes.item(i).value for i in range(node.attributes.length)]);
            outfile.write("    // %s %s\n" % (node.childNodes[1].data.split()[0], node.attributes.item(2).value))
        
        elif (node.nodeName == "assertion"):
            assertions += 1
            assert node.attributes.item(0).name == "id" 
            assertion_id = node.attributes.item(0).value
            
        elif assertion_id and node.nodeName == "text":
            upper_assertion_id = assertion_id.upper().replace("-", "_")
            outfile.write("    public final static String ID_%s = \"%s\";\n" % (upper_assertion_id, assertion_id))
            outfile.write("    public final static String %s = \"%s\";\n\n" % (upper_assertion_id, node.childNodes[0].data.replace("\"", "'")))
            
        for child in node.childNodes:
            traverse(child, assertion_id)

    traverse(dom, None)

outfile.write("}\n// no of assertions %d\n" % assertions)
outfile.close()

