<%@ page import="taack.support.ThemeController; taack.ui.dump.html.theme.ThemeMode" %>
<!DOCTYPE html>

<html lang="${lang}" ${themeMode == ThemeMode.NORMAL ? "data-bs-theme-auto=auto data-bs-theme=${themeAuto.name}" : "data-bs-theme=${themeMode.name}"}>
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
    ${raw(bootstrapCssTag)}
    <asset:stylesheet href="application-taack.css"/>

    <style>
    .navbar-nav > li > .dropdown-menu {
        background-color: ${conf.bgColor};
    }

    body > nav .navbar-nav a.nav-link {
        color: ${conf.fgColor};
    }

<g:if test="${conf.fixedTop}">
    body {
        padding-top: 4.5rem;
    }
</g:if>
    </style>

    <link rel="icon" type="image/png" href="/assets/favicon.png"/>
    <g:layoutHead/>
</head>

<body>

<nav class="navbar navbar-expand-md ${conf.fixedTop ? "fixed-top" :""}" style="background-color: ${conf.bgColor}; color: ${conf.fgColor};">
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
                                        <a class="nav-link ajaxLink taackAjaxLink"
                                           ajaxaction="/theme?isAjax=true"><g:message code="theme.label"/></a>
                                    </li>
                                    <li class="nav-item dropdown">
                                        <a class="nav-link" href="/logout"><g:message code="logout.label"/></a>
                                    </li>
                                </ul>

                            </li>
                        </sec:ifNotSwitched>

                    </sec:ifLoggedIn>
                    <sec:ifNotLoggedIn>
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle" id="navbarUser" role="button"
                               data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                <asset:image src="/taack/icons/actions/config.svg"/>
%{--                                <svg width="22px" height="22px" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">--}%
%{--                                    <path d="M12 15C13.6569 15 15 13.6569 15 12C15 10.3431 13.6569 9 12 9C10.3431 9 9 10.3431 9 12C9 13.6569 10.3431 15 12 15Z" stroke="${conf.fgColor}" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>--}%
%{--                                    <path d="M19.6224 10.3954L18.5247 7.7448L20 6L18 4L16.2647 5.48295L13.5578 4.36974L12.9353 2H10.981L10.3491 4.40113L7.70441 5.51596L6 4L4 6L5.45337 7.78885L4.3725 10.4463L2 11V13L4.40111 13.6555L5.51575 16.2997L4 18L6 20L7.79116 18.5403L10.397 19.6123L11 22H13L13.6045 19.6132L16.2551 18.5155C16.6969 18.8313 18 20 18 20L20 18L18.5159 16.2494L19.6139 13.598L21.9999 12.9772L22 11L19.6224 10.3954Z" stroke="${conf.fgColor}" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>--}%
%{--                                </svg>--}%
                            </a>

                            <ul class="dropdown-menu" aria-labelledby="navbarUser">
                                <li class="nav-item dropdown">
                                    <a class="nav-link" ajaxaction="/theme?isAjax=true"><g:message code="theme.label"/></a>
                                </li>
                                <li class="nav-item dropdown">
                                    <a class="nav-link" href="/login">Login</a>
                                </li>

                            </ul>

                        </li>

                    %{--                        <li class="nav-item">--}%
%{--                            <a class="nav-link" href="/login">Login</a>--}%
%{--                        </li>--}%
                    </sec:ifNotLoggedIn>
                </g:if>
            </ul>
        </div>
    </div>
</nav>

<g:layoutBody/>
${raw(bootstrapJsTag)}
<g:if test="${clientJsPath != null}">
    <script src="${clientJsPath}"></script>
    <asset:javascript src="application-taack-debug.js"/>
</g:if>
<g:else>
    <asset:javascript src="application-taack.js"/>
</g:else>
</body>
</html>
