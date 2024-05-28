package taack.ui.dump.html

import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity
import taack.ast.type.FieldInfo
import taack.ast.type.WidgetKind
import taack.render.TaackUiOverriderService
import taack.ui.EnumOptions
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

@CompileStatic
final class RawHtmlFormDump implements IUiFormVisitor {

    private final static String ST_ID = 'id'
    private final static String ST_VALUES = 'values'
    private final static String ST_LIST = 'list'

    final private ByteArrayOutputStream out
    final private Parameter parameter

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

    private void closeTags(TaackTag tag) {
        IHTMLElement top = topElement
        while (top.taackTag != tag) {
            top = top.parent
        }
        topElement = top
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
                topElement = formThemed.section(topElement, width.sectionCss.split(' '))
        )
    }

    @Override
    void visitFormSectionEnd() {
        closeTags(TaackTag.SECTION)
    }

    private void inputOverride(final String qualifiedName, FieldInfo field) {
        if (aObject instanceof GormEntity) {
            GormEntity entity = aObject as GormEntity
            if (entity.ident() && TaackUiOverriderService.hasInputOverride(field)) {
                String img = TaackUiOverriderService.formInputPreview(entity, field)
                String txt = TaackUiOverriderService.formInputSnippet(entity, field)
                String val = TaackUiOverriderService.formInputValue(entity, field)
                topElement.addChildren(formThemed.inputOverride(topElement, qualifiedName, val, txt, img, topElement.parent))
            }
        }
    }

    private void inputField(final String qualifiedName, final FieldInfo field, final IEnumOptions eos = null, final NumberFormat nf = null) {
        final Class type = field.fieldConstraint.field.type
        final boolean isEnum = field.fieldConstraint.field.type.isEnum()
        final boolean isListOrSet = Collection.isAssignableFrom(type)
        final boolean isBoolean = type == boolean || type == Boolean
        final boolean isDate = Date.isAssignableFrom(type)
        final boolean isNullable = field.fieldConstraint.nullable
        final boolean isFieldDisabled = isDisabled(field)
        StringBuffer result = new StringBuffer()

        if (isBoolean) {
            topElement = formThemed.booleanInput(topElement, qualifiedName, isFieldDisabled, isNullable, field.value as boolean)
        } else if (eos) {
            topElement = formThemed.selects(topElement, eos, isListOrSet, isFieldDisabled, isNullable)
        } else if (isEnum || isListOrSet) {
            if (isEnum) {
                topElement = formThemed.selects(topElement, new EnumOptions(field.fieldConstraint.field.type as Class<Enum>, qualifiedName), isListOrSet, isDisabled(field), field.fieldConstraint.nullable, field.value?.toString())
            } else if (isListOrSet) {
                if (field.fieldConstraint.field.genericType instanceof ParameterizedType) {
                    final ParameterizedType parameterizedType = field.fieldConstraint.field.genericType as ParameterizedType
                    final Type actualType = parameterizedType.actualTypeArguments.first()
                    final Class actualClass = Class.forName(actualType.typeName)
                    final boolean isEnumListOrSet = actualClass.isEnum()
                    if (isEnumListOrSet) {
                        String[] values = actualClass.invokeMethod(ST_VALUES, null) as String[]
                        topElement = formThemed.selects(topElement, new EnumOptions(field.fieldConstraint.field.type as Class<Enum>, qualifiedName), isListOrSet, isDisabled(field), field.fieldConstraint.nullable, values)
                    } else {
                        String[] values = actualClass.invokeMethod(ST_LIST, null)[ST_ID] as String[]
                        topElement = formThemed.selects(topElement, new EnumOptions(field.fieldConstraint.field.type as Class<Enum>, qualifiedName), isListOrSet, isDisabled(field), field.fieldConstraint.nullable, values)
                    }
                }
            }
        } else if (isDate) {
            topElement = formThemed.dateInput(topElement, qualifiedName, isFieldDisabled, isNullable, field.value as Date)
        } else {
            if (field.fieldConstraint.widget == WidgetKind.TEXTAREA.name) {
                topElement = formThemed.textareaInput(topElement, qualifiedName, isFieldDisabled, isNullable, field.value as String)
            } else if (field.fieldConstraint.widget == WidgetKind.FILE_PATH.name) {
                topElement = formThemed.fileInput(topElement, qualifiedName, isFieldDisabled, isNullable, field.value as String)
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
                topElement = formThemed.fileInput(topElement, qualifiedName, isFieldDisabled, isNullable, valueString)
            }
        }
        inputOverride(qualifiedName, field)
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

        topElement = formThemed.formLabel(topElement, qualifiedName, i18n)
        inputField(qualifiedName, field, eos, numberFormat ?: parameter.nf)
    }

    private void formAjaxFieldLabel(final String i18n, final String qualifiedName) {
        topElement = formThemed.formLabel(topElement, qualifiedName, i18n)
    }

    @Override
    void visitFormAjaxField(String i18n, String controller, String action, FieldInfo field, Long id, Map<String, ?> params, FieldInfo[] fieldInfos) {
        final String trI18n = i18n ?: parameter.trField(field)

        final boolean isListOrSet = Collection.isAssignableFrom(field.fieldConstraint.field.type)
        final String qualifiedName = field.fieldName
        final String fieldInfoParams = fieldInfoParams(fieldInfos)
        final boolean isFieldDisabled = isDisabled(field)
        final boolean isNullable = field.fieldConstraint.nullable
        formAjaxFieldLabel(trI18n, qualifiedName)
        formThemed.ajaxField(topElement, field.value as List, qualifiedName, parameter.modalId, parameter.urlMapped(controller, action, id, params), fieldInfoParams, isFieldDisabled, isNullable, isListOrSet)
        inputOverride(qualifiedName, field)
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
        formThemed.ajaxField(topElement, enumOptions, field.value, qualifiedName, parameter.modalId, parameter.urlMapped(controller, action), fieldInfoParams, isFieldDisabled)
    }

    @Override
    void visitFormTabs(List<String> names, FormSpec.Width width = FormSpec.Width.DEFAULT_WIDTH) {
        topElement = formThemed.formTabs(topElement, tabIds, names, width)
        tabIds++
    }

    @Override
    void visitFormTabsEnd() {
        closeTags(TaackTag.TABS)
    }

    @Override
    void visitFormTab(String name) {
        topElement = formThemed.formTab(topElement, ++tabOccurrence)
    }

    @Override
    void visitFormTabEnd() {
        closeTags(TaackTag.TAB)
    }

    @Override
    void visitFormAction(String i18n, String controller, String action, Long id, Map params, boolean isAjax) {
        i18n ?= parameter.trField(controller, action)
        formThemed.formAction(topElement, parameter.urlMapped(controller, action, id, params), i18n)
    }


    @Override
    void visitFormFieldFromMap(final String i18n, final FieldInfo field, final String mapEntry) {
        final String trI18n = i18n ?: parameter.trField(field) ?: mapEntry
        final String qualifiedName = field.fieldName + '.' + mapEntry
        String value = (field.value as Map<String, String>)?.get(mapEntry)
        value = value ? inputEscape(value) : ''
        final boolean isFieldDisabled = isDisabled(field)
        final boolean isNullable = field.fieldConstraint.nullable
        topElement = formThemed.formLabel(topElement, qualifiedName, trI18n)
        if (field.fieldConstraint.widget == WidgetKind.TEXTAREA.name) {
            formThemed.textareaInput(topElement, qualifiedName, isFieldDisabled, isNullable, value)
        } else if (field.fieldConstraint.widget == WidgetKind.FILE_PATH.name) {
            formThemed.fileInput(topElement, qualifiedName, isFieldDisabled, isNullable, value)
        } else {
            if (field.fieldConstraint.widget == WidgetKind.PASSWD.name) {
                formThemed.passwdInput(topElement, qualifiedName, isFieldDisabled, isNullable, value)
            } else {
                formThemed.normalInput(topElement, qualifiedName, isFieldDisabled, isNullable, value)
            }
        }
    }

    @Override
    void visitCol() {
        topElement = formThemed.formCol(topElement)
    }

    @Override
    void visitColEnd() {
        closeTags(TaackTag.COL)
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
