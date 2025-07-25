package taack.ui.dump

import grails.util.Pair
import grails.web.api.WebAttributes
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import org.grails.plugins.web.taglib.ApplicationTagLib
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import taack.ast.type.FieldInfo
import taack.ast.type.GetMethodReturn
import taack.render.ThemeService
import taack.ui.dsl.helper.Utils

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.text.NumberFormat

@CompileStatic
final class Parameter implements WebAttributes {

    enum RenderingTarget {
        WEB, MAIL, PDF
    }

    final static String P_ID = 'id'
    final static String P_SORT = 'sort'
    final static String P_ORDER = 'order'
    final static String P_OFFSET = 'offset'
    final static String P_MAX = 'max'
    final static String P_BRAND = 'brand'
    final static String P_ADDITIONAL_ID = 'additionalId'
    final static String P_FIELD_NAME = 'fieldName'

    final RenderingTarget target
    final String sort
    final String order
    final String lastReadingDate
    final String readingDateFieldString
    final Integer offset
    final Integer max
    final String brand
    final Long additionalId
    final Long beanId
    final Boolean isAjaxRendering
    // this is a refresh of ajaxBlocks (i.e. do not reopen a modal, refresh the content)
    final Boolean isRefresh
    final String ajaxBlockId
    final String targetAjaxBlockId
    final String fieldName
    final Map<String, String> paramsToKeep
    final Long modalId = System.currentTimeMillis()
    final NumberFormat nf
    final MessageSource messageSource
    final Locale lcl
    final boolean testI18n
    final Integer tabIndex
    final Integer tabId
    static ApplicationTagLib applicationTagLib = null
    static ThemeService uiThemeService = null
    boolean isModal = false

    Parameter() {
        this(RenderingTarget.WEB)
    }

    Parameter(final Locale lcl = null, MessageSource messageSource = null, RenderingTarget target, String... paramsToKeep) {
        this.messageSource = messageSource
        this.target = target
        this.sort = params.get(P_SORT) ?: null
        this.order = params.get(P_ORDER) ?: null
        this.lastReadingDate = params.get('lastReadingDate') ?: null
        this.readingDateFieldString = params.get('readingDateFieldString') ?: null
        this.max = params.int(P_MAX) ?: 20
        this.offset = params.int(P_OFFSET) ?: 0
        this.additionalId = params.long(P_ADDITIONAL_ID) ?: null
        this.beanId = params.long(P_ID) ?: null
        this.brand = params.get(P_BRAND) ?: null
        this.fieldName = params.get(P_FIELD_NAME) ?: null
        this.paramsToKeep = [:]
        paramsToKeep.each {
            this.paramsToKeep.put(it, params[it] as String)
        }
        this.ajaxBlockId = params.get('ajaxBlockId') ?: null
        this.targetAjaxBlockId = params.get('targetAjaxBlockId') ?: null
        this.isAjaxRendering = params.boolean('isAjax') == true
        this.isRefresh = params.boolean('refresh') == true
        this.lcl = lcl ?: LocaleContextHolder.locale
        this.testI18n = params.get('lang')?.toString()?.startsWith('test')
        this.nf = lcl ? NumberFormat.getInstance(lcl) : null
        this.tabIndex = params.get('tabIndex') ? params.int('tabIndex') : null
        this.tabId = params.get('tabId') ? params.int('tabId') : null
        if (!applicationTagLib) applicationTagLib = grailsApplication.mainContext.getBean(ApplicationTagLib)
        if (!uiThemeService) uiThemeService = grailsApplication.mainContext.getBean(ThemeService)
    }

    String tr(final String code, final Locale locale = null, final Object[] args = null) {
        if (messageSource == null) return null
        try {
            messageSource.getMessage(code, args, locale ?: lcl)
        } catch (ignore) {
            try {
                messageSource.getMessage(code, args, new Locale('en'))
            } catch (ignored) {
                null
            }
        }
    }

    private static String classNameUncap(Type aType) {
        if (aType instanceof ParameterizedType) {
            return (aType.actualTypeArguments[0] as Class).simpleName.uncapitalize()
        } else if (aType instanceof Class) {
            return aType.simpleName.uncapitalize()
        }
        null
    }

    String trField(final GetMethodReturn methodReturn) {
        if (!methodReturn) return ''
        if (!testI18n) {
            String rv = tr(methodReturn.method.declaringClass.simpleName.uncapitalize() + '.' + methodReturn.method.name + '.label')
            if (rv) return rv
            rv = tr 'default' + '.' + methodReturn.method.name + '.label'
            if (rv) return rv
        }
        return "${methodReturn.method.declaringClass.simpleName.uncapitalize() + '.' + methodReturn.method.name + '.label'}, ${'default' + '.' + methodReturn.method.name + '.label'}"
    }

    static Pair<String, String> actionTrParams(String action) {
        char ch
        for (int i = 0; i < action.length(); i++) {
            ch = action.charAt(i)
            if (Character.isUpperCase(ch)) {
                return new Pair<>(action.substring(0, i), action.substring(i).uncapitalize())
            }
        }
        return new Pair<>(action, null)

    }

    String trField(final String controller, final String action, final boolean hasId) {
        String key = controller.uncapitalize() + '.' + action + '.label'

        if (!testI18n) {
            String rv = tr(key)
            if (rv && rv != key) return rv
            rv = tr 'default.' + action + '.label'
            if (rv) return rv
            Pair p = actionTrParams(action)
            String trClass = tr('default.' + p.bValue?.uncapitalize() + '.label')
            if (p.aValue == 'edit' && !hasId) {
                rv = tr('default' + '.' + 'create' + '.label', null, trClass ?: p.bValue)
            } else {
                rv = tr('default' + '.' + p.aValue + '.label', null, trClass ?: p.bValue)
            }
            if (rv) return rv
        }
        return key
    }

    String trField(final FieldInfo... fieldInfo) {
        if (!fieldInfo || fieldInfo.grep().isEmpty()) return ''
        def fieldNames = fieldInfo*.fieldName
        def fieldTypes = fieldInfo*.fieldConstraint.field*.type
        String cn = fieldInfo[0].fieldConstraint.field.declaringClass.simpleName.uncapitalize()
        final int s = fieldNames.size() - 1

        List<String> keys = []
        for (int i = 0; i <= s; i++) {
            String keySuffix = fieldNames[i..s].join('.') + '.label'
            String rv = tr cn + '.' + keySuffix
            if (rv) {
                if (!testI18n)
                    return rv
                else
                    keys.add("<b>${cn + '.' + keySuffix}</b>" as String)
            } else keys.add("${cn + '.' + keySuffix}" as String)
            String rvd = tr 'default' + '.' + keySuffix
            if (rvd) {
                if (!testI18n)
                    return rvd
                else
                    keys.add("<b>${'default' + '.' + keySuffix}</b>" as String)
            } else keys.add("${'default' + '.' + keySuffix}" as String)
            cn = classNameUncap fieldTypes[i]
        }

        return keys.join(',')
    }

    final String urlMapped(MethodClosure action, Map<String, ? extends Object> params = null, boolean isAjax = false) {
        urlMapped(Utils.getControllerName(action), action.method, params, isAjax)
    }

    final String urlMapped(String controller, String action, Map<String, ? extends Object> params = null, boolean isAjax = false) {
        urlMapped(controller, action, null, params, isAjax)
    }

    final String urlMapped(String controller, String action, Long id, Map<String, ? extends Object> params = null, boolean isAjax = false) {
        def p = params ?: [:]
        if (isAjax) {
            p.put('isAjax', true)
        }
        paramsToKeep.each {
            p.putIfAbsent(it.key, it.value)
        }
        applicationTagLib.createLink(controller: controller, action: action, params: p, absolute: target == RenderingTarget.MAIL, id: id)
    }

    final String urlMapped() {
        urlMapped(controllerName, actionName)
    }
}
