package taackUi

import grails.compiler.GrailsCompileStatic
import grails.gsp.PageRenderer
import org.springframework.beans.factory.annotation.Autowired
import taack.ui.TaackPluginConfiguration
import taack.ui.TaackPluginService

@GrailsCompileStatic
class TaackMenuTagLib {

    static namespace = "taack"

    TaackPluginService taackPluginService

    @Autowired
    PageRenderer g

    /**
     * @attr language REQUIRED
     */
    def displayTaackMenu = { attribs ->
        TaackPluginConfiguration pluginConfiguration = taackPluginService.getTaackPluginConfigurationFromControllerName(controllerName)

        if (pluginConfiguration) {
            if (pluginConfiguration.supportedLanguage.size() > 1) {
                out << g.render(template: "/taackUi/taackMenu", model: [pluginConfiguration: pluginConfiguration,
                                                                        language           : attribs['language'],
                                                                        id                 : attribs['id'],
                                                                        params             : params,
                                                                        controllerName     : controllerName,
                                                                        actionName         : actionName
                ])
            }
        }
    }
}