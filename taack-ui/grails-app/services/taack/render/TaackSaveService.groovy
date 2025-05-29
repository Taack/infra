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
import taack.user.TaackUser

@GrailsCompileStatic
class TaackSaveService implements ResponseRenderer, ServletAttributes, DataBinder {
    TaackUiService taackUiService
    SpringSecurityService springSecurityService

    private final static Map<String, Closure> fieldCustomSavingClosures = [:]

    /**
     * Allow to register a custom save for specific field when saving a gormEntity. (Currently being used for "filePath" only)
     *
     * Example:
     *      TaackSaveService.registerFieldCustomSavingClosures("projectName", { GormEntity gormEntity, Map params ->
     *          if (gormEntity.hasProperty("projectName")) {
     *              gormEntity["projectName"] = "CUSTOM_" + params["projectName"]
     *          }
     *      }
     *
     * @param fieldName: Name of target field (TODO: But cannot recognize a specific domain class. To implement it if needed.)
     * @param closure: Custom saving process
     */
    static final void registerFieldCustomSavingClosures(String fieldName, Closure closure) {
        fieldCustomSavingClosures.put(fieldName, closure)
    }

    final String urlMapped(String controller, String action, Map<String, ? extends Object> params = null, boolean isAjax = false) {
        def p = params
        if (isAjax && params && !params.containsKey('isAjax')) {
            p = new HashMap<String, Object>()
            p.putAll(params)
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

        long c1 = System.currentTimeMillis()

        long c2 = System.currentTimeMillis()
        if (gormEntity instanceof IDomainHistory && gormEntity.ident() != null) {
            T oldEntity = (gormEntity as IDomainHistory<T>).cloneDirectObjectData()
            save(oldEntity, lockedFields, doNotSave, true)
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
                } else {
                    if (id) {
                        if (bindingName.contains(',')) {
                            bindData(gormEntity, params, [include: bindingName.tokenize(',')])
                        } else bindData(gormEntity, params, [include: [bindingName]])
                    } else bindData(gormEntity, params)
                }
            } else {
                fieldCustomSavingClosures.each { e ->
                    if (gormEntity.hasProperty(e.key)) {
                        e.value.call(gormEntity, params as Map)
                        params.remove(e.key)
                    }
                }
                if (includeOrExclude) bindData(gormEntity, params, includeOrExclude)
                else bindData(gormEntity, params)
            }

        long c3 = System.currentTimeMillis()

        try {
            TaackUser currentUser = springSecurityService.currentUser as TaackUser
            if (gormEntity.hasChanged()) {
                if (gormEntity.hasProperty("userCreated") && gormEntity["userCreated"] == null) {
                    gormEntity["userCreated"] = currentUser
                }
                if (gormEntity.hasProperty("userUpdated")) {
                    gormEntity["userUpdated"] = currentUser
                }
            }
        } catch (ignored) {}

        long c4 = System.currentTimeMillis()
        if (!doNotSave) {
            gormEntity.save(failsOnError: true)
        }

        long c5 = System.currentTimeMillis()

        log.info "constrainedProperties: ${c2 - c1}ms, bindingName: ${c3 - c2}ms, gormEntity.hasChanged: ${c4 - c3}ms, save: ${c5 - c4}ms: ELAPSED:${c5 - c1}ms"

        if (!doNotSave) {
            if (gormEntity.hasErrors()) {
                log.error "${gormEntity.errors}"
            } else if (!id) {
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
