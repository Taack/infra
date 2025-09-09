package taack.ui.dsl.table

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ui.dsl.common.Style


/**
 * Hold Global Table Options
 */
@CompileStatic
final class TableOption {
    MethodClosure uploadFileAction
    Map uploadFileActionParams
    Style headerThemeColor

    static TableOptionBuilder getBuilder() {
        return new TableOptionBuilder()
    }

    /**
     * Hold Global Table Options Builder
     */
    static final class TableOptionBuilder {
        private TableOption tableOption

        TableOptionBuilder() {
            tableOption = new TableOption()
        }

        /**
         * Specify Drop Action
         *
         * @param dropAction
         * @param dropActionParams
         * @return
         */
        TableOptionBuilder onDropAction(MethodClosure dropAction, Map<String, ? extends Serializable> dropActionParams = null) {
            tableOption.uploadFileAction = dropAction
            tableOption.uploadFileActionParams = dropActionParams
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
