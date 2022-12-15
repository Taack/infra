package taack.ui

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ui.config.Language

/**
 * Taack Plugin configuration holder.
 */
@CompileStatic
final class TaackPluginConfiguration {
    static final class PluginRole {
        static enum RoleRanking {
            USER,
            MANAGER,
            DIRECTOR
        }
        final String roleName
        final RoleRanking roleRanking

        PluginRole(String roleName, RoleRanking roleRanking) {
            this.roleName = roleName
            this.roleRanking = roleRanking
        }
    }

    interface IPluginRole {
        List<PluginRole> getPluginRoles()
    }

    static final class TaackLinkClass {
        Class linkClass
        String displayString
        MethodClosure showMethod

        TaackLinkClass(Class linkClass, String displayString, MethodClosure showMethod) {
            this.linkClass = linkClass
            this.displayString = displayString
            this.showMethod = showMethod
        }

        /**
         * Used for retrocompatibility purpose
         * @return The controller name as a lower case string
         */
        String getController() {
            showMethod.owner.toString().split(/\./).last().replace('Controller', '').uncapitalize()
        }

        /**
         * Used for retrocompatibility purpose
         * @return The action name
         */
        String getAction() {
            showMethod.method.toString()
        }
    }

    interface ITaackLinkClass {
        List<TaackLinkClass> getTaackLinkClasses()
    }

    final String name
    final String imageResource
    final String mainControllerName
    final List<Language> supportedLanguage
    final IPluginRole pluginRole
    final ITaackLinkClass linkClass
    final boolean hideIcon

    TaackPluginConfiguration(String name, String imageResource, String mainControllerName, List<Language> supportedLanguage, IPluginRole pluginRole, ITaackLinkClass linkClass = null, Boolean hideIcon = false) {
        this.name = name
        this.imageResource = imageResource
        this.mainControllerName = mainControllerName
        this.supportedLanguage = supportedLanguage
        this.pluginRole = pluginRole
        this.linkClass = linkClass
        this.hideIcon = hideIcon
    }
}
