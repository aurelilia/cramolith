<!--
  ~ Developed as part of the Cramolith project.
  ~ Copyright 2021, see git repository at git.angm.xyz for authors and other info.
  ~ This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
  -->

<#-- @ftlvariable name="message" type="java.lang.String" -->
<#import "base.ftl" as base>

<@base.document title="Cramolith - Home" css="index">
    <h2>Welcome to Cramolith</h2>
    <h4>A Pokemon MMO.</h4>
<#--noinspection HtmlUnknownTarget-->
    <#if message != ""><p>${message}</p></#if>
</@base.document>
