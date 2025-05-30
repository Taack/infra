<%@ page import="taack.app.TaackLinkClass; org.apache.commons.lang.StringEscapeUtils; org.grails.datastore.gorm.GormEntity; taack.app.TaackAppRegisterService; taack.user.TaackUser; taack.support.ThemeController; taack.ui.dump.html.theme.ThemeMode" %>
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
        z-index: 9999;
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
                                <%
                                    TaackUser user = currentUser
                                    List<GormEntity> notifications = user?.getUnreadRelatedDataList() ?: []
                                %>
                                <a class="nav-link dropdown-toggle" id="navbarUser" role="button"
                                   data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false"
                                   ${!notifications.isEmpty() ? "unread-notification-number=${notifications.size()}" : ""}>
                                    <sec:username/>
                                </a>

                                <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="navbarUser" style="text-align: center;">
                                    <g:if test="${!notifications.isEmpty()}">
                                        <li class="nav-item dropdown">
                                            <span class="user-notification-header">
                                                <b>Notification (${notifications.size()})</b>
                                            </span>
                                        </li>
                                        <li class="nav-item dropdown">
                                            <div class="user-notification-body">
                                                <g:each in="${notifications.groupBy { TaackAppRegisterService.getTaackLinkClass(it.class.name) }.sort { it.key?.controller }}">
                                                    <%
                                                        TaackLinkClass t = it.key as TaackLinkClass
                                                        List<GormEntity> objects = it.value as List<GormEntity>
                                                    %>
                                                    <div class="group-header"><g:message code="${t != null ? "${t.controller}.app" : "enum.value.OTHER"}" /> (${objects.size()})</div>
                                                    <g:each in="${objects.sort { -it.ident() }}" var="object">
                                                        <a ajaxaction="/taackUserNotification/readUserNotification?objectController=${t.controller}&objectAction=${t.action}&objectClass=${object.class.name}&objectId=${object.ident()}"
                                                           class="group-item nav-link ajaxLink taackAjaxLink" title="${StringEscapeUtils.escapeHtml(object.toString())}">
                                                            ${object.toString()}
                                                        </a>
                                                    </g:each>
                                                </g:each>
                                            </div>
                                        </li>
                                    </g:if>
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
