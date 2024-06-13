<%@ page import="taack.ui.dump.html.theme.ThemeMode" %>
<!DOCTYPE html>

<html lang="${lang}" data-bs-theme="${themeMode.name}">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width">

    <g:if test="${themeMode == ThemeMode.DARK}">
        <meta name="color-scheme" content="dark">
        <meta name="theme-color" content="#eeeeee" media="(prefers-color-scheme: dark)">
    </g:if>
    <g:elseif test="${themeMode == ThemeMode.LIGHT}">
        <meta name="color-scheme" content="light">
        <meta name="theme-color" content="#111111" media="(prefers-color-scheme: light)">
    </g:elseif>

    <title>${conf.defaultTitle}</title>
    <asset:stylesheet href="application-taack-bootstrap.css"/>
    <style>
    .navbar-nav > li > .dropdown-menu {
        background-color: ${conf.bgColor};
    }

    body>nav .navbar-nav a.nav-link {
        color: ${conf.fgColor};
    }

    </style>

    <link rel="icon" type="image/png" href="/assets/favicon.png"/>
    <g:layoutHead/>
</head>

<body>

<nav class="navbar navbar-expand-md" style="background-color: ${conf.bgColor}; color: ${conf.fgColor};">
    <div id="dropdownNav" class="container-fluid">
        <a class="navbar-brand" href="/"><asset:image src='${conf.logoFileName}' width='${conf.logoWidth}'
                                                      height='${conf.logoHeight}' alt="Logo"/>
        </a>
        <button id="dLabel" class="navbar-toggler navbar-dark" type="button" data-bs-toggle="collapse"
                data-bs-target="#navbarSupportedContent"
                aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation"
                data-bs-toggle="dropdownNav">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarSupportedContent">
            ${raw(menu)}
            <ul class="navbar-nav flex-row ml-md-auto ">
                <g:if test="${conf.hasMenuLogin}">
                    <sec:ifLoggedIn>
                        <sec:ifSwitched>
                            <li class="nav-item dropdown">
                                <form action='${request.contextPath}/logout/impersonate' method='POST'>
                                    <input type='submit'
                                           value="Resume as ${grails.plugin.springsecurity.SpringSecurityUtils.switchedUserOriginalUsername}"/>
                                </form>
                            </li>
                        </sec:ifSwitched>
                        <sec:ifNotSwitched>
                            <li class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle" id="navbarUser" role="button"
                                   data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                    <sec:username/>
                                </a>

                                <ul class="dropdown-menu" aria-labelledby="navbarUser">
                                    <li class="nav-item dropdown">
                                        <a class="nav-link ajaxLink taackAjaxLink" ajaxaction="/theme?isAjax=true"><g:message code="theme.label"/></a>
                                    </li>
                                    <li class="nav-item dropdown">
                                        <a class="nav-link" href="/logout"><g:message code="logout.label"/></a>
                                    </li>
                                </ul>

                            </li>
                        </sec:ifNotSwitched>

                    </sec:ifLoggedIn>
                    <sec:ifNotLoggedIn>
                        <li class="nav-item">
                            <a class="nav-link" href="/login">Login</a>
                        </li>
                    </sec:ifNotLoggedIn>

                </g:if>
            </ul>
        </div>
    </div>
</nav>

<g:layoutBody/>
<g:if test="${clientJsPath != null}">
    <script src="${clientJsPath}"></script>
    <asset:javascript src="application-taack-debug.js"/>
</g:if>
<g:else>
    <asset:javascript src="application-taack.js"/>
</g:else>
<script>
    function updateTheme() {
        const colorMode = window.matchMedia("(prefers-color-scheme: dark)").matches ?
            "dark" :
            "light";
        document.querySelector("html").setAttribute("data-bs-theme", colorMode);
    }

    // Set theme on load
    updateTheme()

    // Update theme when the preferred scheme changes
    window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', updateTheme)
</script>
</body>
</html>
