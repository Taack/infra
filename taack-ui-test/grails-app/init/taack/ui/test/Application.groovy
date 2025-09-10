package taack.ui.test

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import groovy.transform.CompileStatic
import taack.ui.TaackUiConfiguration

@CompileStatic
class Application extends GrailsAutoConfiguration {
    static void main(String[] args) {
        TaackUiConfiguration.root = TaackUiConfiguration.home + '/intranetFilesTest'
        GrailsApp.run(Application, args)
    }
}
