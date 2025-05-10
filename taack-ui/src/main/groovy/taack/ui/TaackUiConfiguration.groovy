package taack.ui

import groovy.transform.CompileStatic
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@CompileStatic
@Configuration
@ConfigurationProperties(prefix = "taack-ui")
class TaackUiConfiguration {
    String defaultTitle
    String logoFileName
    int logoWidth
    int logoHeight

    boolean fixedTop
    boolean hasMenuLogin
    boolean outlineContainer
    String bgColor
    String fgColor
    String bodyBgColor

    String root
    String resources
    String javaPath
    String plantUmlPath
    String solrUrl
    Boolean disableSecurity = false
}
