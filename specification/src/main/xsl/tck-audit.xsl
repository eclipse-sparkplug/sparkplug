<?xml version="1.0" encoding="UTF-8"?>
<!--

    Copyright (c) 2018 JSR 371 expert group and contributors

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

-->
<!--
  ~ Creates the TCK audit file based on the DocBook XML mark-up of the specification.
  ~
  ~ The generation of the audit file is controlled by marking individual sentences from the
  ~ specification as relevant for the TCK using the "role" attribute, which can be used in all
  ~ DocBook elements (paras, phrases etc.). The following values for the "role" attribute are
  ~ supported:
  ~
  ~ * tck-testable: Adds a testable assertion with the marked statement to the audit file
  ~ * tck-not-testable: Adds a not testable assertion with the marked statement to the audit file
  ~ * tck-ignore: Ignores the marked statement when given with another marked statement (e.g. to
  ~   exclude explanatory phrases)
  ~ * tck-needs-update: Adds the note "Needs update" to the concerned statement (can be given
  ~   together with tck(-not)-testable)
  ~
  ~ Implementation note:
  ~
  ~ The generation happens in several passes, each processing a result tree fragment (RTF) created
  ~ by the previous pass:
  ~
  ~ * Merge all chapters referenced in the index file
  ~ * Determine the index numbers of all chapters and sections
  ~ * Expand xref elements into the index number of the referenced chapter or section
  ~ * Create the audit file
  ~
  ~ @author Gunnar Morling
  -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xslt"
    xmlns:exslt="http://exslt.org/common" xmlns:xi="http://www.w3.org/2001/XInclude" version="1.0">

    <xsl:output method="xml" indent="yes" xalan:indent-amount="4" />

    <xsl:param name="currentDate"/>
    <xsl:param name="revision"/>

    <!-- A regular apostroph; provided as a variable to avoid escaping issues -->
    <xsl:variable name="apos">'</xsl:variable>

    <!-- ### Passes by creating and processing result tree fragments ### -->
    <xsl:variable name="merged">
        <xsl:apply-templates mode="merge" select="/"/>
    </xsl:variable>

    <xsl:variable name="withSectionNums">
        <xsl:apply-templates mode="addSectionNums" select="exslt:node-set($merged)"/>
    </xsl:variable>

    <xsl:variable name="prepared">
        <xsl:apply-templates mode="prepare" select="exslt:node-set($withSectionNums)"/>
    </xsl:variable>

    <xsl:template match="/">
        <xsl:apply-templates mode="createAuditFile" select="exslt:node-set($prepared)"/>
    </xsl:template>

    <!-- ### Merge templates ### -->

    <xsl:template match="xi:include" mode="merge">
        <xsl:variable name="fileName">en/<xsl:value-of select="@href" /></xsl:variable>
        <xsl:apply-templates select="document($fileName)" mode="merge"/>
    </xsl:template>

    <xsl:template match="@*|node()" mode="merge">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()" mode="merge"/>
        </xsl:copy>
    </xsl:template>

    <!-- ### addSectionNums templates ### -->

    <xsl:template match="/article/section" mode="addSectionNums">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:attribute name="sectionNum"><xsl:number from="article" level="single" /></xsl:attribute>
            <xsl:apply-templates mode="addSectionNums"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="section" mode="addSectionNums">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:attribute name="sectionNum"><xsl:number count="section" from="article" level="multiple" /></xsl:attribute>
            <xsl:apply-templates mode="addSectionNums"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="@*|node()" mode="addSectionNums">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()" mode="addSectionNums"/>
        </xsl:copy>
    </xsl:template>

    <!-- ### Prepare templates ### -->

    <xsl:template match="xref" mode="prepare">
        <xsl:variable name="id" select="@linkend"/>
        <xsl:variable name="linkedNode" select="ancestor::*//*[@xml:id=$id]" />
        <xsl:if test="not($linkedNode)">
            <xsl:message terminate="yes">
                No node found for link id: <xsl:value-of select="$id" />
            </xsl:message>
        </xsl:if>
        <xsl:value-of select="$linkedNode/@sectionNum" />
    </xsl:template>

    <xsl:template match="*[@role = 'tck-ignore']" mode="prepare"/>

    <xsl:template match="@*|node()" mode="prepare">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()" mode="prepare"/>
        </xsl:copy>
    </xsl:template>

    <!-- ### Create audit file templates ### -->

    <xsl:template match="article | book"  mode="createAuditFile">
        <xsl:text>&#10;</xsl:text>
        <xsl:comment>

    Copyright (c) 2016-2019 JSR 371 expert group and contributors

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

</xsl:comment>
        <xsl:text>&#10;</xsl:text>
        <xsl:comment>
            <xsl:text> Generated by tck-audit.xsl at </xsl:text><xsl:value-of select="$currentDate"/><xsl:text> </xsl:text>
            <xsl:text>(revision </xsl:text><xsl:value-of select="$revision"/><xsl:text>) </xsl:text>
        </xsl:comment>
        <xsl:text>&#10;</xsl:text>

        <specification xmlns="http://jboss.com/products/weld/tck/audit" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://jboss.com/products/weld/tck/audit" name="JSR-371: MVC 1.0" version="1.0" id="mvc" generateSectionIds="true">

            <xsl:apply-templates mode="createAuditFile"/>

        </specification>
    </xsl:template>

    <xsl:template match="section" mode="createAuditFile">
        <xsl:call-template name="check-section-id">
            <xsl:with-param name="sectionId"><xsl:value-of select="@xml:id" /></xsl:with-param>
            <xsl:with-param name="sectionNum"><xsl:value-of select="@sectionNum" /></xsl:with-param>
        </xsl:call-template>
        <section>
            <xsl:attribute name="id"><xsl:value-of select="@xml:id" /></xsl:attribute>
            <xsl:attribute name="title"><xsl:value-of select="normalize-space(translate(title, '’', $apos))" /></xsl:attribute>
            <xsl:attribute name="level"><xsl:value-of select="count(ancestor::section) + 1" /></xsl:attribute>
            <xsl:variable name="sectionIdConstant"><xsl:call-template name="section-id-to-constant"><xsl:with-param name="sectionId" select="@xml:id" /></xsl:call-template></xsl:variable>
            <xsl:comment><xsl:text> </xsl:text><xsl:value-of select="@sectionNum" /><xsl:text> - </xsl:text><xsl:value-of select="$sectionIdConstant" /><xsl:text> </xsl:text></xsl:comment>

            <!-- get all assertions directly under chapter, without a section -->
            <xsl:apply-templates select="*[not(local-name() = 'section')]" mode="createAuditFile"/>
        </section>

        <!-- get all sections, flattened to one level -->
        <xsl:apply-templates select="section" mode="createAuditFile"/>
    </xsl:template>

    <!-- Add an assertion for any element with role="tck..." -->
    <xsl:template match="*[starts-with(@role, 'tck')]" mode="createAuditFile">
        <xsl:variable name="normalized"><xsl:value-of select="normalize-space(translate(., '’', $apos))" /></xsl:variable>
        <xsl:variable name="firstWord"><xsl:value-of select="substring-before(concat($normalized, ' '), ' ')"/></xsl:variable>

        <xsl:variable name="assertionText">
            <!-- Capitalize the first word if it doesn't contain upper-case letter already (e.g. a camel-cased method name) -->
            <xsl:choose>
                <xsl:when test="string-length(translate($firstWord, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ-', '')) &lt; string-length($firstWord)">
                    <xsl:value-of select="$normalized"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="concat(translate(substring($normalized, 1, 1), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), substring($normalized, 2))"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <assertion>
            <xsl:choose>
                <xsl:when test="contains(@role, 'id-')">
                    <xsl:variable name="cutLeftPart">
                        <xsl:value-of select="substring-after(@role, 'id-')"/>
                    </xsl:variable>
                    <xsl:attribute name="id">
                        <xsl:value-of select="substring-before(concat($cutLeftPart, ' '), ' ')"/>
                    </xsl:attribute>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:attribute name="id">
                        <xsl:number count="*[starts-with(@role, 'tck')]" from="section" level="any" format="a" />
                    </xsl:attribute>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:if test="contains(@role, 'tck-not-testable')">
                <xsl:attribute name="testable">false</xsl:attribute>
            </xsl:if>

            <text>
                <xsl:choose>
                    <!-- Remove trailing ":" -->
                    <xsl:when test="substring($assertionText, string-length($assertionText)) = ':'">
                        <xsl:value-of select="substring($assertionText, 0, string-length($assertionText))" />
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$assertionText" />
                    </xsl:otherwise>
                </xsl:choose>
            </text>

            <xsl:if test="contains(@role, 'tck-needs-update')">
                <note>Needs update</note>
            </xsl:if>
        </assertion>
    </xsl:template>

    <xsl:template match="@*|node()" mode="createAuditFile">
        <xsl:apply-templates select="@*|node()" mode="createAuditFile"/>
    </xsl:template>

    <!-- Check that the given section id is manually defined -->
    <xsl:template name="check-section-id">
        <xsl:param name="sectionId" />
        <xsl:param name="sectionNum" />
        <xsl:if test="starts-with($sectionId, '_')">
            <xsl:message  terminate="yes">
                Error: section <xsl:value-of select="$sectionNum" /><xsl:text> - </xsl:text><xsl:value-of select="$sectionId" /> seems to be automatically generated: it starts with an underscore.
            </xsl:message>
        </xsl:if>
    </xsl:template>

    <xsl:template name="section-id-to-constant">
        <xsl:param name="sectionId" />
        <xsl:variable name="smallcase" select="'abcdefghijklmnopqrstuvwxyz-'" />
        <xsl:variable name="uppercase" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ_'" />
        <xsl:value-of select="translate($sectionId, $smallcase, $uppercase)" />
    </xsl:template>

</xsl:stylesheet>
