package taack.ui.dsl.common

import groovy.transform.CompileStatic
import org.apache.commons.lang.StringEscapeUtils

@CompileStatic
enum IconStyle {
    SCALE_DOWN("object-fit: scale-down;vertical-align: middle;float: right;max-height: 22px;max-width: 22px;position: relative;z-index: 100"),
    RIGHT("vertical-align: middle;float: right;max-height: 22px;"),
    LEFT("vertical-align: middle;float: left;max-height: 22px;")

    IconStyle(final String inlineStyle) {
        this.inlineStyle = inlineStyle
    }

    final String inlineStyle
}

@CompileStatic
class ActionIcon {
    final static ActionIcon OPERATOR_NOT = new ActionIcon("/assets/taack/icons/operators/not.svg")
    final static ActionIcon OPERATOR_PLUS = new ActionIcon("/assets/taack/icons/operators/plus.svg")

    final static ActionIcon OBJECT_CUSTOMER = new ActionIcon("/assets/taack/icons/objects/customer-auo.svg")
    final static ActionIcon OBJECT_PRODUCT = new ActionIcon("/assets/taack/icons/objects/product-auo.svg")
    final static ActionIcon OBJECT_RANGE = new ActionIcon("/assets/taack/icons/objects/range-auo.svg")
    final static ActionIcon OBJECT_FLAG = new ActionIcon("/assets/taack/icons/objects/flag_icon.svg")

    final static ActionIcon CREATE = new ActionIcon("/assets/taack/icons/actions/add.svg")
    final static ActionIcon EDIT = new ActionIcon("/assets/taack/icons/actions/edit.svg")
    final static ActionIcon SAVE = new ActionIcon("/assets/taack/icons/actions/save.svg")
    final static ActionIcon DOWN = new ActionIcon("/assets/taack/icons/actions/down.svg")
    final static ActionIcon SHOW = new ActionIcon("/assets/taack/icons/actions/show.svg")
    final static ActionIcon UP = new ActionIcon("/assets/taack/icons/actions/up.svg")
    final static ActionIcon DELETE = new ActionIcon("/assets/taack/icons/actions/delete.svg")
    final static ActionIcon IMPORT = new ActionIcon("/assets/taack/icons/actions/import.svg")
    final static ActionIcon EXPORT = new ActionIcon("/assets/taack/icons/actions/export.svg")
    final static ActionIcon EXPORT_PDF = new ActionIcon("/assets/taack/icons/actions/pdf.svg")
    final static ActionIcon EXPORT_CSV = new ActionIcon("/assets/taack/icons/actions/csv.svg")
    final static ActionIcon DOWNLOAD = new ActionIcon("/assets/taack/icons/actions/download.svg")
    final static ActionIcon ADD = new ActionIcon("/assets/taack/icons/actions/add.svg")
    final static ActionIcon SELECT = new ActionIcon("/assets/taack/icons/actions/select.svg")
    final static ActionIcon UNSELECT = new ActionIcon("/assets/taack/icons/actions/delete.svg")
    final static ActionIcon FILTER = new ActionIcon("/assets/taack/icons/actions/filter.svg")
    final static ActionIcon DETAILS = new ActionIcon("/assets/taack/icons/actions/details.svg")
    final static ActionIcon CONFIG = new ActionIcon("/assets/taack/icons/actions/settings.svg")
    final static ActionIcon CONFIG_USER = new ActionIcon("/assets/taack/icons/actions/settings.svg")
    final static ActionIcon SEARCH = new ActionIcon("/assets/taack/icons/actions/search.svg")
    final static ActionIcon REFRESH = new ActionIcon("/assets/taack/icons/actions/refresh.svg")
    final static ActionIcon MERGE = new ActionIcon("/assets/taack/icons/actions/merge.svg")
    final static ActionIcon REPLY = new ActionIcon("/assets/taack/icons/actions/reply.svg")
    final static ActionIcon COPY = new ActionIcon("/assets/taack/icons/actions/copy.svg")
    final static ActionIcon MAIL = new ActionIcon("/assets/taack/icons/actions/mail.svg")
    final static ActionIcon HELP = new ActionIcon("/assets/taack/icons/actions/help.svg")
    final static ActionIcon GRAPH = new ActionIcon("/assets/taack/icons/actions/graph-model.svg")
    final static ActionIcon CHART = new ActionIcon("/assets/taack/icons/actions/chart.svg")
    final static ActionIcon GANTT = new ActionIcon("/assets/taack/icons/actions/gantt.svg")
    final static ActionIcon TIMING = new ActionIcon("/assets/taack/icons/actions/timing.svg")
    final static ActionIcon START = new ActionIcon("/assets/taack/icons/actions/start.svg")
    final static ActionIcon STOP = new ActionIcon("/assets/taack/icons/actions/stop.svg")
    final static ActionIcon FOLLOW = new ActionIcon("/assets/taack/icons/actions/follow.svg")
    final static ActionIcon UNFOLLOW = new ActionIcon("/assets/taack/icons/actions/unfollow.svg")

    ActionIcon(final String src, final IconStyle style = null) {
        this.src = src
        this.style = style
    }

    final String src
    final IconStyle style

    ActionIcon plus(ActionIcon other) {
        return new ActionIcon(this.src + "+" + other.src)
    }

    ActionIcon multiply(ActionIcon other) {
        return new ActionIcon(this.src + "*" + other.src)
    }

    ActionIcon multiply(IconStyle other) {
        return new ActionIcon(this.src, other)
    }

    static private String escapeHtml(String input) {
        StringEscapeUtils.escapeHtml(input).replace("'", "&apos;")
    }

    String getHtml(final String title, final Integer width = 40) {
        boolean hasNextMult = src.contains("*")
        boolean hasNextAdd = src.contains("+")
        if (!hasNextMult && !hasNextAdd) return "<img src='$src' title='${escapeHtml(title)}' ${style?"style='${style.inlineStyle}'":" width='${width}'"}/>"
        StringBuffer res = new StringBuffer()
        res << """<span class="iconStack">"""
        String currentSrc
        String remainingSrc = src
        String previousSign = "+"
        while (hasNextMult || hasNextAdd) {
            String sign
            final int indexPlus = remainingSrc.indexOf('+')
            final int indexMult = remainingSrc.indexOf('*')
            int indexNextSign
            if (!hasNextMult || (hasNextAdd && indexPlus < indexMult)) {
                sign = "+"
                indexNextSign = indexPlus
            } else if (!hasNextAdd || (hasNextMult && indexMult < indexPlus)) {
                sign = "*"
                indexNextSign = indexMult
            }
            currentSrc = remainingSrc.substring(0, indexNextSign)
            remainingSrc = remainingSrc.substring(indexNextSign + 1)
            if (previousSign == "+") {
                res << "<img class='iconStackElement' src='$currentSrc' width='${width}' title='${escapeHtml(title)}'/>"
            } else {
                res << "<img class='iconStackElementExponent' src='$currentSrc' width='${width / 2}' title='${escapeHtml(title)}'/>"
            }
            hasNextMult = remainingSrc.contains("*")
            hasNextAdd = remainingSrc.contains("+")
            previousSign = sign
        }

        if (previousSign == "+") {
            res << "<img class='iconStackElement' src='$remainingSrc' width='${width}' title='${escapeHtml(title)}'/>"
        } else {
            res << "<img class='iconStackElementExponent' src='$remainingSrc' width='${width / 2}' title='${escapeHtml(title)}'/>"
        }
        res << "</span>"
        res.toString()
    }
}

