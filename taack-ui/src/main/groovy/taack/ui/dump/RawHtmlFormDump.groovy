package taack.ui.dump

import grails.util.Triple
import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity
import taack.ast.type.FieldInfo
import taack.ast.type.WidgetKind
import taack.ui.EnumOptions
import taack.ui.IEnumOptions
import taack.ui.dsl.block.BlockSpec.Width
import taack.ui.dsl.form.IUiFormVisitor
import taack.ui.dump.common.BlockLog
import taack.ui.dump.html.element.ButtonStyle
import taack.ui.dump.html.element.HTMLInput
import taack.ui.dump.html.element.InputType
import taack.ui.dump.html.element.TaackTag
import taack.ui.dump.html.form.BootstrapForm
import taack.ui.dump.html.form.IFormTheme
import taack.ui.dump.html.layout.BootstrapLayout

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

    private int tabOccurrence = 0
    private int tabIds = 0
    private FieldInfo[] lockedFields

    final IFormTheme formThemed
    final BlockLog blockLog
    final BootstrapLayout layout

    RawHtmlFormDump(final BlockLog blockLog, final Parameter parameter) {
        this.blockLog = blockLog
        this.parameter = parameter
        formThemed = new BootstrapForm(blockLog)
        layout = new BootstrapLayout(blockLog)

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
        String id = aObject.hasProperty(ST_ID) ? (aObject[ST_ID] != null ? aObject[ST_ID] : "") : ""
        blockLog.topElement.setTaackTag(TaackTag.FORM)
        blockLog.topElement.builder.addChildren(
                formThemed.builder.addClasses('row', 'taackForm').addChildren(
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
        blockLog.topElement = formThemed.formActionBlock(blockLog.topElement)
        formActions.each {
            formThemed.addFormAction(blockLog.topElement, it.cValue, it.aValue, it.bValue)
        }

        blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.FORM)
        tabIds = 0
    }

    @Override
    void visitFormSection(String i18n) {
        i18n ?= parameter.trField(parameter.controllerName, parameter.actionName, false)
        blockLog.topElement = formThemed.section(blockLog.topElement, i18n, false)
    }

    @Override
    void visitFormSectionEnd() {
        blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.SECTION).parent
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

        if (isBoolean) {
            blockLog.topElement = formThemed.booleanInput(blockLog.topElement, qualifiedName, trI18n, isFieldDisabled, false, field.value as boolean)
        } else if (eos) {
            blockLog.topElement = formThemed.selects(blockLog.topElement, qualifiedName, trI18n, eos, isListOrSet, isFieldDisabled, isNullable)
        } else if (isEnum || isListOrSet) {
            if (isEnum) {
                blockLog.topElement = formThemed.selects(blockLog.topElement, qualifiedName, trI18n, new EnumOptions(field.fieldConstraint.field.type as Class<Enum>, qualifiedName, field.value as Enum), isListOrSet, isDisabled(field), field.fieldConstraint.nullable)
            } else if (isListOrSet) {
                if (field.fieldConstraint.field.genericType instanceof ParameterizedType) {
                    final ParameterizedType parameterizedType = field.fieldConstraint.field.genericType as ParameterizedType
                    final Type actualType = parameterizedType.actualTypeArguments.first()
                    final Class actualClass = Class.forName(actualType.typeName)
                    final boolean isEnumListOrSet = actualClass.isEnum()
                    if (isEnumListOrSet) {
                        blockLog.topElement = formThemed.selects(blockLog.topElement, qualifiedName, trI18n, new EnumOptions(actualClass as Class<Enum>, qualifiedName, field.value as Enum[]), true, isDisabled(field), field.fieldConstraint.nullable)
                    } else {
                        String[] values = actualClass.invokeMethod(ST_LIST, null)[ST_ID] as String[]
                        blockLog.topElement = formThemed.selects(blockLog.topElement, qualifiedName, trI18n, new EnumOptions(field.fieldConstraint.field.type as Class<Enum>, qualifiedName, values), isListOrSet, isDisabled(field), field.fieldConstraint.nullable)
                    }
                }
            }
        } else if (isDate) {
            blockLog.topElement = formThemed.dateInput(blockLog.topElement, qualifiedName, trI18n, isFieldDisabled, isNullable, field.value as Date, field.fieldConstraint.widget == WidgetKind.DATETIME.name)
        } else {
            if (field.fieldConstraint.widget == WidgetKind.TEXTAREA.name) {
                blockLog.topElement = formThemed.textareaInput(blockLog.topElement, qualifiedName, trI18n, isFieldDisabled, isNullable, field.value as String)
            } else if (field.fieldConstraint.widget == WidgetKind.FILE_PATH.name) {
                blockLog.topElement = formThemed.fileInput(blockLog.topElement, qualifiedName, trI18n, isFieldDisabled, isNullable, field.value as String)
            } else if (field.fieldConstraint.widget == WidgetKind.MARKDOWN.name) {
                blockLog.topElement = formThemed.markdownInput(blockLog.topElement, qualifiedName, trI18n, isFieldDisabled, isNullable, field.value as String)
            } else if (field.fieldConstraint.widget == WidgetKind.ASCIIDOC.name) {
                blockLog.topElement = formThemed.asciidocInput(blockLog.topElement, qualifiedName, trI18n, isFieldDisabled, isNullable, field.value as String)
            } else {
                String valueString = inputEscape(field.value?.toString())
                if (numberFormat && field.value instanceof Number) {
                    valueString = numberFormat.format(field.value)
                }
                if (field.fieldConstraint.widget == WidgetKind.PASSWD.name)
                    blockLog.topElement = formThemed.passwdInput(blockLog.topElement, qualifiedName, trI18n, isFieldDisabled, isNullable, valueString)
                else
                    blockLog.topElement = formThemed.normalInput(blockLog.topElement, qualifiedName, trI18n, isFieldDisabled, isNullable, valueString)
            }
        }
    }

    @Override
    void visitFormAjaxField(String i18n, String controller, String action, FieldInfo field, Long id, Map<String, ?> params, FieldInfo[] fieldInfos) {
        final String trI18n = i18n ?: parameter.trField(field)

        final boolean isListOrSet = Collection.isAssignableFrom(field.fieldConstraint.field.type)
        final String qualifiedName = field.fieldName
        final boolean isFieldDisabled = isDisabled(field)
        final boolean isNullable = field.fieldConstraint.nullable
        if (isListOrSet)
            formThemed.ajaxField(blockLog.topElement, trI18n, field.value as List, qualifiedName, parameter.modalId, parameter.urlMapped(controller, action, id, params), fieldInfoCollect(fieldInfos), isFieldDisabled, isNullable)
        else
            formThemed.ajaxField(blockLog.topElement, trI18n, field.value, qualifiedName, parameter.modalId, parameter.urlMapped(controller, action, id, params), fieldInfoCollect(fieldInfos), isFieldDisabled, isNullable)

    }

    private static List<String> fieldInfoCollect(FieldInfo[] fieldInfos) {
        fieldInfos ?= new FieldInfo[0]
        fieldInfos.collect { GormEntity.isAssignableFrom(it.fieldConstraint.field.type) ? "${it.fieldName}.id" : it.fieldName } as List<String>
    }

    @Override
    void visitFormAjaxField(String i18n, String controller, String action, FieldInfo field, IEnumOptions enumOptions, FieldInfo[] fieldInfos) {
        final String trI18n = i18n ?: parameter.trField(field)
        final String qualifiedName = field.fieldName
        final boolean isFieldDisabled = isDisabled(field)
        formThemed.ajaxField(blockLog.topElement, trI18n, enumOptions, qualifiedName, parameter.modalId, parameter.urlMapped(controller, action), fieldInfoCollect(fieldInfos), isFieldDisabled)
    }

    @Override
    void visitFormTabs(List<String> names, Width width = Width.QUARTER) {
        blockLog.topElement.setTaackTag(TaackTag.TABS)
        blockLog.topElement = layout.tabs(blockLog.topElement, names)
        tabIds++
    }

    @Override
    void visitFormTabsEnd() {
        blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.TABS)
    }

    @Override
    void visitFormTab(String name) {
        blockLog.topElement.setTaackTag(TaackTag.TAB)
        blockLog.topElement = layout.tab(blockLog.topElement, tabOccurrence++)
    }

    @Override
    void visitFormTabEnd() {
        blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.TAB)
    }

    @Override
    void visitFormAction(String i18n, String url, ButtonStyle style) {
        formActions.add new Triple<String, ButtonStyle, String>(i18n, style, url)
    }

    @Override
    void visitFormAction(String i18n, String controller, String action, Long id, Map params, ButtonStyle style) {
        i18n ?= parameter.trField(controller, action, id != null || params?.containsKey('id'))
        formActions.add new Triple<String, ButtonStyle, String>(i18n, style, parameter.urlMapped(controller, action, id, params))
    }

    @Override
    void visitInnerFormAction(String i18n, String controller, String action, Long id, Map params, ButtonStyle style) {
        blockLog.topElement = formThemed.formActionBlock blockLog.topElement
        i18n ?= parameter.trField(controller, action, id != null || params?.containsKey('id'))
        formThemed.addFormAction(blockLog.topElement, parameter.urlMapped(controller, action, id, params), i18n, style)
        blockLog.topElement = blockLog.topElement.parent
    }

    @Override
    void visitFormFieldFromMap(final String i18n, final FieldInfo field, final String mapEntry) {
        final String trI18n = i18n ?: parameter.trField(field) ?: mapEntry
        final String qualifiedName = field.fieldName + '.' + mapEntry
        String value = (field.value as Map<String, String>)?.get(mapEntry)
        final boolean isFieldDisabled = isDisabled(field)
        final boolean isNullable = field.fieldConstraint.nullable
        if (field.fieldConstraint.widget == WidgetKind.TEXTAREA.name) {
            formThemed.textareaInput(blockLog.topElement, qualifiedName, trI18n, isFieldDisabled, isNullable, value)
        } else {
            if (field.fieldConstraint.widget == WidgetKind.FILE_PATH.name) {
                formThemed.fileInput(blockLog.topElement, qualifiedName, trI18n, isFieldDisabled, isNullable, value)
            } else if (field.fieldConstraint.widget == WidgetKind.ASCIIDOC.name) {
                formThemed.asciidocInput(blockLog.topElement, qualifiedName, trI18n, isFieldDisabled, isNullable, value)
            } else {
                value = value ? inputEscape(value) : ''
                if (field.fieldConstraint.widget == WidgetKind.PASSWD.name) {
                    formThemed.passwdInput(blockLog.topElement, qualifiedName, trI18n, isFieldDisabled, isNullable, value)
                } else {
                    formThemed.normalInput(blockLog.topElement, qualifiedName, trI18n, isFieldDisabled, isNullable, value)
                }
            }
        }
    }

    @Override
    void visitCol(Width width) {
        blockLog.topElement.setTaackTag(TaackTag.COL)
        blockLog.topElement = layout.col(blockLog.topElement, width)
    }

    @Override
    void visitColEnd() {
        blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.COL)
    }

    @Override
    void visitRow() {
        blockLog.topElement.setTaackTag(TaackTag.ROW)
        blockLog.topElement = layout.row(blockLog.topElement)
    }

    @Override
    void visitRowEnd() {
        blockLog.topElement = blockLog.topElement.toParentTaackTag(TaackTag.ROW)
    }

    @Override
    void visitFormHiddenField(FieldInfo fieldInfo) {
        final String qualifiedName = fieldInfo.fieldName
        final boolean isListOrSet = Collection.isAssignableFrom(fieldInfo.fieldConstraint.field.type)

        String value
        if (fieldInfo.value?.hasProperty(ST_ID)) {
            value = "${fieldInfo.value.getAt(ST_ID)}"
            formThemed.addChildren(
                    new HTMLInput(InputType.HIDDEN, value, qualifiedName + '.id')
            )
        } else if (isListOrSet) {
            fieldInfo.value.eachWithIndex { Object it, Integer occ ->
                if (it instanceof GormEntity)
                    formThemed.addChildren(
                            new HTMLInput(InputType.HIDDEN, it.ident(), "${qualifiedName}[$occ].id")
                    )
            }
        } else {
            formThemed.addChildren(
                    new HTMLInput(InputType.HIDDEN, fieldInfo.value, qualifiedName)
            )
        }
    }
}
