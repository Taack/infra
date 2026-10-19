package taack.render

import grails.artefact.controller.support.ResponseRenderer
import grails.compiler.GrailsCompileStatic
import grails.plugin.springsecurity.SpringSecurityService
import grails.validation.Validateable
import grails.web.api.ServletAttributes
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
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParseException

/**
 * Service managing automated data binding, validation, audit trailing, and persistence of GORM entities from HTTP form submissions.
 * <p>
 * Features include:
 * <ul>
 * <li>Field locking enforcement via {@link FieldInfo}</li>
 * <li>Custom field saving hooks registered with {@link #registerFieldCustomSavingClosure}</li>
 * <li>Automatic audit tracking (user created/updated, date created/updated, domain history)</li>
 * <li>Form validation and AJAX response rendering</li>
 * </ul>*/
@GrailsCompileStatic
class TaackSaveService implements ResponseRenderer, ServletAttributes {
    TaackUiService taackUiService
    SpringSecurityService springSecurityService
    SessionFactory sessionFactory

    private final static Map<String, Closure> fieldCustomSavingClosures = [:]

    /**
     * Registers a custom saving closure for a specific entity field.
     *
     * @param fieldName the target field name
     * @param closure closure executing the custom save logic: {@code (GormEntity entity, Map params) -> ...}
     */
    static final void registerFieldCustomSavingClosure(String fieldName, Closure closure) {
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

    final String urlMapped(String controller, String action, Validateable validateable, boolean isAjax = false) {
        def p = params
        if (isAjax && params && !params.containsKey('isAjax')) {
            p = new HashMap<String, Object>()
            validateable.class.getDeclaredFields().each {
                params.containsKey(it.name)
                p.put(it.name, params[it.name])
            }
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

    private static <D extends GormEntity> D getGorm(Long id, Class<D> classD, Session session) {
        D gormEntity = id ? session.find(classD, id) : classD.getDeclaredConstructor().newInstance()
        return gormEntity
    }
    /**
     * Binds request parameters and validates the entity without persisting it to the database.
     *
     * @param aClass GORM entity class
     * @param lockedFields optional array of fields protected from binding
     * @return the bound, unpersisted entity instance
     */
    final <T extends GormEntity> T prepareSave(final Class<T> aClass, final FieldInfo[] lockedFields = null) {
        save(aClass, lockedFields, true)
    }

    /**
     * Binds request parameters to an entity resolved by {@code params.id} (or creates a new instance) and persists it.
     *
     * @param aClass GORM entity class
     * @param lockedFields optional array of fields protected from binding
     * @param doNotSave if {@code true}, validates and binds without executing {@code save()}
     * @return the bound entity instance
     */
    final <T extends GormEntity> T save(final Class<T> aClass, final FieldInfo[] lockedFields = null, final boolean doNotSave = false) {
        final Long id = params.long('id')
        T gormEntity = getGorm(id, aClass, sessionFactory.currentSession)
        save(gormEntity, lockedFields, doNotSave)
    }

    private static Long associationId(Object posted) {
        Object id = posted
        if (posted instanceof Map) {
            Map nested = posted as Map
            if (nested.size() != 1 || !nested.containsKey('id')) throw new SecurityException('Attempt to change pointed data')
            id = nested['id']
        }
        String idString = id?.toString()
        idString ? idString.toLong() : null
    }

    /**
     * Binds request parameters to entity fields with type conversion, field-locking enforcement, and dirty checking.
     * <p>
     * Handles enums, date parsing, numbers (locale-sensitive), collections of GORM entities, nested maps,
     * and triggers any registered custom saving closures.
     *
     * @param gormEntity the target entity instance to populate
     * @param p the HTTP request parameters map
     * @param lockedFields optional array of locked fields; if the first element is {@code null},
     *                     acts as an allowlist (only subsequent fields are editable)
     */
    final void bindObject(Object gormEntity, GrailsParameterMap p, final FieldInfo[] lockedFields = null) {
        boolean isDirtyCheckable = false
        if (DirtyCheckable.isAssignableFrom(gormEntity.class)) {
            isDirtyCheckable = true
        }

//        Set<String> belongsToKey = []
//        if (gormEntity.class.hasProperty('belongsTo')) {
//            if (Map.isAssignableFrom(gormEntity['belongsTo'].class)) {
//                Map belongsTo = gormEntity['belongsTo'] as Map
//                if (belongsTo) {
//                    belongsToKey = belongsTo.keySet()
//                }
//            }
//        }

        List<Field> classFields = (gormEntity.class.declaredFields + gormEntity.class.superclass.declaredFields).findAll {
            Field f -> !['id', 'filePath', 'version', 'userCreated', 'userUpdated', 'userLastUpdated', 'dateCreated', 'lastUpdated'].contains(f.name) && !(f.name.contains('_') || f.name.contains('$')) && !(Modifier.isFinal(f.modifiers) || Modifier.isStatic(f.modifiers) || Modifier.isTransient(f.modifiers)) && /*(*/f.name in p.keySet() //|| f.name in belongsToKey)
        }

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
                    gormEntity[includedField.name] = enumInstance
                    if (isDirtyCheckable) (gormEntity as DirtyCheckable).markDirty(includedField.name)
                }
            } else if (Map.isAssignableFrom(includedField.type)) {
                Map entries = [:]
                Set<String> mapKeys = p.keySet().findAll { it -> it.toString().startsWith(includedField.name + '.')
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
                Class genericType = (includedField.genericType as ParameterizedType).getActualTypeArguments()[0] as Class
                if (GormEntity.isAssignableFrom(genericType)) {
                    List<Long> ids = p.list(includedField.name).grep { !it.toString().empty }.collect { it.toString().toLong() }
                    if ((gormEntity[includedField.name] as Collection<GormEntity>)*.ident() as Set<Long> != ids as Set<Long>) {
                        Collection<GormEntity> gormEntityField = gormEntity[includedField.name] as Collection<GormEntity>
                        gormEntityField.clear()
                        // The other way is to add only new elements / remove only some elements ...
                        gormEntityField.addAll(p.list(includedField.name).collect {
                            getGorm(it as Long, genericType as Class<GormEntity>, sessionFactory.currentSession)
                        } as Collection<GormEntity>)
                        if (isDirtyCheckable) (gormEntity as DirtyCheckable).markDirty(includedField.name)
                    }
                } else if (genericType.isEnum()) {
                    List<Enum> enums = p.list(includedField.name).grep { !it.toString().empty }.collect {
                        Enum.valueOf(genericType as Class<Enum>, it.toString())
                    }
                    if ((gormEntity[includedField.name] as Collection<Enum>)*.name() as Set<String> != enums*.name() as Set<String>) {
                        Collection<Enum> enumField = gormEntity[includedField.name] as Collection<Enum>
                        enumField.clear()
                        // The other way is to add only new elements / remove only some elements ...
                        enumField.addAll(enums)
                        if (isDirtyCheckable) (gormEntity as DirtyCheckable).markDirty(includedField.name)
                    }
                } else if (String.isAssignableFrom(genericType)) {
                    List<String> strings = p.list(includedField.name).grep { !it.toString().empty }
                    if (gormEntity[includedField.name] as Set<String> != strings as Set<String>) {
                        Collection<String> stringField = gormEntity[includedField.name] as Collection<String>
                        stringField.clear()
                        // The other way is to add only new elements / remove only some elements ...
                        stringField.addAll(strings)
                        if (isDirtyCheckable) (gormEntity as DirtyCheckable).markDirty(includedField.name)
                    }

                }
            } else if (GormEntity.isAssignableFrom(includedField.type)) {
                if (!p[includedField.name]?.toString()?.empty) {
                    Long entryId = associationId(p[includedField.name])
                    if (entryId == null) {
                        if (gormEntity[includedField.name] != null) {
                            gormEntity[includedField.name] = null
                            if (isDirtyCheckable) (gormEntity as DirtyCheckable).markDirty(includedField.name)
                        }
                    } else if ((gormEntity[includedField.name + 'Id'] as Long) != entryId) {
                        gormEntity[includedField.name] = getGorm(entryId, includedField.type as Class<GormEntity>, sessionFactory.currentSession)
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
                    Field valueField = includedField
                    Object value = p[includedField.name]
                    String valueString = value?.toString()
                    boolean notEmpty = !value.toString().empty
                    //valueField.setAccessible(true)
                    if (notEmpty) {
                        String i18n = 'typeMismatch'
                        try {
                            if (Boolean.isAssignableFrom(valueField.type) || boolean.isAssignableFrom(valueField.type)) {
                                Boolean valueStringBoolean = valueString.toBoolean()
                                if (gormEntity[includedField.name] != valueStringBoolean) {
                                    gormEntity[includedField.name] = valueStringBoolean
                                    if (isDirtyCheckable) (gormEntity as DirtyCheckable).markDirty(includedField.name)
                                }
                            } else if (Integer.isAssignableFrom(valueField.type) || int.isAssignableFrom(valueField.type)) {
                                i18n = 'typeMismatch.java.lang.Integer'
                                Integer valueStringInteger = valueString.toInteger()
                                if (gormEntity[includedField.name] != valueStringInteger) {
                                    gormEntity[includedField.name] = valueStringInteger
                                    if (isDirtyCheckable) (gormEntity as DirtyCheckable).markDirty(includedField.name)
                                }
                            } else if (BigDecimal.isAssignableFrom(valueField.type)) {
                                i18n = 'typeMismatch.java.math.BigDecimal'
                                NumberFormat nf = DecimalFormat.getInstance(LocaleContextHolder.locale)
                                if (nf instanceof DecimalFormat) nf.setParseBigDecimal(true)
                                BigDecimal valueStringBigDecimal = nf.parse(valueString)
                                if (gormEntity[includedField.name] != valueStringBigDecimal) {
                                    gormEntity[includedField.name] = valueStringBigDecimal
                                    if (isDirtyCheckable) (gormEntity as DirtyCheckable).markDirty(includedField.name)
                                }
                            } else if (Double.isAssignableFrom(valueField.type) || double.isAssignableFrom(valueField.type)) {
                                i18n = 'typeMismatch.java.lang.Double'
                                Double valueStringDouble = valueString.toDouble()
                                if (gormEntity[includedField.name] != valueStringDouble) {
                                    gormEntity[includedField.name] = valueStringDouble
                                    if (isDirtyCheckable) (gormEntity as DirtyCheckable).markDirty(includedField.name)
                                }
                            } else if (Float.isAssignableFrom(valueField.type) || float.isAssignableFrom(valueField.type)) {
                                i18n = 'typeMismatch.java.lang.Float'
                                Float valueStringFloat = valueString.toFloat()
                                if (gormEntity[includedField.name] != valueStringFloat) {
                                    gormEntity[includedField.name] = valueStringFloat
                                    if (isDirtyCheckable) (gormEntity as DirtyCheckable).markDirty(includedField.name)
                                }
                            } else if (Long.isAssignableFrom(valueField.type) || long.isAssignableFrom(valueField.type)) {
                                i18n = 'typeMismatch.java.lang.Long'
                                Long valueStringLong = valueString.toLong()
                                if (gormEntity[includedField.name] != valueStringLong) {
                                    gormEntity[includedField.name] = valueStringLong
                                    if (isDirtyCheckable) (gormEntity as DirtyCheckable).markDirty(includedField.name)
                                }
                            } else if (Short.isAssignableFrom(valueField.type) || short.isAssignableFrom(valueField.type)) {
                                i18n = 'typeMismatch.java.lang.Short'
                                Short valueStringShort = valueString.toShort()
                                if (gormEntity[includedField.name] != valueStringShort) {
                                    gormEntity[includedField.name] = valueStringShort
                                    if (isDirtyCheckable) (gormEntity as DirtyCheckable).markDirty(includedField.name)
                                }
                            } else if (Date.isAssignableFrom(valueField.type)) {
                                i18n = 'typeMismatch.java.lang.Date'
                                Date valueStringDate = DateFormat.parse(valueString.length() < 10 ? "yyyy-MM-dd" : "yyyy-MM-ddTHH:mm", valueString)
                                if (gormEntity[includedField.name] != valueStringDate) {
                                    gormEntity[includedField.name] = valueStringDate
                                    if (isDirtyCheckable) (gormEntity as DirtyCheckable).markDirty(includedField.name)
                                }
                            } else {
                                if (gormEntity[includedField.name] != value) {
                                    gormEntity[includedField.name] = value
                                    if (isDirtyCheckable) (gormEntity as DirtyCheckable).markDirty(includedField.name)
                                }
                            }
                        } catch (ParseException | NumberFormatException | IllegalArgumentException parseException) {
                            log.error(parseException.message)
                            (gormEntity as GormEntity).errors.rejectValue(includedField.name, i18n, [params[includedField.name]] as Object[], null)
//                            gormEntity.properties[includedField.name] = params[includedField.name]
                            if (isDirtyCheckable) (gormEntity as DirtyCheckable).markDirty(includedField.name)
                        }

                    } else {
                        if (gormEntity[includedField.name] != value) {
                            gormEntity[includedField.name] = null // ''
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
        gormEntity.class.declaredFields*.name.intersect(fieldCustomSavingClosures.keySet()).each { fieldName -> fieldCustomSavingClosures.get(fieldName).call(gormEntity, params)
        }
    }

    /**
     * Binds request parameters, updates audit metadata (creator, updater), handles domain history cloning,
     * validates, and persists the GORM entity.
     *
     * @param gormEntity the entity instance to save
     * @param lockedFields optional array of protected fields [null, f1, f2, ...] lock all field but f1, f2, ... but [f1, f2, ...] lock field f1, f2, ...
     * @param doNotSave if {@code true}, validates and binds without calling {@code save(flush: true)}
     * @param doNotBindParams if {@code true}, skips parameter binding (useful for internal history saves)
     * @return the processed entity instance
     * @throws SecurityException if all fields are locked or binding security is violated
     */
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

        if (!doNotSave && !gormEntity.hasErrors()) {
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
    /**
     * Formats validation errors into Taack-specific HTML markup delimited by
     * {@code __ErrorKeyStart__<field>:<errors>__ErrorKeyEnd__} for client-side field highlighting.
     *
     * @param errors the Spring/Grails validation errors
     * @return HTML string formatted for Taack UI error rendering
     */
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

    /**
     * Evaluates validation on a {@link Validateable} object; renders errors if invalid,
     * or outputs the {@code '__reload__'} client instruction on success.
     *
     * @param validateable the command object or validateable instance to check
     * @return rendered error markup or {@code '__reload__'}
     */
    def reloadOrRenderErrors(Validateable validateable) {
        if (validateable.hasErrors()) {
            return renderErrors(validateable.errors)
        } else {
            render """__reload__"""
        }
    }

    /**
     * Evaluates a {@link Validateable} object; renders errors if invalid, or generates a client
     * redirection instruction ({@code '__redirect__<url>'}) using the validateable properties as parameters.
     *
     * @param validateable the command object to validate
     * @param redirectAction method closure of the target action (or {@code null} to emit {@code '__reload__'})
     * @return rendered error markup, a {@code '__redirect__...'} token, or {@code '__reload__'}
     */
    def redirectOrRenderErrors(Validateable validateable, final MC redirectAction = null) {
        if (validateable.hasErrors()) {
            return renderErrors(validateable.errors)
        } else {
            if (redirectAction) {
                render """__redirect__${urlMapped(Utils.getControllerName(redirectAction), redirectAction.method, validateable)}"""
            } else render """__reload__"""
        }
    }

    /**
     * Evaluates a {@link GormEntity}; renders errors if invalid, or generates a client
     * redirection instruction ({@code '__redirect__<url>/<id>'}) targeting the entity.
     *
     * @param gormEntity the entity to validate
     * @param redirectAction method closure of the target action (or {@code null} to emit {@code '__reload__'})
     * @return rendered error markup, a {@code '__redirect__...'} token, or {@code '__reload__'}
     */
    def redirectOrRenderErrors(final GormEntity gormEntity, final MC redirectAction = null) {
        if (gormEntity.hasErrors()) {
            return renderErrors(gormEntity.errors)
        } else {
            if (redirectAction) {
                render """__redirect__${urlMapped(Utils.getControllerName(redirectAction), redirectAction.method)}/${params.id ?: gormEntity.ident() ?: ''}"""
            } else render """__reload__"""
        }
    }

    /**
     * Binds and saves an entity of type {@code aClass}, then redirects to the target action
     * on success or renders validation errors on failure.
     *
     * @param aClass GORM entity class
     * @param redirectAction method closure of the destination action
     * @param lockedFields optional array of protected fields
     */
    void saveThenRedirectOrRenderErrors(final Class<? extends GormEntity> aClass, final MC redirectAction, final FieldInfo[] lockedFields = null) {
        redirectOrRenderErrors(save(aClass, lockedFields), redirectAction)
    }

    /**
     * Binds and saves an entity of type {@code aClass}, then triggers a page reload ({@code '__reload__'})
     * on success or renders validation errors on failure.
     *
     * @param aClass GORM entity class
     * @param lockedFields optional array of protected fields
     */
    void saveThenReloadOrRenderErrors(final Class<? extends GormEntity> aClass, final FieldInfo[] lockedFields = null) {
        redirectOrRenderErrors(save(aClass, lockedFields), null)
    }

    /**
     * Persists the entity and returns either the validation errors or a UI instruction to close the current modal and refresh.
     *
     * @param aClass GORM entity class to bind and save
     * @param lockedFields optional array of fields protected from binding
     * @return HTML error markup if invalid, or {@code ''} on success
     */
    def saveOrRenderErrors(final Class<? extends GormEntity> aClass, final FieldInfo[] lockedFields = null) {
        GormEntity gormEntity = save(aClass, lockedFields)
        if (gormEntity.hasErrors()) {
            renderErrors(gormEntity.errors)
        } else render ''
    }

    /**
     * Persists the entity and returns either the validation errors or a UI instruction to close the current modal and refresh.
     *
     * @param aClass GORM entity class to bind and save
     * @param lockedFields optional array of fields protected from binding
     * @return HTML error markup if invalid, or {@code '__closeLastModalAndRefresh__'} on success
     */
    def saveOrCloseModalAndRefresh(final Class<? extends GormEntity> aClass, final FieldInfo[] lockedFields = null) {
        GormEntity gormEntity = save(aClass, lockedFields)
        if (gormEntity.hasErrors()) {
            renderErrors(gormEntity.errors)
        } else render '__closeLastModalAndRefresh__'
    }

    /**
     * Renders a UI block if the entity has no validation errors, or returns the rendered errors otherwise.
     *
     * @param gormEntity the validated entity
     * @param blockSpecifier the UI block to render on success
     * @return error markup if errors exist, or visits the block specifier
     */
    def displayBlockOrRenderErrors(final GormEntity gormEntity, final UiBlockSpecifier blockSpecifier) {
        if (gormEntity.hasErrors()) {
            return renderErrors(gormEntity.errors)
        } else {
            taackUiService.visit(blockSpecifier)
        }
    }

}
