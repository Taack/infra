package taack.ui.dsl.table

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ui.dsl.common.Style

@CompileStatic
final class TableOption {
    MethodClosure uploadFileAction
    Map uploadFileActionParams
    Style headerThemeColor

    static TableOptionBuilder getBuilder() {
        return new TableOptionBuilder()
    }

    static final class TableOptionBuilder {
        private TableOption tableOption

        TableOptionBuilder() {
            tableOption = new TableOption()
        }

        TableOptionBuilder onDropAction(MethodClosure c, Map<String, ? extends Serializable> parameters = null) {
            tableOption.uploadFileAction = c
            tableOption.uploadFileActionParams = parameters
            this
        }

        TableOptionBuilder headerTheme(Style theme) {
            tableOption.headerThemeColor = theme
            this
        }

        TableOption build() {
            tableOption
        }
    }
}
