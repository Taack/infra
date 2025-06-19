package taack.ui.dsl.helper

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.ast.type.FieldInfo

import java.nio.charset.StandardCharsets

@CompileStatic
final class Utils {
    static final private String[] fieldsToExclude = [
            'belongsTo',
            'constraints',
            'instanceControllersDomainBindingApi',
            'transients',
            'log',
            'instanceConvertersApi',
            'metaClass'
    ]

    static String transformInCamelCase(String name) {
        if (name && name.length() > 0) {
            return name[0].toLowerCase() + name.substring(1)
        }
        null
    }

    static String getControllerName(final MethodClosure methodClosure) {
        if (!methodClosure) return null
        if (methodClosure.owner instanceof Class) {
            Class o = methodClosure.owner
            transformInCamelCase(o.simpleName)[0..-11]
        } else {
            transformInCamelCase(methodClosure.owner.class.simpleName)[0..-11]
        }
    }

    static Map<String, String> getAdditionalFields(final Object currentObject, final FieldInfo field = null, final List<String> toExcludes = []) {
        Map<String, String> additionalFields = [:]
        if (!currentObject['id']) {
            currentObject.class.declaredFields.each {
                if (!(it.name as String).contains('_') && !(it.name as String).contains('$') && !fieldsToExclude.contains(it.name) && (it.name != field?.fieldName) && !toExcludes.contains(it.name)) {
                    if (currentObject[it.name]?.hasProperty('id'))
                        additionalFields.put(it.name, currentObject[it.name]['id']?.toString())
                    else
                        additionalFields.put(it.name, currentObject[it.name]?.toString())
                }
            }
        } else {
            additionalFields.put('id', currentObject['id'].toString())
        }
        additionalFields
    }

    static String getAdditionalInputs(Map<String, ? extends Object> p, final String formId = null) {
        if (p) p.collect { """<input ${formId ? "form='${formId}'" : ''} name="${it.key}" type="hidden" value="${it.value ?: ""}" autocomplete="off">""" }.join('\n')
        else ""
    }

    static String paramsString(final Map<String, ?> params) {
        params?.collect {
            if (it.value) {
                if (Collection.isAssignableFrom(it.value.class) || Object[].isAssignableFrom(it.value.class)) {
                    List<String> tmp = []
                    it.value.each { Object v ->
                        tmp.add "${it.key}=${URLEncoder.encode(v.toString(), StandardCharsets.UTF_8.toString())}" as String
                    }
                    tmp.join('&')
                } else {
                    "${it.key}=${URLEncoder.encode(it.value.toString(), StandardCharsets.UTF_8.toString())}"
                }
            } else {
                "${it.key}="
            }
        }?.join('&')?:''
    }

    static String paramsJson(final Map<String, ? extends Object> params) {
        String inner = params?.collect {
            if (it.value) {
                if (Collection.isAssignableFrom(it.value.class) || Object[].isAssignableFrom(it.value.class)) {
                    "\"${it.key}\": \"[${it.value.collect { String s -> URLEncoder.encode(s, StandardCharsets.UTF_8.toString()) }.join(',')}]\""
                } else {
                    "\"${it.key}\": \"${URLEncoder.encode(it.value.toString(), StandardCharsets.UTF_8.toString())}\""
                }
            } else {
                "\"${it.key}\":"
            }
        }?.join(',') ?: ''

        return '{' + inner + '}'
    }
}
