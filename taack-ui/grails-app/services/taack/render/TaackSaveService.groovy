package taack.render

import grails.artefact.controller.support.ResponseRenderer
import grails.compiler.GrailsCompileStatic
import grails.plugin.springsecurity.SpringSecurityService
import grails.web.api.ServletAttributes
import grails.web.databinding.DataBinder
import org.codehaus.groovy.runtime.MethodClosure as MC
import org.grails.datastore.gorm.GormEnhancer
import org.grails.datastore.gorm.GormEntity
import org.grails.datastore.gorm.GormStaticApi
import org.grails.orm.hibernate.cfg.GrailsHibernateUtil
import org.grails.plugins.web.taglib.ApplicationTagLib
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.validation.Errors
import taack.app.TaackAppRegisterService
import taack.ast.type.FieldInfo
import taack.domain.IDomainHistory
import taack.ui.dsl.UiBlockSpecifier
import taack.ui.dsl.helper.Utils
import taack.user.IUserCreated
import taack.user.IUserUpdated
import taack.user.TaackUser

@GrailsCompileStatic
class TaackSaveService implements ResponseRenderer, ServletAttributes, DataBinder {
    TaackUiService taackUiService
    SpringSecurityService springSecurityService

    private final static Map<String, Closure> fieldCustomSavingClosures = [:]

    /**
     * Allow to register a custom save for specific field when saving a gormEntity.
     *
     * Example:
     *      TaackSaveService.registerFieldCustomSavingClosure("projectName", { GormEntity gormEntity, Map params ->
     *          if (gormEntity instaceof Project) {
     *              gormEntity.projectName = "CUSTOM_" + gormEntity.projectName
     *              // gormEntity.projectName = "CUSTOM_" + params["projectName"]
     *          }
     *      }
     *
     * @param fieldName: Name of target field
     * @param closure: Custom saving process
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
////        if (IUserInterface.isAssignableFrom(beanClass) && beanClass.getAt("enumTransition")) {
////            IEnumTransition enumTransition = entity.getAt(beanClass.getAt("enumTransition") as String) as IEnumTransition
////            if (enumTransition.lockedFields?.contains('*') && enumTransition.lockedFields?.size() == 1) {
////                return true
////            }
////        }
//        return beanIsOwnerLocking(entity)
//    }

    private static <D extends GormEntity> D getGorm(Long id, Class<D> classD) {
        GormEntity gormEntity = ((GormStaticApi<D>) GormEnhancer.findStaticApi(classD)).get(id)
        if (!gormEntity) gormEntity = classD.getDeclaredConstructor().newInstance()
        return gormEntity
    }

    final <T extends GormEntity> T prepareSave(final Class<T> aClass, final FieldInfo[] lockedFields = null) {
        save(aClass, lockedFields, true)
    }

    final <T extends GormEntity> T save(final Class<T> aClass, final FieldInfo[] lockedFields = null, final boolean doNotSave = false) {
        final Long id = params.long("id")
        T gormEntity = getGorm(id, aClass)
        save(gormEntity, lockedFields, doNotSave)
    }

    final <T extends GormEntity> T save(final T gormEntity, final FieldInfo[] lockedFields = null, final boolean doNotSave = false, boolean doNotBindParams = false) {
        final id = gormEntity.ident()
        if (lockedFields && lockedFields.size() == 0) return null
        final String bindingName = params["fieldName"]
        Map includeOrExclude = null
        if (lockedFields) {
            if (lockedFields[0]) {
                includeOrExclude = [exclude: lockedFields*.fieldName]
            } else {
                includeOrExclude = [include: lockedFields[1..<lockedFields.size()]*.fieldName]
            }
        }
//
//        if (beanIsLocked(gormEntity))
//            if (!bindingName)// || !bindingName.equals(transitionName))
//                throw new SecurityException("bean is locked!")

        long c1 = System.currentTimeMillis()
// TODO very slow
//        gormEntity.getProperties().get('constrainedProperties').each {
//            if (it.properties.get('value')['widget'] == 'ajax') {
//                if (params[it.properties.get('key')] && (params[it.properties.get('key')] as String).isEmpty()) {
//                    params[it.properties.get('key')] = null
//                }
//            }
//        }

        long c2 = System.currentTimeMillis()
//        T oldEntity
        if (gormEntity instanceof IDomainHistory && gormEntity.ident() != null) {
//            if (gormEntity.dirty) {
            T oldEntity = (gormEntity as IDomainHistory<T>).cloneDirectObjectData()
            save(oldEntity, lockedFields, doNotSave, true)
//            } else return gormEntity
        }

        if (!doNotBindParams)
            if (bindingName) {
                if (lockedFields && lockedFields*.fieldName.contains(bindingName)) {
                    return null
                }
                if (bindingName.contains('.')) {
                    int iPoint = bindingName.indexOf('.')
                    String realFieldName = bindingName.substring(0, iPoint)
                    String key = bindingName.substring(iPoint + 1)
                    (gormEntity.getAt(realFieldName) as Map).put(key, params.get(bindingName))
//                } else if (bindingName.contains("_setKey_")) {
//                    int iPoint = bindingName.indexOf('_setKey_')
//                    String realFieldName = bindingName.substring(0, iPoint)
//                    String key = bindingName.substring(iPoint + 8)
//                    Object o = (gormEntity.getAt(realFieldName) as Map)[key]
//                    (gormEntity.getAt(realFieldName) as Map).remove(key)
//                    String newKey = params.get(bindingName)
//                    (gormEntity.getAt(realFieldName) as Map).put(newKey, o ?: "")
//                } else if (bindingName.contains("_setValue_")) {
//                    int iPoint = bindingName.indexOf('_setValue_')
//                    String realFieldName = bindingName.substring(0, iPoint)
//                    String key = bindingName.substring(iPoint + 10)
//                    (gormEntity.getAt(realFieldName) as Map)[key] = params.get(bindingName)
//                    println gormEntity.getAt(realFieldName)
                } else {
                    if (id) {
                        if (bindingName.contains(',')) {
                            bindData(gormEntity, params, [include: bindingName.tokenize(',')])
                        } else bindData(gormEntity, params, [include: [bindingName]])
                    } else bindData(gormEntity, params)
                }
            } else {
                Map p = params as Map
                p.remove("filePath") // TODO: gormEntity.save() will always fail on "filePath" if this field has been treated by bindData (Although we re-set a new value after). Why ??
                if (includeOrExclude) bindData(gormEntity, p, includeOrExclude)
                else bindData(gormEntity, p)

                gormEntity.class.declaredFields*.name.intersect(fieldCustomSavingClosures.keySet()).each { fieldName ->
                    fieldCustomSavingClosures.get(fieldName).call(gormEntity, params)
                }
            }

        long c3 = System.currentTimeMillis()

        try {
            TaackUser currentUser = springSecurityService.currentUser as TaackUser
            if (gormEntity.hasChanged()) {
                if (gormEntity instanceof IUserCreated && gormEntity.objectGetUserCreated() == null) {
                    gormEntity.objectSetUserCreated(currentUser)
                } else if (gormEntity.hasProperty("userCreated") && gormEntity["userCreated"] == null) {
                    gormEntity["userCreated"] = currentUser
                }
                if (gormEntity instanceof IUserUpdated) {
                    gormEntity.objectSetUserUpdated(currentUser)
                } else if (gormEntity.hasProperty("userUpdated")) {
                    gormEntity["userUpdated"] = currentUser
                } else if (gormEntity.hasProperty("userLastUpdated")) {
                    gormEntity["userLastUpdated"] = currentUser
                }
            }
        } catch (ignored) {}

        long c4 = System.currentTimeMillis()
        if (!doNotSave) {
//            if (oldEntity && gormEntity instanceof IDomainHistory && gormEntity.ident() != null) {
//                gormEntity.save(failsOnError: true)
//                oldEntity.save(failsOnError: true)
//                if (oldEntity.hasErrors()) {
//                    log.error "oldEntity errors: ${oldEntity.errors}"
//                }
//            } else {
            gormEntity.save(failsOnError: true)
//            }
        }

        long c5 = System.currentTimeMillis()

        log.info "constrainedProperties: ${c2 - c1}ms, bindingName: ${c3 - c2}ms, gormEntity.hasChanged: ${c4 - c3}ms, save: ${c5 - c4}ms: ELAPSED:${c5 - c1}ms"

        if (!doNotSave) {
            if (gormEntity.hasErrors()) {
                log.error "${gormEntity.errors}"
            } else if ((!(gormEntity instanceof IDomainHistory) && !id) || (gormEntity instanceof IDomainHistory && !doNotBindParams)) {
                TaackAppRegisterService.getTaackLinkClass(gormEntity.class.name)?.notificationUserListWhenCreating?.call(gormEntity)?.each { TaackUser u ->
                    u.addToUnreadRelatedDataList(gormEntity)
                }
            }
        }
        return gormEntity
    }

    def redirectOrRenderErrors(final GormEntity gormEntity, final MC redirectAction = null) {
        if (gormEntity.hasErrors()) {
            Errors errors = gormEntity.errors

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
        } else {
//            String rs = params.containsKey('recordState') && params['recordState'] ? '?recordState=' + params['recordState'] : ''

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

    def displayBlockOrRenderErrors(final GormEntity gormEntity, final UiBlockSpecifier blockSpecifier) {
        if (gormEntity.hasErrors()) {
            Errors errors = gormEntity.errors

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
        } else {
            render taackUiService.visit(blockSpecifier)
        }
    }

}
