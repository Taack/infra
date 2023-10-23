package taack.ui

import grails.core.GrailsApplication
import grails.plugins.GrailsPlugin
import groovy.transform.CompileStatic
import org.grails.plugins.AbstractGrailsPluginManager

import javax.annotation.PostConstruct

@CompileStatic
final class TaackPluginService {
    GrailsApplication grailsApplication

    List<TaackPlugin> taackPlugins = []

    static EnumOption[] enumOptions

    @PostConstruct
    void init() {
        println "AUO TaackPluginService PostConstruct"
        AbstractGrailsPluginManager pm = grailsApplication.mainContext.getBean('pluginManager') as AbstractGrailsPluginManager
        List<TaackPlugin> tp = []
        pm.allPlugins.each { GrailsPlugin grailsPlugin ->
            if (TaackPlugin.isAssignableFrom(grailsPlugin.pluginClass)) {
                TaackPlugin taackPlugin = grailsPlugin.instance as TaackPlugin
                println "Load ${taackPlugin.class.name}"
                tp.add taackPlugin
            }
        }
        taackPlugins = tp.sort { it.taackPluginControllerConfigurations.name }
        enumOptions = buildEnumOptions()
    }

    TaackPluginConfiguration getTaackPluginConfigurationFromControllerName(final String controllerName) {
        for (def tp : taackPlugins) {
            for (def tpc : tp.taackPluginControllerConfigurations) {
                if (tpc.mainControllerName == controllerName) return tpc
            }
        }
        return null
    }

    TaackPlugin getTaackPluginFromName(final String name) {
        taackPlugins.find { it.getTaackPluginControllerConfigurations().name.contains(name) }
    }

    private EnumOption[] buildEnumOptions() {
        def controllers = taackPlugins.taackPluginControllerConfigurations.flatten() as List<TaackPluginConfiguration>
        def shownControllers = controllers.findAll { it.hideIcon == false }
        EnumOption[] ret = new EnumOption[shownControllers.size()]
        int i = 0
        for (def c in shownControllers) {
            ret[i++] = new EnumOption(c.mainControllerName, c.name)
        }
        ret
    }
}
