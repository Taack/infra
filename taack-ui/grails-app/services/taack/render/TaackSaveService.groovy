package taack.render

import grails.artefact.controller.support.ResponseRenderer
import grails.compiler.GrailsCompileStatic
import grails.plugin.springsecurity.SpringSecurityService
import grails.validation.Validateable
import grails.web.api.ServletAttributes
import grails.web.databinding.DataBinder
import grails.web.servlet.mvc.GrailsParameterMap
import org.codehaus.groovy.runtime.MethodClosure as MC
import org.grails.datastore.gorm.GormEntity
import org.grails.datastore.mapping.dirty.checking.DirtyCheckable
import org.grails.orm.hibernate.cfg.GrailsHibernateUtil
import org.grails.plugins.web.taglib.ApplicationTagLib
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.validation.Errors
import taack.ast.type.FieldInfo
import taack.domain.IDomainHistory
import taack.ui.dsl.UiBlockSpecifier
import taack.ui.dsl.helper.Utils
import taack.user.IUserCreated
import taack.user.IUserUpdated
import taack.user.TaackUser
import taack.utils.DateFormat

import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType

@GrailsCompileStatic
class TaackSaveService implements ResponseRenderer, ServletAttributes, DataBinder {
    TaackUiService taackUiService
    SpringSecurityService springSecurityService
    SessionFactory sessionFactory

    private final static Map<String, Closure> fieldCustomSavingClosures = [:]

    /**
     * Allow to register a custom save for specific field when saving a gormEntity.
     *
     * Example:
     *      TaackSaveService.registerFieldCustomSavingClosure('projectName', { GormEntity gormEntity, Map params ->
     *          if (gormEntity instaceof Project) {
     *              gormEntity.projectName = 'CUSTOM_' + gormEntity.projectName
     *              // gormEntity.projectName = 'CUSTOM_' + params['projectName']
     *          }
     *      }
     *
     * @param fieldName : Name of target field
     * @param closure : Custom saving process
     */
    static final void registerFieldCustomSavingClosure(String fieldName, Closure closure) {
        fieldCustomSavingClosures.put(fieldName, closure)
    }

//    static <D extends GormEntity> boolean beanIsOwnerLocking(D entity) {
//        boolean ret = false
//        return ret
//    }

    final String urlMapped(String controller, String action, Map<String, ? extends Object> params = null, boolean isAjax = false) {
        def p = params
        if (isAjax && params && !params.containsKey('isAjax')) {
            p = new HashMap<String, Object>()
            p.putAll(params)
//            p.remove('recordState')
            p.put('isAjax', true)
        }
        grailsApplication.mainContext.getBean(ApplicationTagLib).createLink(controller: controller, action: action, params: p)
    }

    final String urlMapped(String controller, String action, Validateable validateable, boolean isAjax = false) {
        def p = params
        if (isAjax && params && !params.containsKey('isAjax')) {
            p = new HashMap<String, Object>()
            validateable.class.getDeclaredFields().each {
                params.containsKey(it.name)
                p.put(it.name, params[it.name])
            }
//            p.remove('recordState')
            p.put('isAjax', true)
        }
        grailsApplication.mainContext.getBean(ApplicationTagLib).createLink(controller: controller, action: action, params: p)
    }

    static Class beanRealClass(Object entity) {
        beanReal(entity).class
    }

    static Object beanReal(Object entity) {
        if (entity instanceof GeneratedGroovyProxy) {
            return entity.proxyTarget
        } else {
            GrailsHibernateUtil.unwrapIfProxy(entity)
        }
    }

//    static <D extends GormEntity> boolean beanIsLocked(D entity) {
//        Class beanClass = beanRealClass(entity)
////        if (IUserInterface.isAssignableFrom(beanClass) && beanClass.getAt('enumTransition')) {
////            IEnumTransition enumTransition = entity.getAt(beanClass.getAt('enumTransition') as String) as IEnumTransition
////            if (enumTransition.lockedFields?.contains('*') && enumTransition.lockedFields?.size() == 1) {
////                return true
////            }
////        }
//        return beanIsOwnerLocking(entity)
//    }

    private static <D extends GormEntity> D getGorm(Long id, Class<D> classD, Session session) {
//        GormEntity gormEntity = ((GormStaticApi<D>) GormEnhancer.findEntity(classD)).get(id)
        D gormEntity = id ? session.find(classD, id) : classD.getDeclaredConstructor().newInstance()
//        if (!gormEntity) gormEntity = classD.getDeclaredConstructor().newInstance()
        return gormEntity
    }

    final <T extends GormEntity> T prepareSave(final Class<T> aClass, final FieldInfo[] lockedFields = null) {
        save(aClass, lockedFields, true)
    }

    final <T extends GormEntity> T save(final Class<T> aClass, final FieldInfo[] lockedFields = null, final boolean doNotSave = false) {
        final Long id = params.long('id')
        T gormEntity = getGorm(id, aClass, sessionFactory.currentSession)
        save(gormEntity, lockedFields, doNotSave)
    }

    final void bindObject(Object gormEntity, GrailsParameterMap p, final FieldInfo[] lockedFields = null) {
        boolean isDirtyCheckable = false
        if (DirtyCheckable.isAssignableFrom(gormEntity.class)) {
            isDirtyCheckable = true
        }

        Map<String, Long> belongsToKey = [:]
        if (gormEntity.class.hasProperty('belongsTo')) {
            Map belongsTo = gormEntity['belongsTo'] as Map
            if (belongsTo) {
                List<String> belongsToProps = belongsTo.keySet().collect { it.toString() }
                for (String key in belongsToProps) {
                    if (params[key] instanceof Map) {
                        Map km = params[key]
                        if (km.size() == 1 && km.containsKey('id'))
                            belongsToKey.put key, km['id'].toString().toLong()
                        else throw new SecurityException('Attempt to change pointed data')
                    }
                }
            }
        }

        List<Field> classFields = (gormEntity.class.declaredFields + gormEntity.class.superclass.declaredFields).findAll { Field f ->
            !['id', 'filePath', 'version', 'userCreated', 'userUpdated', 'userLastUpdated', 'dateCreated', 'lastUpdated'].contains(f.name) &&
                    !(f.name.contains('_') || f.name.contains('$')) &&
                    !(Modifier.isFinal(f.modifiers) || Modifier.isStatic(f.modifiers) || Modifier.isTransient(f.modifiers)) &&
                    (f.name in p.keySet() || f.name in belongsToKey.keySet())
        }

        println "classField $classFields"

        for (Field includedField in classFields) {
            if (lockedFields && lockedFields[0] != null && lockedFields*.fieldConstraint*.field.contains(includedField)) {
                // Modify only fields that are not in lockedField
                continue
            } else if (lockedFields && lockedFields[0] == null && !lockedFields*.fieldConstraint*.field.contains(includedField)) {
                // Modify only fields that are after null. [null, f1, f2 ...]
                continue
            }

            if (includedField.type.isEnum() && p[includedField.name]) {
                String enumString = p[includedField.name]
                Enum enumInstance = Enum.valueOf(includedField.type as Class<Enum>, enumString)
                if ((gormEntity[includedField.name] as Enum) != enumInstance) { // TODO: need to mark dirty ...
                    Field enumField = gormEntity.class.getDeclaredField(includedField.name)
                    enumField.setAccessible(true)
                    enumField.set gormEntity, enumInstance

                    if (isDirtyCheckable) (gormEntity as DirtyCheckable).markDirty(includedField.name)
                }
            } else if (Map.isAssignableFrom(includedField.type)) {
                Map entries = [:]
                Set<String> mapKeys = p.keySet().findAll { it ->
                    it.toString().startsWith(includedField.name + '.')
                }
                for (mapKey in mapKeys) {
                    if (mapKey.count('.') == 1) {
                        entries.put(mapKey - (includedField.name + '.'), p.get(mapKey))
                    } else throw new SecurityException('Map Key with more than one point.')
                }
                if ((gormEntity[includedField.name] as Map) != entries) {
                    gormEntity[includedField.name] = entries
                    if (isDirtyCheckable) (gormEntity as DirtyCheckable).markDirty(includedField.name)
                }
            } else if (Collection.isAssignableFrom(includedField.type)) { // TODO: What if reodering a list
                List<Long> ids = p.list(includedField.name).grep { !it.toString().empty }.collect { it.toString().toLong() }
                if ((gormEntity[includedField.name] as Collection<GormEntity>)*.ident() as Set<Long> != ids as Set<Long>) {
                    Collection<GormEntity> gormEntityField = gormEntity[includedField.name] as Collection<GormEntity>
                    gormEntityField.clear()
                    // The other way is to add only new elements / remove only some elements ...
                    gormEntityField.addAll(p.list(includedField.name).collect {
                        getGorm(it as Long, (includedField.genericType as ParameterizedType).getActualTypeArguments()[0] as Class<GormEntity>, sessionFactory.currentSession)
                    } as Collection<GormEntity>)
                    if (isDirtyCheckable) (gormEntity as DirtyCheckable).markDirty(includedField.name)
                }
            } else if (GormEntity.isAssignableFrom(includedField.type)) {
                if (!p[includedField.name]?.toString()?.empty) {
                    Long entryId = belongsToKey.containsKey(includedField.name) ? belongsToKey[includedField.name] : p[includedField.name]?.toString()?.toLong()
                    if ((gormEntity[includedField.name + 'Id'] as Long) != entryId) {
                        GormEntity entryEntity = getGorm(entryId, includedField.type, sessionFactory.currentSession)
                        gormEntity[includedField.name] = entryEntity
                        if (isDirtyCheckable) (gormEntity as DirtyCheckable).markDirty(includedField.name)
                    }
                } else {
                    if (gormEntity[includedField.name] != null) {
                        gormEntity[includedField.name] = null
                        if (isDirtyCheckable) (gormEntity as DirtyCheckable).markDirty(includedField.name)
                    }
                }
            } else {
                if (p[includedField.name] != null) {
                    Field valueField = gormEntity.class.getDeclaredField(includedField.name)
                    Object value = p[includedField.name]
                    String valueString = value?.toString()
                    boolean notEmpty = !value.toString().empty
                    valueField.setAccessible(true)
                    if (notEmpty) {
                        if (Boolean.isAssignableFrom(valueField.type) || boolean.isAssignableFrom(valueField.type)) {
                            Boolean valueStringBoolean = valueString.toBoolean()
                            if (gormEntity[includedField.name] != valueStringBoolean) {
                                valueField.set gormEntity, valueStringBoolean
                                if (isDirtyCheckable) (gormEntity as DirtyCheckable).markDirty(includedField.name)
                            }
                        } else if (Integer.isAssignableFrom(valueField.type) || int.isAssignableFrom(valueField.type)) {
                            Integer valueStringInteger = valueString.toInteger()
                            if (gormEntity[includedField.name] != valueStringInteger) {
                                valueField.set gormEntity, valueStringInteger
                                if (isDirtyCheckable) (gormEntity as DirtyCheckable).markDirty(includedField.name)
                            }
                        } else if (BigDecimal.isAssignableFrom(valueField.type)) {
                            BigDecimal valueStringBigDecimal = valueString.toBigDecimal()
                            if (gormEntity[includedField.name] != valueStringBigDecimal) {
                                valueField.set gormEntity, valueStringBigDecimal
                                if (isDirtyCheckable) (gormEntity as DirtyCheckable).markDirty(includedField.name)
                            }
                        } else if (Double.isAssignableFrom(valueField.type) || double.isAssignableFrom(valueField.type)) {
                            Double valueStringDouble = valueString.toDouble()
                            if (gormEntity[includedField.name] != valueStringDouble) {
                                valueField.set gormEntity, valueStringDouble
                                if (isDirtyCheckable) (gormEntity as DirtyCheckable).markDirty(includedField.name)
                            }
                        } else if (Float.isAssignableFrom(valueField.type) || float.isAssignableFrom(valueField.type)) {
                            Float valueStringFloat = valueString.toFloat()
                            if (gormEntity[includedField.name] != valueStringFloat) {
                                valueField.set gormEntity, valueStringFloat
                                if (isDirtyCheckable) (gormEntity as DirtyCheckable).markDirty(includedField.name)
                            }
                        } else if (Long.isAssignableFrom(valueField.type) || long.isAssignableFrom(valueField.type)) {
                            Long valueStringLong = valueString.toLong()
                            if (gormEntity[includedField.name] != valueStringLong) {
                                valueField.set gormEntity, valueStringLong
                                if (isDirtyCheckable) (gormEntity as DirtyCheckable).markDirty(includedField.name)
                            }
                        } else if (Integer.isAssignableFrom(valueField.type) || int.isAssignableFrom(valueField.type)) {
                            Integer valueStringInteger = valueString.toInteger()
                            if (gormEntity[includedField.name] != valueStringInteger) {
                                valueField.set gormEntity, valueStringInteger
                                if (isDirtyCheckable) (gormEntity as DirtyCheckable).markDirty(includedField.name)
                            }
                        } else if (Short.isAssignableFrom(valueField.type) || short.isAssignableFrom(valueField.type)) {
                            Short valueStringShort = valueString.toShort()
                            if (gormEntity[includedField.name] != valueStringShort) {
                                valueField.set gormEntity, valueStringShort
                                if (isDirtyCheckable) (gormEntity as DirtyCheckable).markDirty(includedField.name)
                            }
                        } else if (Date.isAssignableFrom(valueField.type)) {
                            Date valueStringDate = DateFormat.parse("yyyy-MM-dd", valueString)
                            if (gormEntity[includedField.name] != valueStringDate) {
                                valueField.set gormEntity, valueStringDate
                                if (isDirtyCheckable) (gormEntity as DirtyCheckable).markDirty(includedField.name)
                            }
                        } else {
                            if (gormEntity[includedField.name] != value) {
                                valueField.set gormEntity, value
                                if (isDirtyCheckable) (gormEntity as DirtyCheckable).markDirty(includedField.name)
                            }
                        }
                    } else {
                        if (gormEntity[includedField.name] != value) {
                            valueField.set gormEntity, null // ''
                            if (isDirtyCheckable) (gormEntity as DirtyCheckable).markDirty(includedField.name)
                        }
                    }
                } else {
                    if (gormEntity[includedField.name] != null) {
                        gormEntity[includedField.name] = null
                        if (isDirtyCheckable) (gormEntity as DirtyCheckable).markDirty(includedField.name)
                    }
                }
            }
        }
        gormEntity.class.declaredFields*.name.intersect(fieldCustomSavingClosures.keySet()).each {
            fieldName -> fieldCustomSavingClosures.get(fieldName).call(gormEntity, params)
        }
    }

    final <T extends GormEntity> T save(final T gormEntity, final FieldInfo[] lockedFields = null, final boolean doNotSave = false, boolean doNotBindParams = false) {
        final id = gormEntity.ident()
        if (lockedFields != null && lockedFields.size() == 0) {
            log.error "Not supposed to call save on $gormEntity"
            throw new SecurityException("Update not allowed")
        }
        T oldEntity
        if (gormEntity instanceof IDomainHistory && id != null) {
            oldEntity = (gormEntity as IDomainHistory<T>).cloneDirectObjectData()
        }
        if (!doNotBindParams) {
            bindObject(gormEntity, params, lockedFields)
        }

        try {
            TaackUser currentUser = springSecurityService.currentUser as TaackUser
            if (gormEntity.hasChanged()) {
                if (gormEntity instanceof IUserCreated && gormEntity.objectGetUserCreated() == null) {
                    gormEntity.objectSetUserCreated(currentUser)
                } else if (gormEntity.hasProperty('userCreated') && gormEntity['userCreated'] == null) {
                    gormEntity['userCreated'] = currentUser
                }
                if (gormEntity instanceof IUserUpdated) {
                    gormEntity.objectSetUserUpdated(currentUser)
                } else if (gormEntity.hasProperty('userUpdated')) {
                    gormEntity['userUpdated'] = currentUser
                } else if (gormEntity.hasProperty('userLastUpdated')) {
                    gormEntity['userLastUpdated'] = currentUser
                }
            }
        } catch (ignored) {
            log.warn("Ignored Exception: " + ignored.message)
        }

        if (!doNotSave) {
            if (oldEntity && gormEntity.validate() && (doNotBindParams || gormEntity.isDirty())) {
                save(oldEntity, lockedFields, doNotSave, true)
            }

            gormEntity.save(flush: true)
        }

        if (!doNotSave && gormEntity.hasErrors()) {
            log.error "${gormEntity.errors}"
        }

        return gormEntity
    }

    def renderErrors(Errors errors) {
        Map<String, List<String>> fieldErrors = [:]
        errors.fieldErrors.each {
            if (fieldErrors.get(it.field)) {
                fieldErrors.get(it.field).add(grailsAttributes.messageSource.getMessage(it, LocaleContextHolder.locale))
            } else {
                fieldErrors.put(it.field, [grailsAttributes.messageSource.getMessage(it, LocaleContextHolder.locale)])
            }
        }
        return fieldErrors.collect {
            render """__ErrorKeyStart__${it.key}:<ul class="errorKey">${it.value.collect { """<li class="errorEntry">$it</li>""" }.join('')}</ul>__ErrorKeyEnd__"""
        }.join('')
    }

    def reloadOrRenderErrors(Validateable validateable) {
        if (validateable.hasErrors()) {
            return renderErrors(validateable.errors)
        } else {
            render """__reload__"""
        }
    }

    def redirectOrRenderErrors(Validateable validateable, final MC redirectAction = null) {
        if (validateable.hasErrors()) {
            return renderErrors(validateable.errors)
        } else {
            if (redirectAction) {
                render """__redirect__${urlMapped(Utils.getControllerName(redirectAction), redirectAction.method, validateable)}"""
            } else render """__reload__"""
        }
    }

    def redirectOrRenderErrors(final GormEntity gormEntity, final MC redirectAction = null) {
        if (gormEntity.hasErrors()) {
            return renderErrors(gormEntity.errors)
        } else {
            if (redirectAction) {
                render """__redirect__${urlMapped(Utils.getControllerName(redirectAction), redirectAction.method)}/${params.id ?: gormEntity.ident() ?: ''}"""
            } else render """__reload__"""
        }
    }

    // TODO: Implement cases where formSpecifier is not null
    void saveThenRedirectOrRenderErrors(final Class<? extends GormEntity> aClass, final MC redirectAction, final FieldInfo[] lockedFields = null) {
        redirectOrRenderErrors(save(aClass, lockedFields), redirectAction)
    }

    void saveThenReloadOrRenderErrors(final Class<? extends GormEntity> aClass, final FieldInfo[] lockedFields = null) {
        redirectOrRenderErrors(save(aClass, lockedFields), null)
    }

    def saveOrRenderErrors(final Class<? extends GormEntity> aClass, final FieldInfo[] lockedFields = null) {
        GormEntity gormEntity = save(aClass, lockedFields)
        if (gormEntity.hasErrors()) {
            renderErrors(gormEntity.errors)
        } else render ''
    }

    def saveOrCloseModalAndRefresh(final Class<? extends GormEntity> aClass, final FieldInfo[] lockedFields = null) {
        GormEntity gormEntity = save(aClass, lockedFields)
        if (gormEntity.hasErrors()) {
            renderErrors(gormEntity.errors)
        } else render '__closeLastModalAndRefresh__'
    }

    def displayBlockOrRenderErrors(final GormEntity gormEntity, final UiBlockSpecifier blockSpecifier) {
        if (gormEntity.hasErrors()) {
            return renderErrors(gormEntity.errors)
        } else {
            taackUiService.visit(blockSpecifier)
        }
    }

}
