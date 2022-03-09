import xml.dom.minidom

print("""
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

    //@SpecAssertions works only with constants like string but not enum or arrays

""")

with xml.dom.minidom.parse('../specification/build/tck-audit/tck-audit.xml') as dom:

    assertions = 0
    def traverse(node, assertion_id):
        global assertions
        
        if node.nodeName == "section":
            #print([node.attributes.item(i).value for i in range(node.attributes.length)]);
            print("// %s %s\n" % (node.childNodes[1].data.split()[0], node.attributes.item(2).value))
        
        elif (node.nodeName == "assertion"):
            assertions += 1
            assert node.attributes.item(0).name == "id" 
            assertion_id = node.attributes.item(0).value
            
        elif assertion_id and node.nodeName == "text":
            upper_assertion_id = assertion_id.upper().replace("-", "_")
            print("    public final static String ID_%s = \"%s\";" % (upper_assertion_id, assertion_id))
            print("    public final static String %s = \"%s\";" % (upper_assertion_id, node.childNodes[0].data.replace("\"", "'")))
            print("\n")
            
        for child in node.childNodes:
            traverse(child, assertion_id)

    traverse(dom, None)


print("}")

print("// no of assertions", assertions)