package taack.support

import grails.compiler.GrailsCompileStatic
import grails.plugin.springsecurity.annotation.Secured
import taack.render.TaackUiProgressBarService
import taack.render.TaackUiService
import taack.ui.dsl.UiBlockSpecifier
import taack.ui.dsl.block.UiBlockVisitor

/**
 * Support Controller for progressbar.
 *
 * <p>Here is how to add a progress bar to a long duration job:
 *
 * <pre>{@code
 *          String pId = taackUiProgressBarService.progressStart(BlockSpec.buildBlockSpec {
 *                            custom('''<p>Test ended</p>''')
 *          }, 100)
 *  def task = task {
 *      customerRows.eachWithIndex { String[] row, int i ->
 *          importCustomer(row[1], row[2], row[3], row[4], row[5], row[6], row[7], row[8], row[9], row[10], row[11], row[12], row[13], row[14], row[15], row[16], row[17])
 *          taackUiProgressBarService.progress(pId, 1)
 *      }
 *      contactRows.eachWithIndex { String[] row, int i ->
 *          importContact(currentUser, row[0], row[1], row[2], row[3], row[4], row[5], row[6], row[7], row[8])
 *          taackUiProgressBarService.progress(pId, 1)
 *      }
 *      taackUiProgressBarService.progressEnded(pId)
 *  }
 *}</pre>
 */
@GrailsCompileStatic
@Secured('isAuthenticated()')
class ProgressController {

    TaackUiProgressBarService taackUiProgressBarService
    TaackUiService taackUiService

    def drawProgress(String id) {
        if (id && taackUiProgressBarService.progressMax(id)) {
            int max = taackUiProgressBarService.progressMax(id)
            int value = taackUiProgressBarService.progress(id)
            boolean ended = taackUiProgressBarService.progressHasEnded(id)
            if (!ended) {
                response.outputStream << taackUiService.visit(TaackUiProgressBarService.buildProgressBlock(id, max, value))
            } else {
                response.outputStream << taackUiService.visit(taackUiProgressBarService.buildEndBlock(id))
            }
            response.outputStream.flush()
            response.outputStream.close()
        } else {
            return true
        }
    }

    def echoSelect(String key, String label) {
        if (key && label)
            taackUiService.show(
                    new UiBlockSpecifier().ui {
                        closeModal key, label
                    }
            )
        else return true
    }
}
