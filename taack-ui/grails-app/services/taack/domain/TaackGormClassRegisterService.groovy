package taack.domain

import grails.compiler.GrailsCompileStatic
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import org.grails.datastore.gorm.GormEntity
import taack.ui.dsl.helper.Utils
import taack.user.TaackUser

/**
 * Bind additional information to a GormClass, so that the gormEntity could have default behaviors (For example Default showing page, Default displayed label, ...)
 *
 * Use {@link taack.domain.TaackGormClassRegisterService#register(TaackGormClass... gormClasses)} to register a TaackGormClass :
 *
 *      TaackGormClassRegisterService.register(
 *              new TaackGormClass(gormClass).builder.setShowMethod(showMethod).setShowLabel(showLabel).build()
 *      )
 *
 *
 * @attribute gormClass The Gorm class
 * @attribute showMethod The action to show when clicking on the object. The access control should be managed from there
 * @attribute showLabel The closure to define the displayed label of this object
 * @attribute notification The notification to send to certain users when a new object is created
 */
@CompileStatic
final class TaackGormClass {
    final Class<? extends GormEntity> gormClass
    MethodClosure showMethod
    Closure<String> showLabel
    TaackNotification notification

    TaackGormClass(Class<? extends GormEntity> c) {
        this.gormClass = c
    }

    /**
     * Define the details of the notification when a new object is created
     *
     * @attribute recipients The closure to define the users who should receive the notification
     * @attribute title The closure to define the title/groupName of the notification
     */
    final class TaackNotification {
        final private Closure<List<TaackUser>> recipients
        final private Closure<String> title

        TaackNotification(Closure<List<TaackUser>> recipients, Closure<String> title) {
            this.recipients = recipients
            this.title = title
        }

        Closure<List<TaackUser>> getRecipientsClosure() {
            recipients
        }

        Closure<String> getTitleClosure() {
            title
        }
    }

    /**
     * Allow to build a TaackGormClass instance quickly :
     *
     *      new TaackGormClass(gormClass).builder.setShowMethod(showMethod).setShowLabel(showLabel).build()
     */
    TaackGormClassBuilder getBuilder() {
        return new TaackGormClassBuilder(this)
    }
    final class TaackGormClassBuilder {
        private TaackGormClass taackGormClass

        TaackGormClassBuilder(TaackGormClass taackGormClass) {
            this.taackGormClass = taackGormClass
        }

        TaackGormClassBuilder setShowMethod(MethodClosure showMethod) {
            taackGormClass.showMethod = showMethod
            this
        }

        TaackGormClassBuilder setShowLabel(Closure<String> showLabel) {
            taackGormClass.showLabel = showLabel
            this
        }

        TaackGormClassBuilder setNotification(Closure recipients, Closure title) {
            taackGormClass.notification = new TaackNotification(recipients, title)
            this
        }

        TaackGormClass build() {
            taackGormClass
        }
    }

    String getShowController() {
        Utils.getControllerName(showMethod)
    }

    String getShowAction() {
        showMethod.method
    }
}

@GrailsCompileStatic
class TaackGormClassRegisterService {
    private final static List<TaackGormClass> taackGormClasses = []

    static void register(TaackGormClass... gormClasses) {
        gormClasses.each { TaackGormClass it ->
            if (!getTaackGormClass(it.gormClass.name)) {
                taackGormClasses.add(it)
            }
        }
    }

    static void registerShowMethod(Class<? extends GormEntity> gormClass, MethodClosure showMethod) {
        TaackGormClass c = getTaackGormClass(gormClass.name)
        if (!c) {
            taackGormClasses.add(new TaackGormClass(gormClass).builder.setShowMethod(showMethod).build())
        } else {
            c.builder.setShowMethod(showMethod)
        }
    }

    static void registerShowLabel(Class<? extends GormEntity> gormClass, Closure<String> showLabel) {
        TaackGormClass c = getTaackGormClass(gormClass.name)
        if (!c) {
            taackGormClasses.add(new TaackGormClass(gormClass).builder.setShowLabel(showLabel).build())
        } else {
            c.builder.setShowLabel(showLabel)
        }
    }

    static void registerNotification(Class<? extends GormEntity> gormClass, Closure<List<TaackUser>> recipients, Closure<String> title) {
        TaackGormClass c = getTaackGormClass(gormClass.name)
        if (!c) {
            taackGormClasses.add(new TaackGormClass(gormClass).builder.setNotification(recipients, title).build())
        } else {
            c.builder.setNotification(recipients, title)
        }
    }

    static TaackGormClass getTaackGormClass(String clazz) {
        return taackGormClasses.find { TaackGormClass it -> it.gormClass?.name == clazz }
    }
}
