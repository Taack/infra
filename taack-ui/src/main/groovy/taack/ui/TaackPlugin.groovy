/**
 * Contains all user interface classes
 */
package taack.ui

/**
 * Allow to add additional info to grails plugin (like icons, supported languages ...).
 * Must be inherited by the Grails plugin.
 */
interface TaackPlugin {
    /**
     *
     * @return the list of app configuration this gradle module contains
     */
    List<TaackPluginConfiguration> getTaackPluginControllerConfigurations()
}