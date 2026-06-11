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
    /** Number of leftmost columns to freeze (sticky) when the table scrolls horizontally. 0 disables. */
    int stickyColumns

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

        /**
         * Freeze the {@code count} leftmost columns so they stay visible while the
         * table scrolls horizontally.
         *
         * @param count number of leftmost columns to keep sticky
         */
        TableOptionBuilder stickyColumns(int count) {
            tableOption.stickyColumns = count
            this
        }

        TableOption build() {
            tableOption
        }
    }
}
