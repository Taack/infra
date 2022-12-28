<!DOCTYPE html>

<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="color-scheme" content="dark light">
    %{--    <link href="https://cdn.jsdelivr.net/npm/bootstrap-dark-5@1.1.3/dist/css/bootstrap-unlit.min.css" rel="stylesheet">--}%

    <title>${conf.defaultTitle}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3" crossorigin="anonymous">
    <asset:stylesheet href="application-taack.css"/>
    <meta name="theme-color" content="#eeeeee" media="(prefers-color-scheme: dark)">
    <meta name="theme-color" content="#111111" media="(prefers-color-scheme: light)">
    <style>
    .navbar-nav > li > .dropdown-menu {
        background-color: ${conf.bgColor};
    }

    .navbar-nav a.nav-link {
        color: ${conf.fgColor};
    }

    <g:if test="${!conf.outlineContainer}">
    .taackContainer {
        outline: none;
    }
    </g:if>

    .navbar-toggler .navbar-toggler-icon {
        background-image: url("data:image/svg+xml;charset=utf8,%3Csvg viewBox='0 0 32 32' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath stroke='${URLEncoder.encode(conf.fgColor, "UTF-8")}' stroke-width='2' stroke-linecap='round' stroke-miterlimit='10' d='M4 8h24M4 16h24M4 24h24'/%3E%3C/svg%3E");
    }

    %{--.navbar-toggler.navbar-toggler {--}%
    %{--    border-color: ${c.fgColor};--}%
    %{--}--}%
    </style>

    <link rel="icon" type="image/png" href="/assets/favicon.png"/>
    <g:layoutHead/>
</head>

<body class="day">

<nav class="navbar navbar-expand-md"
     style="background-color: ${conf.bgColor}; color: ${conf.fgColor}; font-size: small;">
    <div id="dropdownNav" class="container-fluid">
        <a class="navbar-brand" href="/"><asset:image src='${conf.logoFileName}' width='${conf.logoWidth}'
                                                      height='${conf.logoHeight}'/>
        </a>
        <button id="dLabel" class="navbar-toggler navbar-dark" type="button" data-bs-toggle="collapse"
                data-bs-target="#navbarSupportedContent"
                aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation"
                data-bs-toggle="dropdownNav">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                ${raw(menu)}
            </ul>
            <ul class="navbar-nav flex-row ml-md-auto ">
                <taack:displayTaackMenu language="${language}" id="${id}" params="${params}"/>
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
                                        <a class="nav-link" href="/logout">Logout</a>
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

<asset:javascript src="application-taack.js"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-ka7Sk0Gln4gmtz2MlQnikT1wXgYsOg+OMhuP+IlRH9sENBO0LRn5q+8nbTov4+1p"
        crossorigin="anonymous"></script>
</body>
</html>
