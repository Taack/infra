package taack.app

import grails.compiler.GrailsCompileStatic
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.MethodClosure
import org.jetbrains.annotations.NotNull
import taack.render.TaackUiService
import taack.ui.EnumOption
import taack.ui.dsl.helper.Utils

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
    int compareTo(@NotNull Object o) {
        return this.toString() <=> o.toString()
    }

    @Override
    String toString() {
        return Utils.getControllerName(entryPoint) + '/' + entryPoint.method
    }
}

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
