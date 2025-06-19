package taack.ui

import grails.plugins.*

class TaackUiGrailsPlugin extends Plugin {

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = '7.0.0-M4 > *'
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        'grails-app/views/error.gsp'
    ]

    // TODO Fill in these fields
    def title = 'Taack Ui Grails Plugin' // Headline display name of the plugin
    def author = 'Adrien Guichard'
    def authorEmail = 'adrien.guichard@taack.org'

    def profiles = ['web']
    def documentation = 'http://taack.org'
    def license = 'APACHE'

    Closure doWithSpring() { {->
            // TODO Implement runtime spring config (optional)
        }
    }

    void doWithDynamicMethods() {
        // TODO Implement registering dynamic methods to classes (optional)
    }

    void doWithApplicationContext() {
        // TODO Implement post initialization spring config (optional)
    }

    void onChange(Map<String, Object> event) {
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    void onConfigChange(Map<String, Object> event) {
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    void onShutdown(Map<String, Object> event) {
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
