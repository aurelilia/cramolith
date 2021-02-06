<!--
  ~ Developed as part of the Cramolith project.
  ~ Copyright 2021, see git repository at git.angm.xyz for authors and other info.
  ~ This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
  -->

<#-- @ftlvariable name="message" type="java.lang.String" -->
<#import "base.ftl" as base>

<@base.document title="Cramolith - Home">          <!-- change name to home.ftl-->
    <section class="navtop">
        <#--noinspection HtmlUnknownTarget-->
        <a class="active" href="/">Home</a>
        <#--noinspection HtmlUnknownTarget-->
        <a href="/register">Register</a>
        <#--noinspection HtmlUnknownTarget-->
        <a href="/changelog">Changelog</a>
        <#--noinspection HtmlUnknownTarget-->
        <a href="/about_us">About us</a>
    </section>
        <h2>Welcome to Cramolith</h2>
        <h4>The game that doesn't really exist yet.</h4>
<#--noinspection HtmlUnknownTarget-->
        <#if message != ""><p>${message}</p></#if>
</@base.document>
