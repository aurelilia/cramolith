<!--
  ~ Developed as part of the Cramolith project.
  ~ Copyright 2021, see git repository at git.angm.xyz for authors and other info.
  ~ This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
  -->
<#-- @ftlvariable name="error" type="java.lang.String" -->
<#import "base.ftl" as base>

<@base.document title="Cramolith - Login" css="login">
<#--noinspection HtmlUnknownTarget-->
    <form action="/login/submit" method="post" class="login-form">
        <label for="username"></label>
        <input id="username" name="username" type="text" placeholder="Username" required pattern="{,20}">
        <label for="pw"></label>
        <input id="pw" name="pw" type="password" placeholder="Password" required pattern="{,50}">
        <input class="submit" type="submit" value="Submit">
    </form>
    <#if error != ""><p>${error}</p></#if>
</@base.document>