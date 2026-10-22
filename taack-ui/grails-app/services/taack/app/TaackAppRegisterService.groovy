package taack.app

import grails.compiler.GrailsCompileStatic
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import taack.render.TaackUiService
import taack.ui.EnumOption
import taack.ui.dsl.helper.Utils

/**
 * Represents an application module entry point in Taack UI, encapsulating its controller action and SVG icon.
 */
@CompileStatic
final class TaackApp implements Comparable {
    final MethodClosure entryPoint
    final String svg

    /**
     * Creates a new application descriptor. i18n allow to give a description and an app name localized.
     *
     * @param entryPoint controller action closure (e.g. {@code MyController.&index})
     * @param svg SVG markup representing the application icon
     */
    TaackApp(MethodClosure entryPoint, String svg) {
        this.entryPoint = entryPoint
        this.svg = svg
    }

    String getControllerName() {
        Utils.getControllerName(entryPoint)
    }

    String getLabel() {
        TaackUiService.tr("${controllerName}.app" as String)
    }

    String getDesc() {
        TaackUiService.tr("${controllerName}.desc" as String)
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

/**
 * Registry service for managing application modules ({@link TaackApp}) available in your Intranet.
 * <p>
 * Provides methods to register applications at startup and retrieve them as lists or UI options.
 */
@GrailsCompileStatic
class TaackAppRegisterService {
    private final static SortedSet<TaackApp> taackApps = new TreeSet<TaackApp>()

    static void register(TaackApp app) {
        taackApps.add(app)
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
}
