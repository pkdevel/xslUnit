<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="html" indent="yes" />

	<xsl:template match="/">
		<html>
			<body>
				<h2>My CD Collection</h2>
				<xsl:apply-templates />
			</body>
		</html>
	</xsl:template>

	<xsl:template match="cd">
		<p>
			<xsl:apply-templates select="title" />
			<xsl:apply-templates select="artist" />
		</p>
	</xsl:template>

	<xsl:template match="title">
		<xsl:text>Title:</xsl:text>
		<span style="color:#ff0000">
			<xsl:value-of select="." />
		</span>
	</xsl:template>

	<xsl:template match="artist">
		<xsl:text>Artist:</xsl:text>
		<span style="color:#00ff00">
			<xsl:value-of select="." />
		</span>
	</xsl:template>
</xsl:stylesheet>
