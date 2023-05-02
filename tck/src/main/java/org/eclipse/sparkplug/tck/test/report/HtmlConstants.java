/*******************************************************************************
 * Copyright (c) 2023 Anja Helmbrecht-Schaar
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Anja Helmbrecht-Schaar - initial implementation and documentation
 *******************************************************************************/
package org.eclipse.sparkplug.tck.test.report;

public class HtmlConstants {
    static final String REPORT_TITLE = "Eclipse&trade; Sparkplug&trade; TCK Results summary";
    static final String TITLE = "<h1>";
    static final String TITLE_END = "</h1>";
    static final String SUBTITLE = "<h2>";
    static final String SUBTITLE_END = "</h2>";
    static final String CAP = "<h3>";
    static final String CAP_END = "</h3>";
    static final String TABLE = "<table>";
    static final String TABLE_TITLE = "<tr><th>Assertion ID</th><th>Assertion Type</th><th>Test</th><th>Time</th><th>Result</th></tr>";
    static final String OPTIONAL_STATS_HEADER = "<tr><th>Optional assertion count</th><th>Optional number passed</th><th> </th><th>Percent passed without optional</th></tr>";
    static final String STATS_HEADER = "<tr><th>Assertion count</th><th>Number passed</th><th>Number failed</th><th>Percent passed</th></tr>";

    static final String COLUMN = "</td><td>";
    static final String ROW = "<tr><td>";
    static final String ROW_END = "</td></tr>";
    static final String TABLE_END = "</table>";
    static final String HTML_PREFIX = "<!DOCTYPE html>\n" +
            "    <html>\n<head title=\"" + REPORT_TITLE + "\" >\n" +
            "    <style>\ntable, th, td {" +
            "        border-width: 1px;\n" +
            "        border-color: darkgray;\n" +
            "        border-collapse: collapse;\n" +
            "        border-style: solid;\n" +
            "        padding: 5px;\n" +
            "        text-align: left;\n" +
            "        size: A4;" +
            "    }\n</style>\n" +
            "    </head><body>\n";
    static final String HTML_POSTFIX = " </body>\n</html>";
}
