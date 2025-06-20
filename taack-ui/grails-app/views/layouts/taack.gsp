<%@ page import="grails.util.Triple; taack.ui.dsl.common.ActionIcon; taack.domain.TaackGormClass; taack.domain.TaackGormClassRegisterService; org.grails.datastore.gorm.GormEntity; taack.app.TaackAppRegisterService; taack.user.TaackUser; taack.support.ThemeController; taack.ui.dump.html.theme.ThemeMode" %>
<!DOCTYPE html>
<g:if test="${themeMode && themeAuto}">
    <html lang="${lang}" ${themeMode == ThemeMode.NORMAL ? "data-bs-theme-auto=auto data-bs-theme=${themeAuto.name}" : "data-bs-theme=${themeMode.name}"}>
</g:if>
<g:else>
    <html lang="${lang}">
</g:else>
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
                                    Map<GormEntity, Date> notifications = user?.getNotificationRelatedDataList(true) ?: [:]
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
                                                Notification
                                                <a ajaxaction="/taackUserNotification/readAllUserNotifications?title=all"
                                                   unread-notification-number="${notifications.size()}"></a>
                                            </span>
                                            <a class="user-notification-detail-btn" ajaxaction="/taackUserNotification/showUserNotifications" href="/taackUserNotification/showUserNotifications?isAjax=false">
                                                ${raw(ActionIcon.SHOW.getHtml("Details", 20))}
                                            </a>
                                        </li>
                                        <li class="nav-item dropdown">
                                            <div class="user-notification-body">
                                                <g:each in="${notifications.collect {
                                                    new Triple(it.key, it.value, TaackGormClassRegisterService.getTaackGormClass(it.key.class.name))
                                                }.groupBy { Triple<GormEntity, Date, TaackGormClass> info ->
                                                    info.cValue?.notification?.getTitleClosure()?.call(info.aValue.ident())
                                                }.sort { it.key }}" var="group">
                                                    <div class="user-notification-group">
                                                        <div class="group-header">
                                                            <g:if test="${group.key != null}">
                                                                ${group.key}
                                                            </g:if>
                                                            <g:else>
                                                                <g:message code="enum.value.OTHER" />
                                                            </g:else>
                                                            <a ajaxaction="/taackUserNotification/readAllUserNotifications?title=${group.key ?: "other"}"
                                                               unread-notification-number="${(group.value as List).size()}"></a>
                                                        </div>
                                                        <g:each in="${(group.value as List<Triple<GormEntity, Date, TaackGormClass>>).sort { -it.bValue.time }}" var="object">
                                                            <%
                                                                String labelPrefix = object.cValue?.typeLabel?.call(object.aValue.ident())
                                                                String label = object.cValue?.showLabel?.call(object.aValue.ident()) ?: object.aValue.toString()
                                                            %>
                                                            <a ajaxaction="/taackUserNotification/readUserNotification?objectClass=${object.aValue.class.name}&objectId=${object.aValue.ident()}"
                                                               class="group-item nav-link ajaxLink taackAjaxLink" title="${label}">
                                                                <g:if test="${labelPrefix}">
                                                                    <span class="group-item-prefix">[${labelPrefix}]</span>
                                                                </g:if>
                                                                ${label}
                                                            </a>
                                                        </g:each>
                                                    </div>
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
                                    <a class="nav-link" href="/taackLogin">Login</a>
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
