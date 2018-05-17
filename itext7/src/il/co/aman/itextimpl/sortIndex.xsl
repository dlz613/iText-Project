<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:template match="/Index">
        <Index>
            <xsl:copy-of select="@*"/>
            <xsl:for-each select="Tag">
                <xsl:sort select="Cell/@Bookmark_Level"/>
                <xsl:copy-of select="."/>
            </xsl:for-each>
        </Index>
    </xsl:template>

</xsl:stylesheet>
