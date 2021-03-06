<!--
  ~ Developed as part of the Cramolith project.
  ~ Copyright 2021, see git repository at git.angm.xyz for authors and other info.
  ~ This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
  -->

<#-- @ftlvariable name="user" type="xyz.angm.cramolith.server.database.Player" -->
<#import "base.ftl" as base>

<@base.document title="Cramolith - User Settings" css="register">
<#--noinspection HtmlUnknownTarget-->
    <p>You can change your name or password here.</p>
    <form action="/settings/submit" method="post" class="register-form">
        <label for="username"></label>
        <input id="username" name="username" type="text" placeholder="Username" value="${user.name}" required pattern="{,20}">
        <label for="pw"></label>
        <input id="pw" name="pw" type="password" placeholder="Password" value="${user.password}" required pattern="{,50}">
        <input type="submit" value="Submit">
    </form>
</@base.document>
