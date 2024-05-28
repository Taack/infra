package taack.ui.dump.html

import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity
import taack.ast.type.FieldInfo
import taack.ast.type.WidgetKind
import taack.render.TaackUiOverriderService
import taack.ui.EnumOptions
import taack.ui.IEnumOption
import taack.ui.IEnumOptions
import taack.ui.base.form.FormSpec
import taack.ui.base.form.IUiFormVisitor
import taack.ui.dump.Parameter
import taack.ui.dump.theme.elements.base.*
import taack.ui.dump.theme.elements.form.BootstrapForm
import taack.ui.dump.theme.elements.form.IFormTheme

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.text.NumberFormat
import java.text.SimpleDateFormat

@CompileStatic
final class RawHtmlFormDump implements IUiFormVisitor {

    private final static String ST_ID = 'id'
    private final static String ST_NAME = 'name'
    private final static String ST_VALUES = 'values'
    private final static String ST_LIST = 'list'
    private final static String ST_CL_DIV = '</div>'
    private final static String ST_CL_SELECT = '</select>'
    private final static String ST_CL_FIELDSET = '</fieldset>'
    private final static String ST_CL_SELECT_DIV = ST_CL_SELECT + ST_CL_DIV

    final private ByteArrayOutputStream out
    final private Parameter parameter

    boolean isActionButtonPrimary = true

    private Object aObject

    private int tabOccurrence = 0
    private int tabIds = 0
    private FieldInfo[] lockedFields

    IFormTheme formThemed
    IHTMLElement topElement

    RawHtmlFormDump(final ByteArrayOutputStream out, final Parameter parameter) {
        this.out = out
        this.parameter = parameter
    }

    static String inputEscape(final String val) {
        val?.replace('"', '&quot;')?.replace('\'', '&#39;')?.replace('\n', '')?.replace('\r', '')
    }

    private boolean isDisabled(FieldInfo field) {
        if (lockedFields == null) {
            return false
        } else if (lockedFields.size() == 0) {
            return true
        } else if (lockedFields.size() > 0 && lockedFields[0] == null) {
            return !lockedFields*.fieldName.contains(field.fieldName)
        } else {
            return aObject.hasProperty(ST_ID) && (lockedFields*.fieldName.contains(field.fieldName) || lockedFields.length == 0)
        }
    }

    @Override
    void visitForm(final Object aObject, final FieldInfo[] lockedFields = null) {
        this.lockedFields = lockedFields
        this.aObject = aObject
        parameter.aClassSimpleName = aObject.class.simpleName
        String id = aObject.hasProperty(ST_ID) ? (aObject[ST_ID] != null ? aObject[ST_ID] : "") : ""
        formThemed = new BootstrapForm().builder.setTaackTag(TaackTag.FORM).addChildren(
                new HTMLInput(InputType.HIDDEN, id, 'id'),
                new HTMLInput(InputType.HIDDEN, aObject.class.name, 'className'),
                new HTMLInput(InputType.HIDDEN, parameter.applicationTagLib.controllerName, 'originController'),
                new HTMLInput(InputType.HIDDEN, parameter.applicationTagLib.actionName, 'originAction'),
                new HTMLInput(InputType.HIDDEN, parameter.brand, 'originBrand')
        ).build() as IFormTheme
        topElement = formThemed
    }

    @Override
    void visitFormEnd() {
        out << formThemed.output
        tabIds = 0
    }

    @Override
    void visitFormSection(String i18n, FormSpec.Width width = FormSpec.Width.DEFAULT_WIDTH) {
        IHTMLElement top = topElement
        topElement = new HTMLDiv()
        top.builder.addChildren(
                formThemed.section(topElement, width.sectionCss.split(' '))
        )
    }

    @Override
    void visitFormSectionEnd() {
        IHTMLElement top = topElement
        while (top.taackTag != TaackTag.SECTION) {
            top = top.parent
        }
        topElement = top
    }

    private String inputOverride(final String qualifiedName, FieldInfo field, String result) {
        if (aObject instanceof GormEntity) {
            GormEntity entity = aObject as GormEntity
            if (entity.ident() && TaackUiOverriderService.hasInputOverride(field)) {
                String img = TaackUiOverriderService.formInputPreview(entity, field)
                String txt = TaackUiOverriderService.formInputSnippet(entity, field)
                String val = TaackUiOverriderService.formInputValue(entity, field)
                topElement.addChildren(formThemed.inputOverride(qualifiedName, val, txt, img, inputEscape(result)))
            }
        }
        null
    }

    private String inputField(final String qualifiedName, final FieldInfo field, final IEnumOptions eos = null, final NumberFormat nf = null) {
        final Class type = field.fieldConstraint.field.type
        final boolean isEnum = field.fieldConstraint.field.type.isEnum()
        final boolean isListOrSet = Collection.isAssignableFrom(type)
        final boolean isBoolean = type == boolean || type == Boolean
        final boolean isDate = Date.isAssignableFrom(type)
        StringBuffer result = new StringBuffer()

        if (isBoolean) {
            formThemed.booleanInput(topElement, qualifiedName, field.value as boolean)
        } else if (eos) {
            formThemed.selects(topElement, eos, isListOrSet, isDisabled(field), field.fieldConstraint.nullable)
        } else if (isEnum || isListOrSet) {
            if (isEnum) {
                formThemed.selects(topElement, new EnumOptions(field.fieldConstraint.field.type as Class<Enum>, qualifiedName), isListOrSet, isDisabled(field), field.fieldConstraint.nullable, field.value?.toString())
            } else if (isListOrSet) {
                if (field.fieldConstraint.field.genericType instanceof ParameterizedType) {
                    final ParameterizedType parameterizedType = field.fieldConstraint.field.genericType as ParameterizedType
                    final Type actualType = parameterizedType.actualTypeArguments.first()
                    final Class actualClass = Class.forName(actualType.typeName)
                    final boolean isEnumListOrSet = actualClass.isEnum()
                    if (isEnumListOrSet) {
                        String[] values = actualClass.invokeMethod(ST_VALUES, null) as String[]
                        formThemed.selects(topElement, new EnumOptions(field.fieldConstraint.field.type as Class<Enum>, qualifiedName), isListOrSet, isDisabled(field), field.fieldConstraint.nullable, values)
                    } else {
                        String[] values = actualClass.invokeMethod(ST_LIST, null)[ST_ID] as String[]
                        formThemed.selects(topElement, new EnumOptions(field.fieldConstraint.field.type as Class<Enum>, qualifiedName), isListOrSet, isDisabled(field), field.fieldConstraint.nullable, values)
                    }
                }
            }
        } else if (isDate) {
            formThemed.dateInput(topElement, qualifiedName, field.value as Date)
        } else {
            if (field.fieldConstraint.widget == WidgetKind.TEXTAREA.name) {
                formThemed.textareaInput(topElement, qualifiedName, field.value as String)
            } else if (field.fieldConstraint.widget == WidgetKind.FILE_PATH.name) {
                formThemed.fileInput(topElement, qualifiedName, field.value as String)
            } else if (field.fieldConstraint.widget == WidgetKind.MARKDOWN.name) {
                result.append """\
                <div id="${qualifiedName}-editor">
                    <textarea id="${qualifiedName}" name="${qualifiedName}" class="wysiwyg-content markdown many-to-one pure-u-12-24" autocomplete="off" ${isDisabled(field) ? "disabled" : ""} rows="8">${field.value ?: ""}</textarea>
                    <div id="${qualifiedName}-markdown-preview" class="pure-u-10-24 markdown-body wysiwyg-markdown-preview"></div>
                    <input value="" readonly="on" class="many-to-one taackAjaxFormM2O" autocomplete="off" id="${qualifiedName}-attachment-select" taackAjaxFormM2OInputId="${qualifiedName}-attachment-link" taackAjaxFormM2OAction="${parameter.urlMapped('markdown', 'selectAttachment')}"/>
                    <input value="" type="hidden" id="${qualifiedName}-attachment-link"/>
                </div>\
                """.stripIndent().strip()
            } else if (field.fieldConstraint.widget == WidgetKind.ASCIIDOC.name) {
                result.append """\
                <div id="${qualifiedName}-editor">
                    <div id="${qualifiedName}" contenteditable="true" class="wysiwyg-content asciidoctor" style="width: 90%" ${isDisabled(field) ? "disabled" : ""}>${field.value ?: ''}</div>
                </div>\
                """.stripIndent().strip()
            } else {
                String valueString = inputEscape(field.value?.toString())
                if (nf && field.value instanceof Number) {
                    valueString = nf.format(field.value)
                }
                formThemed.fileInput(topElement, qualifiedName, valueString)
            }
        }
        return inputOverride(qualifiedName, field, result.toString()) ?: result.toString()
    }

    @Override
    void visitFormField(final String i18n, final FieldInfo field, final IEnumOptions eos = null, NumberFormat numberFormat = null) {
        final String trI18n = i18n ?: parameter.trField(field)

        if (field.fieldConstraint.constraints) {
            if (field.fieldConstraint.constraints.widget == WidgetKind.AJAX.name) {
                visitFormAjaxField(trI18n, null, null, field, null, null)
                return
            }
        }
        final String qualifiedName = field.fieldName
        final Class type = field.fieldConstraint.field.type
        final boolean isBoolean = type == boolean || type == Boolean

        out << """
                <div class="pure-u-1">
                <div class="pure-u-1 taackFieldError" taackFieldError="${qualifiedName}" style="display: none;"></div>
                <div class="pure-u-1 ${qualifiedName}-field-form ${isBoolean ? 'vertical-center ' : ''}">
                    <label for="${qualifiedName}">
                        ${trI18n}
                    </label>
                </div>
            """
        out << inputField(qualifiedName, field, eos, numberFormat ?: parameter.nf)
        out << ST_CL_DIV
    }

    private void formAjaxFieldLabel(final String i18n, final String qualifiedName) {
        out << """
                <div class="pure-u-1">
                    <div class="pure-u-1 taackFieldError" taackFieldError="${qualifiedName}" style="display: none;"></div>
                    <label for="${qualifiedName}">
                        ${i18n}
                    </label>
            """
    }

    @Override
    void visitFormAjaxField(String i18n, String controller, String action, FieldInfo field, Long id, Map<String, ?> params, FieldInfo[] fieldInfos) {
        final String trI18n = i18n ?: parameter.trField(field)

        final boolean isListOrSet = Collection.isAssignableFrom(field.fieldConstraint.field.type)
        final String qualifiedName = field.fieldName
        final String fieldInfoParams = fieldInfoParams(fieldInfos)
        final boolean isFieldDisabled = isDisabled(field)
        formAjaxFieldLabel(trI18n, qualifiedName)

        if (isListOrSet) {
            int occ = 0
            field.value.each {
                boolean isString = String.isAssignableFrom(it.class)
                out << """
                    <span class="M2MParent">
                        ${isFieldDisabled ? "" : '<img class="deleteIconM2M" src="/assets/taack/icons/actions/delete.svg" width="16" onclick="this.parentElement.innerText="";" style="margin: 5px 15px 0 0;">'}
                        <input value="${it ? inputEscape(it.toString()) : ''}" readonly="on" class="many-to-one pure-u-22-24 ${isFieldDisabled ? "" : "taackAjaxFormM2M"}" autocomplete="off" id="${qualifiedName}${parameter.modalId}-${occ}" taackAjaxFormM2MInputId="ajaxBlock${parameter.modalId}Modal-${qualifiedName}-${occ}" taackAjaxFormM2MAction="${parameter.urlMapped(controller, action, id, params)}" $fieldInfoParams/>
                        <input value="${it ? (isString ? it : it[ST_ID]) : ''}" type="hidden" name="${qualifiedName}" attr-name="${qualifiedName}" id="ajaxBlock${parameter.modalId}Modal-${qualifiedName}-${occ}"/>
                    </span>
                    """
                occ++
            }
            if (!isFieldDisabled) {
                out << """
                <span class="M2MToDuplicate">
                    <img class="deleteIconM2M" src="/assets/taack/icons/actions/delete.svg" width="16" onclick="this.parentElement.innerText='';">
                    <input value="" readonly="on" class="many-to-one pure-u-22-24 ${isFieldDisabled ? "" : "taackAjaxFormM2M"}" autocomplete="off" id="${qualifiedName}${parameter.modalId}-${occ}" taackAjaxFormM2MInputId="ajaxBlock${parameter.modalId}Modal-${qualifiedName}-${occ}" taackAjaxFormM2MAction="${parameter.urlMapped(controller, action, id, params)}" $fieldInfoParams/>
                    <input value="" type="hidden" attr-name="${qualifiedName}" id="ajaxBlock${parameter.modalId}Modal-${qualifiedName}-${occ}"/>
                </span>
                """
            }
            out << ST_CL_DIV
        } else if (String.isAssignableFrom(field.fieldConstraint.field.type)) {
            out << """
                ${isFieldDisabled ? "" : """<img class="deleteIconM2M" src="/assets/taack/icons/actions/delete.svg" width="16" onclick="document.getElementById('ajaxBlock${parameter.modalId}Modal-${qualifiedName}').value='';document.getElementById('${qualifiedName}${parameter.modalId}').value='';">"""}
                <input value="${field.value ?: ''}" readonly="on" class="many-to-one pure-u-22-24 ${isFieldDisabled ? "" : "taackAjaxFormM2O"}" autocomplete="off" id="${qualifiedName}${parameter.modalId}" taackAjaxFormM2OInputId="ajaxBlock${parameter.modalId}Modal-${qualifiedName}" taackAjaxFormM2OAction="${parameter.urlMapped(controller, action, id, params)}" $fieldInfoParams/>
                <input value="${field.value ? field.value : ''}" type="hidden" name="${qualifiedName}" id="ajaxBlock${parameter.modalId}Modal-${qualifiedName}"/>
            """
            out << ST_CL_DIV
        } else {
            String rep = """\
                ${isFieldDisabled ? "" : """<img class="deleteIconM2M" src="/assets/taack/icons/actions/delete.svg" width="16" onclick="document.getElementById('ajaxBlock${parameter.modalId}Modal-${qualifiedName}').value='';document.getElementById('${qualifiedName}${parameter.modalId}').value='';">"""}
                <input value="${field.value ?: ''}" readonly="on" class="many-to-one pure-u-22-24 ${isFieldDisabled ? "" : "taackAjaxFormM2O"}" autocomplete="off" id="${qualifiedName}${parameter.modalId}" taackAjaxFormM2OInputId="ajaxBlock${parameter.modalId}Modal-${qualifiedName}" taackAjaxFormM2OAction="${parameter.urlMapped(controller, action, id, params)}" $fieldInfoParams/>
                <input value="${field.value ? field.value[ST_ID] : ''}" type="hidden" name="${qualifiedName}" id="ajaxBlock${parameter.modalId}Modal-${qualifiedName}"/>\
            """.stripIndent().strip()

            String res = inputOverride(qualifiedName, field, rep)

            res ?= rep
            out << res
            out << ST_CL_DIV
        }
    }

    private static String fieldInfoParams(FieldInfo[] fieldInfos) {
        fieldInfos ? "taackFieldInfoParams=\"${fieldInfos.collect { it.value?.hasProperty(ST_ID) ? "${it.fieldName}.id" : it.fieldName }.join(',')}\"" : ""
    }

    @Override
    void visitFormAjaxField(String i18n, String controller, String action, FieldInfo field, IEnumOptions enumOptions, FieldInfo[] fieldInfos) {
        final String trI18n = i18n ?: parameter.trField(field)
        final String qualifiedName = field.fieldName
        final boolean isFieldDisabled = isDisabled(field)
        final String fieldInfoParams = fieldInfoParams(fieldInfos)
        formAjaxFieldLabel(trI18n, qualifiedName)

        IEnumOption[] enumConstraints = enumOptions.options
        out << """
                <div class="pure-u-1">
                ${isFieldDisabled ? "" : """<img class="deleteIconM2M" src="/assets/taack/icons/actions/delete.svg" width="16" onclick="this.nextElementSibling.value='';">"""}
                <select name="${qualifiedName}" id="${qualifiedName}Select" ${isFieldDisabled ? "disabled" : ""} class="many-to-one pure-u-22-24 ${isFieldDisabled ? "" : 'taackAjaxFormSelectM2O'}" autocomplete="off" id="${qualifiedName}${parameter.modalId}" taackAjaxFormM2OSelectId="ajaxBlock${parameter.modalId}Modal-${qualifiedName}" taackAjaxFormM2OAction="${parameter.urlMapped(controller, action)}" $fieldInfoParams/>
                <option value=""></option>
                """

        enumConstraints.each {
            out << """<option value="${it.key}" ${field.value == it.key ? 'selected' : ''}>${it.value}</option>"""
        }
        out << ST_CL_SELECT_DIV

    }

    @Override
    void visitFormTabs(List<String> names, FormSpec.Width width = FormSpec.Width.DEFAULT_WIDTH) {
        out << """<div class="pc-tab ${width.sectionCss}">"""
        names.eachWithIndex { it, occ ->
            out << """<input ${occ == 0 ? 'checked="checked"' : ''} id="tab${occ + 1}-f${tabIds}" class="inputTab${occ + 1}" type="radio" name="pct-${tabIds}" />"""
        }
        out << '<nav><ul>'
        names.eachWithIndex { it, occ ->
            out << """
                <li class="tab${occ + 1}">
                    <label for="tab${occ + 1}-f${tabIds}">${it}</label>
                </li>
            """
        }
        out << '</ul></nav>'
        out << '<section>'
        tabIds++
    }

    @Override
    void visitFormTabsEnd() {
        if (!isActionButtonPrimary) out << "</fieldset>"
        isActionButtonPrimary = true
        tabOccurrence = 0
        out << '</section></div>'
    }

    @Override
    void visitFormTab(String name) {
        out << """<div class="tab${++tabOccurrence} pure-g">"""
    }

    @Override
    void visitFormTabEnd() {
        out << ST_CL_DIV
    }

    @Override
    void visitFormAction(String i18n, String controller, String action, Long id, Map params, boolean isAjax) {
        i18n ?= parameter.trField(controller, action)
        if (isActionButtonPrimary) out << "<fieldset style=\"width: 100%; text-align: right;\">"
        out << """<button type="submit" class="pure-button ${isActionButtonPrimary ? 'pure-button-primary' : ''} ${isAjax ? "taackFormAction" : ""}" formaction="${parameter.urlMapped(controller, action, id, params)}">${i18n}</button>"""
        isActionButtonPrimary = false
    }


    @Override
    void visitFormFieldFromMap(final String i18n, final FieldInfo field, final String mapEntry) {
        final String trI18n = i18n ?: parameter.trField(field) ?: mapEntry
        final String qualifiedName = field.fieldName + '.' + mapEntry
        final String value = (field.value as Map<String, String>)?.get(mapEntry)
        out << """
                <div class="pure-u-1">
                <div class="pure-u-1 taackFieldError" taackFieldError="${qualifiedName}" style="display: none;"></div>
                    <label for="${qualifiedName}">
                        ${trI18n}
                    </label>
            """
        if (field.fieldConstraint.widget == WidgetKind.TEXTAREA.name) {
            out << """<textarea name="${qualifiedName}" class="many-to-one pure-u-1" autocomplete="off" rows="8">${value ?: ''}</textarea>"""
        } else if (field.fieldConstraint.widget == WidgetKind.FILE_PATH.name) {
            out << """
                <input id="${qualifiedName}" name="${qualifiedName}"  type="file" class="many-to-one pure-u-1" autocomplete="off" ${field.fieldConstraint.nullable ? '' : 'required=""'} value="${value ?: ''}" list="${qualifiedName}List">
                <datalist id="${qualifiedName}List"></datalist>
                """
        } else {
            out << """
                <input id="${qualifiedName}" name="${qualifiedName}"  type="${field.fieldConstraint.widget == WidgetKind.PASSWD.name ? "password" : "text"}" class="many-to-one pure-u-1" autocomplete="off" ${field.fieldConstraint.nullable ? '' : 'required=""'} value="${value ? inputEscape(value) : ''}" list="${qualifiedName}List">
                <datalist id="${qualifiedName}List"></datalist>
                """
        }

        out << ST_CL_DIV

    }

    @Override
    void visitCol() {
        out << "<div class='pure-u-1 pure-u-md-1-2'>"
    }

    @Override
    void visitColEnd() {
        isActionButtonPrimary = true
        out << ST_CL_DIV
    }

    @Override
    void visitFormHiddenField(FieldInfo fieldInfo) {
        final String qualifiedName = fieldInfo.fieldName
        final boolean isListOrSet = Collection.isAssignableFrom(fieldInfo.fieldConstraint.field.type)

        String value
        if (fieldInfo.value?.hasProperty(ST_ID)) {
            value = "${fieldInfo.value.getAt(ST_ID)}"
            out << "<input type=\"hidden\" name=\"${qualifiedName}.id\" value=\"${value}\"/>"
        } else if (isListOrSet) {
            fieldInfo.value.eachWithIndex { Object it, Integer occ ->
                if (it instanceof GormEntity)
                    out << "<input type=\"hidden\" name=\"${qualifiedName}[$occ].id\" value=\"${it.ident()}\"/>"
            }
        } else {
            value = fieldInfo.value ?: ''
            out << "<input type=\"hidden\" name=\"${qualifiedName}\" value=\"${value}\"/>"
        }
    }

}