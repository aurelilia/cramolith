<!--
  ~ Developed as part of the Cramolith project.
  ~ This file was last modified at 2/4/21, 12:26 PM.
  ~ Copyright 2021, see git repository at git.angm.xyz for authors and other info.
  ~ This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
  -->

<#-- @ftlvariable name="error" type="java.lang.String" -->

<!DOCTYPE html>
<html lang="en">

    <head>
        <title>Cramolith</title>
        <meta charset="UTF-8">
        <link href="/static/css/index.css" type="text/css" rel="stylesheet">
        <link rel="preconnect" href="https://fonts.gstatic.com">
        <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@300;400&display=swap" rel="stylesheet">
    </head>

    <body>
    <section class="overlay">
        <header>
            <h1>Cramolith</h1>
        </header>
        <main>
            <form action="/submit" method="post">
                <label for="username"></label>
                <input id="username" name="username" type="text" placeholder="Username" required pattern="{,20}">
                <br>
                <br>
                <label for="pw"></label>
                <input id="pw" name="pw" type="password" placeholder="Password" required pattern="{,50}">
                <br>
                <br>
                <label for="pw-confirm"></label>
                <input id="pw-confirm" name="pw-confirm" type="password" placeholder="Confirm Password" pattern="{,50}">
                <br>
                <input type="submit" value="Submit">
            </form>
            <p>${error}</p>
                </main>
            </section>
    </body>

</html>
