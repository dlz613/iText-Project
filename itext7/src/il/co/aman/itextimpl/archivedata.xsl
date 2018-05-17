<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    
    <xsl:output method="text"/>
    
    <xsl:template match="Data/Page[@Id]">PAGE,<xsl:value-of select="@Id"/>|
<xsl:apply-templates/>
</xsl:template>
    
    <xsl:template match="Tag/Cell"><xsl:if test="not(@Id = '-999')"><xsl:value-of select="@Id"/>:<xsl:value-of
        select="@type"/>,<xsl:value-of select="@Left"/>,<xsl:value-of select="@Top"
        />,<xsl:if test="parent::Tag/@TagKey"><xsl:value-of select="parent::Tag/@TagKey"/></xsl:if>&lt;<xsl:value-of select="."/>&gt;
</xsl:if></xsl:template>
    <xsl:template match="text()"/>
</xsl:stylesheet>
