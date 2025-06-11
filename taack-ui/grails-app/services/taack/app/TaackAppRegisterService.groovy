package taack.app

import grails.compiler.GrailsCompileStatic
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import org.grails.datastore.gorm.GormEntity
import taack.render.TaackUiService
import taack.ui.EnumOption
import taack.ui.dsl.helper.Utils
import taack.user.TaackUser

@CompileStatic
final class TaackApp implements Comparable {
    final MethodClosure entryPoint
    final String svg

    TaackApp(MethodClosure entryPoint, String svg) {
        this.entryPoint = entryPoint
        this.svg = svg
    }

    String getLabel() {
        TaackUiService.tr("${Utils.getControllerName(entryPoint)}.app" as String)
    }

    String getDesc() {
        TaackUiService.tr("${Utils.getControllerName(entryPoint)}.desc" as String)
    }

    @Override
    int compareTo( Object o) {
        return this.toString() <=> o.toString()
    }

    @Override
    String toString() {
        return Utils.getControllerName(entryPoint) + '/' + entryPoint.method
    }
}

@CompileStatic
final class TaackLinkClass {
    final Class<? extends GormEntity> linkClass
    final MethodClosure showMethod
    final Closure<List<TaackUser>> notificationUserListWhenCreating

    TaackLinkClass(Class<? extends GormEntity> linkClass, MethodClosure showMethod, Closure<List<TaackUser>> notificationUserListWhenCreating = null) {
        this.linkClass = linkClass
        this.showMethod = showMethod
        this.notificationUserListWhenCreating = notificationUserListWhenCreating
    }

    String getController() {
        Utils.getControllerName(showMethod)
    }

    String getAction() {
        showMethod.method
    }
}

@GrailsCompileStatic
class TaackAppRegisterService {
    private final static SortedSet<TaackApp> taackApps = new TreeSet<TaackApp>()
    private final static List<TaackLinkClass> taackLinkClasses = []

    static void register(TaackApp app, TaackLinkClass... linkClasses) {
        taackApps.add(app)
        taackLinkClasses.addAll(linkClasses)
    }

    static void register(TaackLinkClass... linkClasses) {
        taackLinkClasses.addAll(linkClasses)
    }

    static List<TaackApp> getApps() {
        taackApps.toList()
    }

    static EnumOption[] getOptions() {
        List<EnumOption> res = []
        apps.each {
            res.add new EnumOption(Utils.getControllerName(it.entryPoint), it.label)
        }
        res as EnumOption[]
    }

    static TaackLinkClass getTaackLinkClass(String clazz) {
        return taackLinkClasses.find { it.linkClass?.name == clazz }
    }
}
