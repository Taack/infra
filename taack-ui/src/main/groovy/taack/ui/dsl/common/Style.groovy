package taack.ui.dsl.common

import groovy.transform.CompileStatic
import taack.ui.dump.html.style.IStyleDescriptor

@CompileStatic
enum CssContent {
    COLOR_RED('taackColorRed'),
    COLOR_BLUE('taackColorBlue'),
    COLOR_GREEN('taackColorGreen'),
    COLOR_ORANGE('taackColorOrange'),
    COLOR_YELLOW('taackColorYellow'),

    FONT_BOLD('taackFontBold', 'font-weight: bold;'),
    FONT_ITALIC('taackFontItalic', 'font-style: italic;'),
    FONT_EMPHASIS('taackFontEmphasis', 'font-weight: bolder;font-size: larger;'),
    FONT_SMALLER('taackFontSmaller', 'font-size: small;'),

    FORM_DISABLE('taackCssDisabled', 'color: gray;'),
    FORM_ENABLED('taackCssDisabled', ''),

    BREAK_AFTER('taackCssDisabled', ''),
    NO_BREAK_AFTER('taackCssDisabled', ''),
    NO_BORDER('taackCssDisabled', ''),

    MARKDOWN_BODY('taackCssDisabled', '')

    CssContent(String classCss, String inlineCss = null) {
        this.classCss = classCss
        this.inlineCss = inlineCss
    }
    final String classCss
    final String inlineCss
}

@CompileStatic
class Style implements IStyleDescriptor {
    final static Style RED = new Style(CssContent.COLOR_RED)
    final static Style BLUE = new Style(CssContent.COLOR_BLUE)
    final static Style GREEN = new Style(CssContent.COLOR_GREEN)
    final static Style ORANGE = new Style(CssContent.COLOR_ORANGE)
    final static Style YELLOW = new Style(CssContent.COLOR_YELLOW)

    final static Style BOLD = new Style(CssContent.FONT_BOLD)
    final static Style ITALIC = new Style(CssContent.FONT_ITALIC)
    final static Style EMPHASIS = new Style(CssContent.FONT_EMPHASIS)
    final static Style SMALLER = new Style(CssContent.FONT_SMALLER)

    final static Style DISABLED = new Style(CssContent.FORM_DISABLE)
    final static Style ENABLED = new Style(CssContent.FORM_ENABLED)

    final static Style BREAK_AFTER = new Style(CssContent.BREAK_AFTER)
    final static Style NO_BREAK_AFTER = new Style(CssContent.NO_BREAK_AFTER)
    final static Style NO_BORDER = new Style(null, 'border: none;')

    final static Style TAG = new Style('tag')

    final static Style BLUE_TAG = new Style('blueTag')
    final static Style GREEN_TAG = new Style('greenTag')
    final static Style YELLOW_TAG = new Style('yellowTag')
    final static Style ORANGE_TAG = new Style('orangeTag')
    final static Style RED_TAG = new Style('redTag')
    final static Style PINK_TAG = new Style('pinkTag')
    final static Style GREY_TAG = new Style('greyTag')
    final static Style WHITE_TAG = new Style('whiteTag')

    final static Style ALIGN_RIGHT = new Style(null, 'text-align: right;', true)
    final static Style ALIGN_CENTER = new Style(null, 'text-align: center;', true)
    final static Style NOWRAP = new Style(null, 'white-space: nowrap;')

    final static Style LABEL_WIDTH_AUTO_MIN = new Style(null, 'width: 1%; white-space: nowrap;')
    final static Style LABEL_WIDTH90PX = new Style(null, 'width: 90px; display: inline-block;', false, true)
    final static Style LABEL_WIDTH120PX = new Style(null, 'width: 120px; display: inline-block;', false, true)
    final static Style LABEL_WIDTH150PX = new Style(null, 'width: 150px; display: inline-block;', false, true)
    final static Style LABEL_MARGIN_TOP5PX = new Style(null, 'margin-top: 5px;', false, true)
    final static Style LABEL_MARGIN_BOTTOM5PX = new Style(null, 'margin-bottom: 5px;', false, true)

    final static Style MARKDOWN_BODY = new Style('markdown-body')

    Style(final String cssClassesString, final String cssStyleString = null, final boolean isDiv = false, final boolean applyToLabel = false) {
        if (applyToLabel) {
            this.labelCssClassesString = cssClassesString
            this.labelCssStyleString = cssStyleString
            this.cssClassesString = ''
            this.cssStyleString = ''
            this.isDiv = isDiv
        } else {
            this.labelCssClassesString = ''
            this.labelCssStyleString = ''
            this.cssClassesString = cssClassesString
            this.cssStyleString = cssStyleString
            this.isDiv = isDiv
        }
    }


    Style(final CssContent cssContent) {
        this(cssContent.classCss, cssContent.inlineCss)
    }

    final String cssClassesString
    final String cssStyleString
    final boolean isDiv
    String labelCssClassesString
    String labelCssStyleString

    Style plus(Style other) {
        if (other == null) return this
        Style res = new Style((this.cssClassesString ?: '') + ' ' + (other.cssClassesString ?: ''), (this.cssStyleString ?: '') + ' ' + (other.cssStyleString ?: ''), this.isDiv || other.isDiv)
        res.labelCssClassesString += "${labelCssClassesString?:''} ${other.labelCssClassesString?:''}"
        res.labelCssStyleString += "${labelCssStyleString?:''} ${other.labelCssStyleString?:''}"
        res
    }

    @Override
    String getStyleOutput() {
        return cssStyleString
    }

    @Override
    String getClasses() {
        return cssClassesString
    }
}

