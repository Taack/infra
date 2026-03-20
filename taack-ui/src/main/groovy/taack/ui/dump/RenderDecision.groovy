package taack.ui.dump

import groovy.transform.CompileStatic
import taack.ui.dump.common.BlockLog

@CompileStatic
final class RenderDecision {
    private final Parameter parameter
    private final BlockLog blockLog

    boolean isModal = false
    boolean isRefreshing
    boolean renderTab = false
    boolean poke = false
    String currentAjaxBlockId = null
    String explicitAjaxBlockId = null

    RenderDecision(Parameter parameter, BlockLog blockLog) {
        this.parameter = parameter
        this.blockLog = blockLog
        this.isRefreshing = parameter.params.boolean('refresh')
    }

    private void simpleLog(String toPrint) {
        blockLog.simpleLog('RenderDecision::' + toPrint)
    }

    /**
    * Check if a block DSL element has to be rendered. If a page contains multiple block, and user click on
    * a table to sort, only the table will be rendered when processing the DSL.
    *
    * * If the page is not ajax, everything is rendered
    * * else
    * * * If no explicit ajaxBlock, only current or target ajaxBlock is rendered
    * * * If explicit ajaxBlocks are specified (via {@link #setExplicitAjaxBlockId(String id) setAjaxBlockId}),
    *     only then will be rendered if in ajax mode (parameter.isAjaxRendering == true)
    *
    * @param id
    * @return
    */
    boolean shouldRender(String id = null) {
        // if many blocks in the same response, only redraw current block
        // further the first block must be in ajaxMode until current block ends
        if (parameter.target == Parameter.RenderingTarget.MAIL) return true
        if (poke) return true
        simpleLog("shouldRender0 :> renderTab: $renderTab, id: $id, explicitAjaxBlockId: ${explicitAjaxBlockId}, isModal: ${isModal}, params: ${parameter.params}")

        if (!parameter.isAjaxRendering) {
            simpleLog('shouldRender01: true')
            return true
        }
        if (renderTab && parameter.isAjaxRendering) {
            simpleLog('shouldRender02: true')
            return true
        }

        if ((!id && (!parameter.isAjaxRendering && !isModal) || explicitAjaxBlockId != null)) {
            boolean ret = !parameter.tabId && !parameter.ajaxBlockId || parameter.ajaxBlockId == explicitAjaxBlockId
            simpleLog("shouldRender1 return ${ret}, because NOT (AJAX OR MODAL) OR AJAX BUT ANOTHER id")
            return ret
        } else if (!id && !parameter.ajaxBlockId) {
            simpleLog("shouldRender2 return isModal($isModal && $poke)")
            return isModal
        }

        if (parameter.isAjaxRendering && currentAjaxBlockId == null) {
            currentAjaxBlockId = parameter.ajaxBlockId
        }

        if (parameter.targetAjaxBlockId) {
            currentAjaxBlockId = parameter.targetAjaxBlockId
        }

        simpleLog("shouldRender3 :> currentAjaxBlockId = ${currentAjaxBlockId}, targetAjaxBlockId = ${parameter.targetAjaxBlockId}, ajaxBlockId = ${parameter.ajaxBlockId}")

        boolean doRender = (currentAjaxBlockId == id && (isModal || isRefreshing)) || (!currentAjaxBlockId && isModal)
        simpleLog("shouldRender4 => doRender = $doRender && $poke")
        return doRender
    }

    boolean shouldRenderLayout() {
        simpleLog("shouldRenderLayout ${parameter.isAjaxRendering}, ${parameter.ajaxBlockId} $currentAjaxBlockId ${parameter.targetAjaxBlockId}")
        return !parameter.isAjaxRendering
    }
}
