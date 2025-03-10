package taack.ui

import groovy.transform.CompileStatic
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@CompileStatic
@Configuration
@ConfigurationProperties("taack-ui")
class TaackUiConfiguration {

    static String defaultTitle = 'Taack Framework'
    static String logoFileName = 'logo-taack-web.svg'
    static int logoWidth = 70
    static int logoHeight = 60

    static boolean hasMenuLogin = true
    static boolean outlineContainer = false
    static boolean fixedTop = false
    static String bgColor = '#05294c'
    static String fgColor = '#eeeeee'
    static String bodyBgColor = '#fff'

    static String solrUrl = 'http://localhost:8983/solr/taack'
    static Boolean disableSecurity = false
}
