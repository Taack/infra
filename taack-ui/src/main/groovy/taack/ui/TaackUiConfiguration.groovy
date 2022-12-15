package taack.ui

import groovy.transform.CompileStatic
import io.micronaut.context.annotation.ConfigurationProperties

@CompileStatic
@ConfigurationProperties("taack-ui")
class TaackUiConfiguration {
    String defaultTitle
    String logoFileName
    int logoWidth
    int logoHeight

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
}
