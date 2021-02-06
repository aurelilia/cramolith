<!--
  ~ Developed as part of the Cramolith project.
  ~ Copyright 2021, see git repository at git.angm.xyz for authors and other info.
  ~ This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
  -->

<#macro document title>
    <!DOCTYPE html>
    <html lang="en">

    <head>
        <title>${title}</title>
        <meta charset="UTF-8">
        <link href="/static/css/index.css" type="text/css" rel="stylesheet">
        <link rel="preconnect" href="https://fonts.gstatic.com">
        <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@300;400&display=swap" rel="stylesheet">
    </head>

    <body>
    <header>
        <h1 class="logo">Cramolith</h1>
    </header>
    <main>
        <#nested>
    </main>
    </body>

    </html>
</#macro>