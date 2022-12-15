package taack.support

import grails.compiler.GrailsCompileStatic
import grails.plugin.springsecurity.annotation.Secured
import taack.base.TaackUiProgressBarService
import taack.base.TaackUiSimpleService
import taack.ui.base.UiBlockSpecifier

/**
 * Support Controller for progressbar.
 *
 * <p>Here is how to add a progress bar to a long duration job:
 *
 * <pre>{@code
 *  String pId = taackUiProgressBarService.progressStart(CrewUiService.messageBlock("Done .."), rowCount)
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
 * }</pre>
 */
@GrailsCompileStatic
@Secured("isAuthenticated()")
class ProgressController {

    TaackUiProgressBarService taackUiProgressBarService
    TaackUiSimpleService taackUiSimpleService

    def drawProgress(String id) {
        if (id && taackUiProgressBarService.progressMax(id)) {
            int max = taackUiProgressBarService.progressMax(id)
            int value = taackUiProgressBarService.progress(id)
            boolean ended = taackUiProgressBarService.progressHasEnded(id)
            if (!ended) {
                response.outputStream << taackUiSimpleService.visit(new UiBlockSpecifier().ui {
                    closeModalAndUpdateBlock(TaackUiProgressBarService.buildProgressBlock(id, max, value).closure)
                }, true)
                response.outputStream.flush()
                response.outputStream.close()
            } else {
                taackUiSimpleService.show(new UiBlockSpecifier().ui {
                    closeModalAndUpdateBlock(taackUiProgressBarService.progressEnds(id).closure)
                })
            }
        } else {
            return true
        }
    }
}
