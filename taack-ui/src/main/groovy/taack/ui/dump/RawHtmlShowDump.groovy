package taack.ui.dump

import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo
import taack.ast.type.GetMethodReturn
import taack.ast.type.WidgetKind
import taack.render.TaackUiEnablerService
import taack.ui.IEnumOption
import taack.ui.dsl.common.ActionIcon
import taack.ui.dsl.common.Style
import taack.ui.dsl.helper.Utils
import taack.ui.dsl.show.IUiShowVisitor

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.text.NumberFormat
import java.text.SimpleDateFormat

@CompileStatic
final class RawHtmlShowDump implements IUiShowVisitor {

    final private ByteArrayOutputStream out
    final private Parameter parameter

    final String blockId
    private String controller
    private String action
    private Object aObject

    RawHtmlShowDump(final String id, final ByteArrayOutputStream out, final Parameter parameter) {
        this.out = out
        this.parameter = parameter
        this.blockId = id
    }

    @Override
    void visitShow(Object currentObject, String controller = null, String action = null) {
        this.aObject = currentObject
        this.controller = controller
        this.action = action

        out << "<div class='property-list taackShow col-12'>"
    }

    @Override
    void visitShowEnd() {
        out << "</div>"
    }

    @Override
    void visitSection(String i18n) {
        out << '<div>'
    }

    @Override
    void visitSectionEnd() {
        out << '</div>'
    }

    @Override
    void visitShowFieldUnLabeled(Style style, FieldInfo... fields) {
        visitShowField(null, fields.last(), style)
    }

    @Override
    void visitShowFieldLabeled(Style style, FieldInfo... fields) {
        visitShowField(parameter.trField(fields), fields.last(), style)
    }

    @Override
    void visitShowFieldUnLabeled(Style style, GetMethodReturn methodReturn) {
        visitShowField(null, methodReturn.value?.toString(), style)
    }

    @Override
    void visitShowFieldLabeled(Style style, GetMethodReturn methodReturn) {
        visitShowField(parameter.trField(methodReturn), methodReturn.value?.toString(), style)
    }

    private static String showField(String i18n, String field, Style style, boolean sanitize = true) {
        if (i18n) {
            """
                <li class="fieldcontain">
                    <span class="property-label ref-prefix">${i18n}</span>
                    <span class="property-value ${style ? style.cssClassesString : ''}" style="${style ? style.cssStyleString : ''}">${field}</span>
                </li> 
        """
        } else {
            """<div class="${style ? style.cssClassesString : ''}" style="${style ? style.cssStyleString : ''}">${sanitize ? TaackUiEnablerService.sanitizeString(field) : field}</div>  """
        }
//        """
//                <li class="fieldcontain">
//                    ${i18n?"""<span class="property-label ref-prefix">${i18n}</span>""":""}
//                    <span class="${i18n?"property-value":""} ${style ? style.cssClassesString : ''}">${field}</span>
//                </li>
//        """
    }

    @Override
    void visitShowField(final String i18n, final FieldInfo field, final Style style) {
        if (field?.value)
            out << showField(i18n, RawHtmlTableDump.dataFormat(field.value, null), style, false)
    }

    @Override
    void visitShowField(final String i18n, final String field, final Style style) {
        if (field) {
            out << showField(i18n, field, style)
        }
    }

    @Override
    void visitShowField(String html) {
        out << html
    }

    private static String inputField(final String qualifiedName, final FieldInfo field, final IEnumOption[] eos = null, final String ajax = '', final NumberFormat nf = null) {
        final String showClass = 'taackShowInput'
        final Class type = field.fieldConstraint.field.type
        final boolean isEnum = field.fieldConstraint.field.type.isEnum()
        final boolean isListOrSet = Collection.isAssignableFrom(type)
        final boolean isBoolean = type == boolean || type == Boolean
        final boolean isDate = Date.isAssignableFrom(type)
        StringBuffer result = new StringBuffer()
        if (isBoolean) {
            result.append """
                    <input type="checkbox" $ajax name="${qualifiedName}" value="1" id="${qualifiedName}Check" ${field.value ? 'checked=""' : ''} class="many-to-one pure-u-22-24 ${showClass} ">
                    <input type="hidden" name="${qualifiedName}" value="0" id="${qualifiedName}Check" ${!field.value ? 'checked=""' : ''} class="many-to-one pure-u-22-24">
                    """
        } else if (eos) {
            IEnumOption[] enumConstraints = eos
            result.append """
                <div class="center-flex pure-u-1">
                <select $ajax name="${qualifiedName}" id="${qualifiedName}Select" ${isListOrSet ? "multiple" : ""} class="${showClass}" >
                <option value=""></option>
                """

            enumConstraints.each {
                if (isListOrSet) {
                    result.append """<option value="${it.key}" ${(field.value as Collection).contains(it.key) ? 'selected' : ''}>${it.value}</option>"""
                } else {
                    result.append """<option value="${it.key}" ${field.value == it.key ? 'selected' : ''}>${it.value}</option>"""
                }
            }
            result.append '</select></div>'
        } else if (isEnum || isListOrSet) {
            result.append """
                <div class="center-flex pure-u-1">
                <select $ajax name="${qualifiedName}" id="${qualifiedName}Select" ${isListOrSet ? "multiple" : ""} class="${showClass}">
                <option value=""></option>
                """

            if (isEnum) {
                List<String> values = type.invokeMethod("values", null) as List<String>
                values.each {
                    result.append """<option value="${it}" ${field.value == it ? 'selected="selected"' : ''}>${it}</option>"""
                }
            } else if (isListOrSet) {
                if (field.fieldConstraint.field.genericType instanceof ParameterizedType) {
                    final ParameterizedType parameterizedType = field.fieldConstraint.field.genericType as ParameterizedType
                    final Type actualType = parameterizedType.actualTypeArguments.first()
                    final Class actualClass = Class.forName(actualType.typeName)
                    final boolean isEnumListOrSet = actualClass.isEnum()
                    if (isEnumListOrSet) {
                        List values = actualClass.invokeMethod('values', null) as List
                        values.each {
                            result.append """<option value="${it}" ${(field.value as Collection)*.toString().contains(it.toString()) ? 'selected="selected"' : ''}>${it}</option>"""
                        }
                    } else {
                        List values = actualClass.invokeMethod('list', null) as List
                        values.each {
                            result.append """<option value="${it["id"]}" ${((field.value as Collection)*.getAt("id") as List)?.contains(it["id"]) ? 'selected="selected"' : ''}>${it}</option>"""
                        }
                    }
                }
            }
            result.append '</select></div>'
        } else if (isDate) {
            String date = new SimpleDateFormat("yyyy-MM-dd").format(field.value ?: new Date() as Date)
            result.append """
                <input id="${qualifiedName}" $ajax name="${qualifiedName}" type="date" class="many-to-one pure-u-22-24 $showClass" autocomplete="off" ${date ? '' : 'required=""'} value="${date ?: ''}" list="${qualifiedName}List">
                <datalist id="${qualifiedName}List"></datalist>
                """
        } else {
            if (field.fieldConstraint.widget == WidgetKind.TEXTAREA.name) {
                result.append """<textarea id="${qualifiedName}" name="${qualifiedName}" $ajax class="many-to-one pure-u-22-24 $showClass" autocomplete="off">${field.value ?: ''}</textarea>"""
            } else if (field.fieldConstraint.widget == WidgetKind.FILE_PATH.name) {
                result.append """
                <input id="${qualifiedName}" name="${qualifiedName}" $ajax type="file" class="many-to-one pure-u-22-24 $showClass" autocomplete="off" ${field.fieldConstraint.nullable ? '' : 'required=""'} value="${field.value ?: ''}" list="${qualifiedName}List">
                """
            } else {

                String valueString = field.value
                if (nf && field.value instanceof Number) {
                    valueString = nf.format(field.value)
                }
                result.append """
                <input id="${qualifiedName}" name="${qualifiedName}" $ajax type="text" class="many-to-one pure-u-22-24 $showClass" autocomplete="off" ${field.fieldConstraint.nullable ? '' : 'required=""'} value="${valueString ?: ''}" list="${qualifiedName}List">
                <datalist id="${qualifiedName}List"></datalist>
                """
            }
        }
        result.toString()
    }

    @Override
    void visitShowInputField(String i18n, FieldInfo field, boolean isAjax = false) {
        String widget
        final String qualifiedName = field.fieldName

        if (!isAjax) {
            if (field.fieldConstraint.widget == WidgetKind.AJAX.name) {
                widget = """
                <img class="close" src="/assets/delete.png" width="16" onclick="document.getElementById('ajaxBlock${parameter.modalId}Modal-${qualifiedName}').value='';document.getElementById('${qualifiedName}${parameter.modalId}').value='';">
                <input value="${field.value ?: ''}" readonly="on" class="many-to-one pure-u-22-24" autocomplete="off" id="${qualifiedName}${parameter.modalId}" inputId="ajaxBlock${parameter.modalId}Modal-${qualifiedName}" onclick="openAutocompleteSelect(this, 'ajaxBlock${parameter.modalId}Modal', '${parameter.applicationTagLib.controllerName}', '${field.fieldConstraint.field.declaringClass.name}', '${qualifiedName}');"/>
                <input value="${field.value ? field.value["id"] : ''}" type="hidden" name="${qualifiedName}" id="ajaxBlock${parameter.modalId}Modal-${qualifiedName}"/>
            """
            } else {
                widget = inputField(qualifiedName, field, null, null, parameter.nf)
            }

            out << """
                <div class="fieldcontain">
                    <span class="property-label ref-prefix">${i18n}</span>
                    <span class="property-value">
                        <form action="/${controller}/${action}" method="post" class="ng-pristine ng-valid">
                            <input name="id" type="hidden" value="${aObject["id"]}" autocomplete="off">
                            <input name="className" type="hidden" value="${field.fieldConstraint.field.declaringClass.name}" autocomplete="off">
                            <input name="fieldName" type="hidden" value="${field.fieldName}" autocomplete="off">
                            <input type="hidden" name="returnAction" value="${action ?: ''}">
                            <input type="hidden" name="returnController" value="${controller ?: ''}">
                            ${widget}
                        </form>
                    </span>
                </div>
            """
        } else {
            // TODO split visitShowInputField, remove isAjax parameter (will be only ajax) with the capability to override MC from field spec
            Map<String, String> params = [
                    className   : field.fieldConstraint.field.declaringClass.name, fieldName: "${field.fieldName}".toString(),
                    returnAction: "${action ?: ''}".toString(), returnController: "${controller ?: ''}".toString()
            ]
            widget = inputField(qualifiedName, field, null, """
                 ajaxController="/${controller}" ajaxAction="/${action}" ajaxParams="${Utils.paramsString(params)}"
            """, parameter.nf)

            out << widget
        }
    }

    @Override
    void visitShowAction(String i18n, ActionIcon actionIcon, String controller, String action, Long id, Map<String, Object> additionalParams, boolean isAjax) {
        i18n ?= parameter.trField(controller, action, id != null)
        if (isAjax) {
            out << """
                     <div class='icon'>
                        <a class='ajaxLink taackShowAction' ajaxAction='${parameter.urlMapped(controller, action, id, additionalParams)}'>
                            ${actionIcon.getHtml(i18n)}
                        </a>
                     </div>
                """
        } else {
            out << """
                 <div class='icon'>
                    <a class='link' href="${parameter.urlMapped(controller, action, id, additionalParams)}">
                        ${actionIcon.getHtml(i18n)}
                    </a>
                 </div>
                """
        }
    }

    @Override
    void visitShowAction(String i18n, String linkText, String controller, String action, Long id, Map additionalParams, boolean isAjax = true) {
        if (linkText) {
            additionalParams ?= [:]
            additionalParams['isAjax'] = isAjax
            String link = """<a class="taackShowAction" ${isAjax ? "ajaxAction" : "href"}="${parameter.urlMapped(controller, action, id, additionalParams)}">${linkText}</a>"""
            out << showField(i18n, link, null)
        }
    }

    @Override
    void visitShowInlineHtml(String html, String additionalCSSClass) {
        out << "</div>"
        out << """<div class="$additionalCSSClass">$html</div>"""
        out << "<div class='property-list taackShow col-12'>"
    }

}
