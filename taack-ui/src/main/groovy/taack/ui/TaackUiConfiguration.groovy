package taack.ui

import groovy.transform.CompileStatic

@CompileStatic
class TaackUiConfiguration {
    static String defaultTitle = "Taack"
    static String logoFileName = "logo-taack-web.svg"
    static int logoWidth = 70
    static int logoHeight = 60

    static boolean fixedTop = false
    static boolean hasMenuLogin = true
    static boolean outlineContainer = false
    static String bgColor = '#05294c'
    static String fgColor = '#eeeeee'
    static String bodyBgColor = '#fff'

    static String home = System.getProperty("user.home")
    static String root = home + '/intranetFiles'
    static String taack = home + '/taack'
    static String resources = root + '/resources'
    static String javaPath = '/usr/bin/java'
    static String plantUmlPath = home + '/plantuml-1.2022.7.jar'
    static String solrUrl = 'http://localhost:8983/solr/taack'
    static Boolean disableSecurity = false
}
