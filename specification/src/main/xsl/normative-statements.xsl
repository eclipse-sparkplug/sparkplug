<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" omit-xml-declaration="yes"/>

    <xsl:template match="text()"/>

    <xsl:template match="section">
        <xsl:if test="child::assertion">
            <xsl:text>&#xd;&#xa;</xsl:text>
            <xsl:text>=== </xsl:text>
            <xsl:value-of select="@title"/>
            <xsl:text>&#xd;&#xa;</xsl:text>
            <xsl:apply-templates select="assertion"/>
        </xsl:if>

    </xsl:template>

    <xsl:template match="assertion">
        <xsl:text>* </xsl:text>
        <xsl:value-of select="text"/>
        <xsl:text>&#xd;&#xa;</xsl:text>
    </xsl:template>


</xsl:stylesheet>