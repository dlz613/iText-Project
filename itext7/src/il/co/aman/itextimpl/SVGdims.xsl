<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:svg="http://www.w3.org/2000/svg">

    <xsl:output method="text"/>

    <xsl:template match="/">
        <xsl:choose>
            <xsl:when test="//xsl:param[@name = 'full_width']">
                <!-- the new (Gotwig) barchart -->
                <xsl:value-of select="//xsl:param[@name = 'full_width']/@select"/>,<xsl:value-of
                    select="//xsl:param[@name = 'full_height']/@select"/>,<xsl:value-of
                    select="//xsl:param[@name = 'fontsize']/@select"/>,<xsl:value-of
                    select="//xsl:param[@name = 'grid_depth']/@select"/>,<xsl:value-of
                    select="//xsl:param[@name = 'rule_width']/@select"/>,<xsl:value-of
                    select="//xsl:param[@name = 'padding']/@select"/>,<xsl:value-of
                    select="//xsl:param[@name = 'y_labels_width']/@select"/>
            </xsl:when>
            <xsl:when test="//xsl:param[@name = 'full_height']">
                <!-- barchart -->
                <xsl:value-of select="//xsl:param[@name = 'size']"/>,<xsl:value-of
                    select="//xsl:param[@name = 'full_height']"/>,<xsl:value-of
                    select="//xsl:param[@name = 'fontsize']"/>,<xsl:value-of
                    select="//xsl:param[@name = 'rule_width']"/>,<xsl:value-of
                    select="//xsl:param[@name = 'border']"/>,<xsl:value-of
                    select="//xsl:param[@name = 'width_corr']"/>,<xsl:value-of
                    select="//xsl:param[@name = 'sample_corr']"/>,<xsl:value-of
                    select="//xsl:param[@name = 'keystart_corr']"/>,<xsl:value-of
                    select="//xsl:param[@name = 'rule_corr']"
                    /><!--,<xsl:value-of
                    select="//xsl:param[@name = 'height_corr']"/>,<xsl:value-of
                    select="//xsl:param[@name = 'height_corr_2']"/>-->,<xsl:value-of
                    select="//xsl:param[@name = 'minus_corr']"/>,<xsl:value-of
                    select="//xsl:param[@name = 'plus_corr']"/>
            </xsl:when>
            <xsl:otherwise>
                <!-- piechart -->
                <xsl:value-of select="//xsl:param[@name = 'whole_size']"/>,<xsl:value-of
                    select="//xsl:param[@name = 'fontsize']"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>
