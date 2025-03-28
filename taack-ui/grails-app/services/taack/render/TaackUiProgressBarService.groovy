package taack.render


import grails.compiler.GrailsCompileStatic
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.web.util.WebUtils
import taack.ui.dsl.UiBlockSpecifier
import taack.ui.dsl.block.BlockSpec

/**
 * Service managing progressbar. The action must return immediately after having initialised the progressbar.
 *
 * <p>Usage:
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
 *}</pre>

 */
@GrailsCompileStatic
class TaackUiProgressBarService {
    private Map<String, Integer> progressRegister = [:]
    private Map<String, Integer> progressRegisterMax = [:]
    private Map<String, Closure<BlockSpec>> endsRegister = [:]
    private Set<String> endedProgress = []

    TaackUiService taackUiService

    static UiBlockSpecifier buildProgressBlock(String ret, int max, int value) {
        new UiBlockSpecifier().ui {
            modal {
                ajaxBlock "drawProgress=$ret", {
                    row {
                        custom """\
                        <label for="progress">Job progress:</label>
                        <progress id="progress" max="$max" value="$value"> $value / $max </progress>
                    """.stripIndent()
                    }
                }
            }
        }
    }

    UiBlockSpecifier buildEndBlock(String ret) {
        Closure<BlockSpec> b = progressEnds(ret)
        if (b) {
            new UiBlockSpecifier().ui {
                modal {
                    ajaxBlock "drawProgress=$ret", {
                        inline b
                    }
                }
            }
        } else null
    }

    /**
     * Start to display the progressbar
     *
     * @param ends Block to display once the task is finished
     * @param max Number of task steps
     * @return The ID of the progressbar
     */
    String progressStart(Closure<BlockSpec> ends, Integer max) {
        String ret = (ends.toString() + System.currentTimeMillis()).sha256()
        progressRegister.put(ret, 0)
        progressRegisterMax.put(ret, max)
        endsRegister.put(ret, ends)

        GrailsWebRequest webRequest = WebUtils.retrieveGrailsWebRequest()
        webRequest.currentResponse.outputStream << taackUiService.visit(buildProgressBlock(ret, max, 0))
        webRequest.currentResponse.outputStream.flush()
        webRequest.currentResponse.outputStream.close()

        return ret
    }

    /**
     * Retrieve the max number of steps of a progress bar.
     *
     * @param id The progressbar ID
     * @return Max number of steps
     */
    Integer progressMax(String id) {
        progressRegisterMax[id]
    }

    /**
     * Increment the progress bar
     *
     * @param id The progressbar ID
     * @param progressDelta Number of steps to add
     * @return Current progression
     */
    Integer progress(String id, Integer progressDelta = 0) {
        progressRegister[id] = progressRegister[id] + progressDelta
        log.info "$this ::: progressRegister[id] = ${progressRegister[id]}"
        return progressRegister[id]
    }

    Closure<BlockSpec> progressEnds(String id) {
        def c = endsRegister[id]
        progressClean(id)
        return c
    }

    void progressEndedClosure(String id, Closure<BlockSpec> c) {
        endsRegister[id] = c
    }

    boolean progressHasEnded(String id) {
        endedProgress.contains(id)
    }

    /**
     * Call this method to display the block specified on {@link #progressStart} once the task is finished
     *
     * @param id
     * @return
     */
    boolean progressEnded(String id) {
        endedProgress.add(id)
    }

    private void progressClean(String id) {
        log.info "$progressRegister $progressRegisterMax $endsRegister $endsRegister"
        progressRegister.remove(id)
        progressRegisterMax.remove(id)
        endsRegister.remove(id)
        endedProgress.remove(id)
    }
}
