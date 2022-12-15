<li class="nav-item dropdown">
    <a class="nav-link dropdown-toggle" id="navbarLang" role="button"
       data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
        <asset:image
                src="taack/icons/countries/4x3/${language?.iso2 ?: pluginConfiguration.supportedLanguage.first().iso2}.webp"
                width="20"/>
    </a>
    <ul class="dropdown-menu" aria-labelledby="navbarLang">
        <g:each in="${pluginConfiguration.supportedLanguage}">
            <li class="nav-item">
                <%
                    String lang = it.iso2
                    if (lang == "cn") lang = "zh_cn"
                %>
                <g:link action="${actionName}" controller="${controllerName}" id="${id}" class="nav-link" params="${params + [lang: lang]}">
                    <asset:image src="taack/icons/countries/4x3/${it.iso2}.webp" width="20"/>
                    ${it.labeling}
                </g:link>
            </li>
        </g:each>
    </ul>
</li>