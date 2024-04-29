package taack.ui.dump

import grails.web.api.WebAttributes
import grails.web.servlet.mvc.GrailsParameterMap
import groovy.transform.CompileStatic
import org.grails.plugins.web.taglib.ApplicationTagLib
import org.springframework.context.MessageSource
import taack.ast.type.FieldInfo
import taack.ast.type.GetMethodReturn

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.text.NumberFormat

@CompileStatic
final class Parameter implements WebAttributes {

    final static String P_ID = 'id'
    final static String P_SORT = 'sort'
    final static String P_ORDER = 'order'
    final static String P_OFFSET = 'offset'
    final static String P_MAX = 'max'
    final static String P_BRAND = 'brand'
    final static String P_ADDITIONAL_ID = 'additionalId'
    final static String P_ORIGINAL_CONT = 'originController'
    final static String P_ORIGINAL_ACTION = 'originAction'
    final static String P_FIELD_NAME = 'fieldName'

    final String sort
    final String order
    final Integer offset
    final Integer max
    final String brand
    final String originController
    final String originAction
    final Long additionalId
    final Long beanId
    final Boolean isAjaxRendering
    final String fieldName
    final Long modalId = System.currentTimeMillis()
    final NumberFormat nf
    final GrailsParameterMap map
    final MessageSource messageSource
    final Locale lcl
    final boolean testI18n
    final ApplicationTagLib applicationTagLib

    String aClassSimpleName

    Parameter(final Boolean isAjaxRendering = false, final Locale lcl = null, MessageSource messageSource = null) {
        this.map = params
        this.messageSource = messageSource
        this.sort = params.get(P_SORT) ?: null
        this.order = params.get(P_ORDER) ?: null
        this.max = params.int(P_MAX) ?: 20
        this.offset = params.int(P_OFFSET) ?: 0
        this.additionalId = params.long(P_ADDITIONAL_ID) ?: null
        this.beanId = params.long(P_ID) ?: null
        this.brand = params.get(P_BRAND) ?: null
        this.originController = params.get(P_ORIGINAL_CONT) ?: controllerName ?: null
        this.originAction = params.get(P_ORIGINAL_ACTION) ?: actionName ?: null
        this.fieldName = params.get(P_FIELD_NAME) ?: null
        this.isAjaxRendering = isAjaxRendering
        this.lcl = lcl
        this.testI18n = params.get('lang')?.toString()?.startsWith('test')
        this.nf = NumberFormat.getInstance(lcl)
        this.applicationTagLib = grailsApplication.mainContext.getBean(ApplicationTagLib)
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
        if (!testI18n) {
            String rv = tr(methodReturn.method.declaringClass.simpleName.uncapitalize() + '.' + methodReturn.method.name + '.label')
            if (rv) return rv
            rv = tr 'default' + '.' + methodReturn.method.name + '.label'
            if (rv) return rv
        }
        return "${methodReturn.method.declaringClass.simpleName.uncapitalize() + '.' + methodReturn.method.name + '.label'}, ${'default' + '.' + methodReturn.method.name + '.label'}"
    }

    String trField(final String controller, final String action) {
        String key = controller.uncapitalize() + '.' + action + '.label'
        if (!testI18n) {
            String rv = tr(key)
            if (rv) return rv
            rv = tr 'default' + '.' + action + '.label'
            if (rv) return rv
        }
        return key
    }

    String trField(final FieldInfo... fieldInfo) {
        def fieldNames = fieldInfo*.fieldName
        def fieldTypes = fieldInfo*.fieldConstraint.field*.type
        String cn = aClassSimpleName?.uncapitalize() ?: fieldInfo[0].fieldConstraint.field.declaringClass.simpleName.uncapitalize()
        final int s = fieldNames.size() - 1
        if (!testI18n) {
            for (int i = 0; i <= s; i++) {
                String rv = tr cn + '.' + fieldNames[i..s].join('.') + '.label'
                if (rv) return rv
                rv = tr 'default' + '.' + fieldNames[i..s].join('.') + '.label'
                if (rv) return rv
                cn = classNameUncap fieldTypes[i]
            }
        }
        StringBuffer rvl = new StringBuffer()
        for (int i = 0; i <= s; i++) {
            String rv = cn + '.' + fieldNames[i..s].join('.') + '.label'
            def found = tr(rv) == null
            rvl << (found ? '<b>' : '') + rv + (found ? '</b>' : '')
            rvl << ', '
            rv = 'default' + '.' + fieldNames[i..s].join('.') + '.label'
            found = tr(rv) == null
            rvl << (found ? '<b>' : '') + rv + (found ? '</b>' : '')
            rvl << ', '
            cn = classNameUncap fieldTypes[i]
        }
        return rvl.toString()
    }

    final String urlMapped(String controller, String action, Map<String, ? extends Object> params = null, boolean isAjax = false) {
        def p = params
        if (isAjax) {
            p = new HashMap<String, Object>()
            if (params) p.putAll(params)
            p.put('isAjax', true)
        }
        applicationTagLib.createLink(controller: controller, action: action, params: p)
    }

    final String urlMapped(String controller, String action, Long id, Map<String, ? extends Object> params = null, boolean isAjax = false) {
        def p = params
        if (isAjax) {
            p = new HashMap<String, Object>()
            if (params) p.putAll(params)
            p.put('isAjax', true)
        }
        applicationTagLib.createLink(controller: controller, action: action, params: p, id: id)
    }

}
