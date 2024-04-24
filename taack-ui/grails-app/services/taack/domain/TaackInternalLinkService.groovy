package taack.domain

import grails.compiler.GrailsCompileStatic
import grails.util.Holders
import org.grails.datastore.gorm.GormEntity
import taack.ui.TaackPluginConfiguration
import taack.ui.TaackPluginService

@GrailsCompileStatic
final class TaackInternalLinkService {
    TaackPluginService taackPluginService

    TaackPluginConfiguration.TaackLinkClass getTaackLinkClass(GormEntity gormEntity) {
        List<TaackPluginConfiguration> configs = taackPluginService.taackPlugins*.getTaackPluginControllerConfigurations().flatten() as List<TaackPluginConfiguration>
        List<TaackPluginConfiguration.TaackLinkClass> listLinkClass = configs*.linkClass*.taackLinkClasses.flatten() as List<TaackPluginConfiguration.TaackLinkClass>
        return listLinkClass.find {
            it?.linkClass?.name == gormEntity?.class?.name
        }
    }

    static TaackPluginConfiguration.TaackLinkClass getTaackLinkClass(String clazz) {
        TaackPluginService taackPluginService = Holders.applicationContext.getBean('taackPluginService') as TaackPluginService
        List<TaackPluginConfiguration> configs = taackPluginService.taackPlugins*.getTaackPluginControllerConfigurations().flatten() as List<TaackPluginConfiguration>
        List<TaackPluginConfiguration.TaackLinkClass> listLinkClass = configs*.linkClass*.taackLinkClasses.flatten() as List<TaackPluginConfiguration.TaackLinkClass>
        return listLinkClass.find {
            it?.linkClass?.name == clazz
        }
    }
}
