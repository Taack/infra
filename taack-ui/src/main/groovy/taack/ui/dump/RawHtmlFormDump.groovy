package taack.ui.dump

import grails.util.Triple
import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity
import taack.ast.type.FieldInfo
import taack.ast.type.WidgetKind
import taack.ui.EnumOptions
import taack.ui.IEnumOptions
import taack.ui.dsl.block.BlockSpec
import taack.ui.dump.common.BlockLog
import taack.ui.dump.html.element.ButtonStyle
import taack.ui.dump.html.element.HTMLInput
import taack.ui.dump.html.element.IHTMLElement
import taack.ui.dump.html.element.InputType
import taack.ui.dump.html.element.TaackTag
import taack.ui.dump.html.layout.BootstrapLayout
import taack.ui.dump.html.layout.HTMLEmpty
import taack.ui.dump.html.theme.ThemeSelector
import taack.ui.dsl.form.FormSpec
import taack.ui.dsl.form.IUiFormVisitor
import taack.ui.dump.html.form.BootstrapForm
import taack.ui.dump.html.form.IFormTheme

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.text.NumberFormat

@CompileStatic
final class RawHtmlFormDump implements IUiFormVisitor {

    private final static String ST_ID = 'id'
    private final static String ST_VALUES = 'values'
    private final static String ST_LIST = 'list'
    final private List<Triple<String, ButtonStyle, String>> formActions = []

    final private Parameter parameter

    private Object aObject
    final private Random random = new Random(0)

    private int tabOccurrence = 0
    private int tabIds = 0
    private FieldInfo[] lockedFields

    final IFormTheme formThemed
    final BlockLog blockLog

    RawHtmlFormDump(final BlockLog blockLog, final Parameter parameter) {
        this.blockLog = blockLog
        this.parameter = parameter
        formThemed = new BootstrapForm(blockLog)

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
         blockLog.topElement.builder.addChildren(
                formThemed.builder.addClasses('row', 'taackForm').setTaackTag(TaackTag.FORM).addChildren(
                        new HTMLInput(InputType.HIDDEN, id, 'id'),
                        new HTMLInput(InputType.HIDDEN, aObject.class.name, 'className'),
                        new HTMLInput(InputType.HIDDEN, parameter.applicationTagLib.controllerName, 'originController'),
                        new HTMLInput(InputType.HIDDEN, parameter.applicationTagLib.actionName, 'originAction'),
                        new HTMLInput(InputType.HIDDEN, parameter.brand, 'originBrand')
                ).build()
        )
         blockLog.topElement = formThemed
    }

    @Override
    void visitFormEnd() {
         blockLog.topElement = formThemed.formActionBlock( blockLog.topElement)
        formActions.each {
            formThemed.addFormAction( blockLog.topElement, it.cValue, it.aValue, it.bValue)
        }

         blockLog.topElement =  blockLog.topElement.toParentTaackTag(TaackTag.FORM)
        tabIds = 0
    }

    @Override
    void visitFormSection(String i18n) {
         blockLog.topElement = formThemed.section( blockLog.topElement, i18n)
    }

    @Override
    void visitFormSectionEnd() {
         blockLog.topElement =  blockLog.topElement.toParentTaackTag(TaackTag.SECTION)
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

        final boolean isEnum = field.fieldConstraint.field.type.isEnum()
        final boolean isListOrSet = Collection.isAssignableFrom(type)
        final boolean isDate = Date.isAssignableFrom(type)
        final boolean isNullable = field.fieldConstraint.nullable
        final boolean isFieldDisabled = isDisabled(field)
        StringBuffer result = new StringBuffer()

        if (isBoolean) {
             blockLog.topElement = formThemed.booleanInput( blockLog.topElement, qualifiedName, trI18n, isFieldDisabled, isNullable, field.value as boolean)
        } else if (eos) {
             blockLog.topElement = formThemed.selects( blockLog.topElement, qualifiedName, trI18n, eos, isListOrSet, isFieldDisabled, isNullable)
        } else if (isEnum || isListOrSet) {
            if (isEnum) {
                 blockLog.topElement = formThemed.selects( blockLog.topElement, qualifiedName, trI18n, new EnumOptions(field.fieldConstraint.field.type as Class<Enum>, qualifiedName, field.value as Enum), isListOrSet, isDisabled(field), field.fieldConstraint.nullable)
            } else if (isListOrSet) {
                if (field.fieldConstraint.field.genericType instanceof ParameterizedType) {
                    final ParameterizedType parameterizedType = field.fieldConstraint.field.genericType as ParameterizedType
                    final Type actualType = parameterizedType.actualTypeArguments.first()
                    final Class actualClass = Class.forName(actualType.typeName)
                    final boolean isEnumListOrSet = actualClass.isEnum()
                    if (isEnumListOrSet) {
                        Enum[] values = actualClass.invokeMethod(ST_VALUES, null) as Enum[]
                         blockLog.topElement = formThemed.selects( blockLog.topElement, qualifiedName, trI18n, new EnumOptions(field.fieldConstraint.field.type as Class<Enum>, qualifiedName, values), isListOrSet, isDisabled(field), field.fieldConstraint.nullable)
                    } else {
                        String[] values = actualClass.invokeMethod(ST_LIST, null)[ST_ID] as String[]
                         blockLog.topElement = formThemed.selects( blockLog.topElement, qualifiedName, trI18n, new EnumOptions(field.fieldConstraint.field.type as Class<Enum>, qualifiedName, values), isListOrSet, isDisabled(field), field.fieldConstraint.nullable)
                    }
                }
            }
        } else if (isDate) {
             blockLog.topElement = formThemed.dateInput( blockLog.topElement, qualifiedName, trI18n, isFieldDisabled, isNullable, field.value as Date)
        } else {
            if (field.fieldConstraint.widget == WidgetKind.TEXTAREA.name) {
                 blockLog.topElement = formThemed.textareaInput( blockLog.topElement, qualifiedName, trI18n, isFieldDisabled, isNullable, field.value as String)
            } else if (field.fieldConstraint.widget == WidgetKind.FILE_PATH.name) {
                 blockLog.topElement = formThemed.fileInput( blockLog.topElement, qualifiedName, trI18n, isFieldDisabled, isNullable, field.value as String)
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
                if (numberFormat && field.value instanceof Number) {
                    valueString = numberFormat.format(field.value)
                }
                 blockLog.topElement = formThemed.normalInput( blockLog.topElement, qualifiedName, trI18n, isFieldDisabled, isNullable, valueString)
            }
        }
    }

    @Override
    void visitFormAjaxField(String i18n, String controller, String action, FieldInfo field, Long id, Map<String, ?> params, FieldInfo[] fieldInfos) {
        final String trI18n = i18n ?: parameter.trField(field)

        final boolean isListOrSet = Collection.isAssignableFrom(field.fieldConstraint.field.type)
        final String qualifiedName = field.fieldName
        final String fieldInfoParams = fieldInfoParams(fieldInfos)
        final boolean isFieldDisabled = isDisabled(field)
        final boolean isNullable = field.fieldConstraint.nullable
        if (isListOrSet)
            formThemed.ajaxField( blockLog.topElement, trI18n, field.value as List, qualifiedName, parameter.modalId, parameter.urlMapped(controller, action, id, params), fieldInfoParams, isFieldDisabled, isNullable)
        else {
            GormEntity v = (field?.value) as GormEntity
            formThemed.ajaxField( blockLog.topElement, trI18n, v, qualifiedName, parameter.modalId, parameter.urlMapped(controller, action, id, params), fieldInfoParams, isFieldDisabled, isNullable)
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
        formThemed.ajaxField( blockLog.topElement, trI18n, enumOptions, field.value, qualifiedName, parameter.modalId, parameter.urlMapped(controller, action), fieldInfoParams, isFieldDisabled)
    }

    @Override
    void visitFormTabs(List<String> names, BlockSpec.Width width = BlockSpec.Width.QUARTER) {
         blockLog.topElement = BootstrapLayout.tabs( blockLog.topElement, random.nextInt(), names, width)
        tabIds++
    }

    @Override
    void visitFormTabsEnd() {
         blockLog.topElement =  blockLog.topElement.toParentTaackTag(TaackTag.TABS)
    }

    @Override
    void visitFormTab(String name) {
         blockLog.topElement.setTaackTag(TaackTag.TAB)
         blockLog.topElement = BootstrapLayout.tab( blockLog.topElement, ++tabOccurrence)
    }

    @Override
    void visitFormTabEnd() {
         blockLog.topElement =  blockLog.topElement.toParentTaackTag(TaackTag.TAB)
    }

    @Override
    void visitFormAction(String i18n, String controller, String action, Long id, Map params, ButtonStyle style) {
        i18n ?= parameter.trField(controller, action)
        formActions.add new Triple<String, ButtonStyle, String>(i18n, style, "/${controller}/${action}" as String)
    }

    @Override
    void visitInnerFormAction(String i18n, String controller, String action, Long id, Map params, ButtonStyle style) {
         blockLog.topElement = formThemed.formActionBlock  blockLog.topElement
        i18n ?= parameter.trField(controller, action)
        formThemed.addFormAction( blockLog.topElement, "/${controller}/${action}", i18n, style)
         blockLog.topElement =  blockLog.topElement.parent
    }

    @Override
    void visitFormFieldFromMap(final String i18n, final FieldInfo field, final String mapEntry) {
        final String trI18n = i18n ?: parameter.trField(field) ?: mapEntry
        final String qualifiedName = field.fieldName + '.' + mapEntry
        String value = (field.value as Map<String, String>)?.get(mapEntry)
        value = value ? inputEscape(value) : ''
        final boolean isFieldDisabled = isDisabled(field)
        final boolean isNullable = field.fieldConstraint.nullable
        if (field.fieldConstraint.widget == WidgetKind.TEXTAREA.name) {
            formThemed.textareaInput( blockLog.topElement, qualifiedName, trI18n, isFieldDisabled, isNullable, value)
        } else if (field.fieldConstraint.widget == WidgetKind.FILE_PATH.name) {
            formThemed.fileInput( blockLog.topElement, qualifiedName, trI18n, isFieldDisabled, isNullable, value)
        } else {
            if (field.fieldConstraint.widget == WidgetKind.PASSWD.name) {
                formThemed.passwdInput( blockLog.topElement, qualifiedName, trI18n, isFieldDisabled, isNullable, value)
            } else {
                formThemed.normalInput( blockLog.topElement, qualifiedName, trI18n, isFieldDisabled, isNullable, value)
            }
        }
    }

    @Override
    void visitCol() {
         blockLog.topElement.setTaackTag(TaackTag.COL)
         blockLog.topElement = BootstrapLayout.col( blockLog.topElement)
    }

    @Override
    void visitColEnd() {
         blockLog.topElement =  blockLog.topElement.toParentTaackTag(TaackTag.COL)
    }

    @Override
    void visitRow() {
         blockLog.topElement.setTaackTag(TaackTag.ROW)
         blockLog.topElement = BootstrapLayout.row( blockLog.topElement)
    }

    @Override
    void visitRowEnd() {
         blockLog.topElement =  blockLog.topElement.toParentTaackTag(TaackTag.ROW)
    }

    @Override
    void visitFormHiddenField(FieldInfo fieldInfo) {
        final String qualifiedName = fieldInfo.fieldName
        final boolean isListOrSet = Collection.isAssignableFrom(fieldInfo.fieldConstraint.field.type)

        String value
        if (fieldInfo.value?.hasProperty(ST_ID)) {
            value = "${fieldInfo.value.getAt(ST_ID)}"
            formThemed.addChildren(
                    new HTMLInput(InputType.HIDDEN, value, qualifiedName)
            )
        } else if (isListOrSet) {
            fieldInfo.value.eachWithIndex { Object it, Integer occ ->
                if (it instanceof GormEntity)
                    formThemed.addChildren(
                            new HTMLInput(InputType.HIDDEN, it.ident(), "${qualifiedName}[$occ].id")
                    )
            }
        } else {
            value = fieldInfo.value ?: ''
            formThemed.addChildren(
                    new HTMLInput(InputType.HIDDEN, value, qualifiedName)
            )
        }
    }
}
