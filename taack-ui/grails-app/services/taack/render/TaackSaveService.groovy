package taack.render

import grails.artefact.controller.support.ResponseRenderer
import grails.compiler.GrailsCompileStatic
import grails.plugin.springsecurity.SpringSecurityService
import grails.validation.Validateable
import grails.web.api.ServletAttributes
import grails.web.databinding.DataBinder
import org.codehaus.groovy.runtime.MethodClosure as MC
import org.grails.datastore.gorm.GormEntity
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

import java.lang.reflect.Field

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
            Map p = params as Map
            p.remove('filePath')
            List<String> props = []
            if (gormEntity.class.hasProperty('belongsTo')) {
                Map properties = gormEntity.class.getProperties()
                if (properties['belongsTo'] instanceof Map) {
                    Map belongsTo = properties['belongsTo'] as Map
                    List<String> belongsToProps = belongsTo.keySet().collect { it.toString() }
                    for (String key in belongsToProps) {
                        if (params[key] instanceof Map) {
                            Map km = params[key]
                            if (km.size() == 1 && km.containsKey('id'))
                                props.add key
                            else throw new SecurityException('Attempt to change pointed data')
                        }
                    }
                    props.addAll(belongsTo.keySet().collect { it.toString() + '.id' })

                }
            }
            for (Field includedField in (gormEntity.class.declaredFields + gormEntity.class.superclass.declaredFields)) {
                if (lockedFields && lockedFields[0] != null && lockedFields.contains(includedField))
                // Modify only fields that are not in lockedField
                    continue else if (lockedFields && lockedFields[0] == null && !lockedFields.contains(includedField))
                // Modify only fields that are after null. [null, f1, f2 ...]
                    continue

                if (includedField.name.contains('_') || includedField.name.contains('$')) continue
                if (GormEntity.isAssignableFrom(includedField.type)) {
                    if (p[includedField.name].toString().isLong()) props << includedField.name // + '.id'
                } else props << includedField.name
            }
            Map includedParams = new HashMap(p)
            includedParams.retainAll {
                props.contains(it.key)
            }
            bindData(gormEntity, includedParams, [include: props])
            gormEntity.class.declaredFields*.name.intersect(fieldCustomSavingClosures.keySet()).each {
                fieldName -> fieldCustomSavingClosures.get(fieldName).call(gormEntity, params)
            }
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
        }

        if (!doNotSave) {
            if (oldEntity && gormEntity.validate() && (doNotBindParams || gormEntity.isDirty())) {
                save(oldEntity, lockedFields, doNotSave, true)
            }
            gormEntity.save(flush: true, failsOnError: true)
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
