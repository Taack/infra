package taack.ui.dsl.diagram

import grails.util.Holders
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.render.TaackUiEnablerService
import taack.ui.dsl.helper.Utils
import taack.ui.dump.Parameter

import java.awt.Color

@CompileStatic
final class DiagramOption {
    String title
    boolean showDataCount = false
    boolean hideLegend = false
    List<Color> keyColors
    DiagramResolution resolution
    String clickActionUrl

    static DiagramOptionBuilder getBuilder() {
        return new DiagramOptionBuilder()
    }

    final static class DiagramOptionBuilder {
        private DiagramOption diagramOption

        DiagramOptionBuilder() {
            this.diagramOption = new DiagramOption()
        }

        DiagramOptionBuilder showDataCount() {
            diagramOption.showDataCount = true
            this
        }

        DiagramOptionBuilder hideLegend() {
            diagramOption.hideLegend = true
            this
        }

        DiagramOptionBuilder setKeyColors(Color... color) {
            if (color?.size() > 0) {
                diagramOption.keyColors = color.toList()
            }
            this
        }

        DiagramOptionBuilder setTitle(String title) {
            diagramOption.title = title
            this
        }

        DiagramOptionBuilder setResolution(DiagramResolution resolution) {
            diagramOption.resolution = resolution
            this
        }

        DiagramOptionBuilder setClickAction(MethodClosure action, Long id = null, Map params = null) {
            if ((Holders.grailsApplication.mainContext.getBean('taackUiEnablerService') as TaackUiEnablerService).hasAccess(action, id, params)) {
                diagramOption.clickActionUrl = (new Parameter(Parameter.RenderingTarget.WEB)).urlMapped(Utils.getControllerName(action), action.method, id, params)
            }
            this
        }

        DiagramOptionBuilder setClickAction(MethodClosure action, Map params) {
            setClickAction(action, null, params)
        }

        DiagramOption build() {
            diagramOption
        }
    }

    enum DiagramResolution {
        DEFAULT_540P(960.0, 540.0, 1.0),
        DEFAULT_720p(1280.0, 720.0, 1.3333),
        DEFAULT_1080P(1920.0, 1080.0, 2.0),
        DEFAULT_2K(2048.0, 1080.0, 2.1333),
        DEFAULT_1440P(2560.0, 1440.0, 2.6667),
        DEFAULT_4K(3840.0, 2160.0, 4.0)

        DiagramResolution(BigDecimal width, BigDecimal height, BigDecimal fontSizePercentage) {
            this.width = width
            this.height = height
            this.fontSizePercentage = fontSizePercentage
        }

        final BigDecimal width
        final BigDecimal height
        final BigDecimal fontSizePercentage
    }
}