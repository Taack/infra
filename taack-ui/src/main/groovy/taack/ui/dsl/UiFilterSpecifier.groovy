package taack.ui.dsl

import groovy.transform.CompileStatic
import taack.ast.type.FieldInfo
import taack.ui.dsl.filter.FilterCommon
import taack.ui.dsl.filter.FilterSpec
import taack.ui.dsl.filter.IUiFilterVisitor

/**
 * Class describing a filter to display. A filter only lives with a table {@link UiTableSpecifier}, they are
 * associated via {@link taack.ui.dsl.block.BlockSpec#tableFilter(java.lang.String, UiFilterSpecifier, java.lang.String, UiTableSpecifier)}
 * in the same block element.
 *
 * A table only block create an empty filter to manage sorting and pagination
 */
@CompileStatic
final class UiFilterSpecifier {
    Closure<FilterSpec> closure
    Class aClass
    Map<String, ? extends Object> additionalParams
    boolean hasSec = false

    /**
     * Allow to draw the filter
     *
     * @param aClass class to filter after
     * @param additionalParams additional parameters to pass to action
     * @param closure closure describing the filter (see {@link FilterSpec})
     * @return return itself
     */
    UiFilterSpecifier ui(final Class aClass, Map<String, ? extends Object> additionalParams = null, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = FilterSpec) final Closure closure) {
        this.closure = closure
        this.aClass = aClass
        this.additionalParams = additionalParams
        this
    }

    /**
     * Allow to draw the filter
     *
     * @param aClass class to filter after
     * @param closure closure describing the filter (see {@link FilterSpec})
     * @param fieldInfos add other parameters to the action targeted by the filter (see {@link taack.render.TaackUiService#ajaxBind(java.lang.Class)}
     * @return return itself
     */
    UiFilterSpecifier ui(final Class aClass, @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = FilterSpec) final Closure closure, final FieldInfo<?>... fieldInfos) {
        this.closure = closure
        this.aClass = aClass
        this.additionalParams = fieldInfos.toList().findAll {it.value }.collectEntries {
            if (it.value.hasProperty('id'))
                new MapEntry('ajaxParams.' + it.fieldName + '.id', it.value.getAt('id'))
            else
                new MapEntry('ajaxParams.' + it.fieldName, it.value.toString())
        }
        this
    }

    /**
     * Security Filter
     *
     * @param aClass class to filter after
     * @param closure closure describing the filter (see {@link FilterSpec})
     * @return return itself
     */
    UiFilterSpecifier sec(final Class aClass, @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = FilterCommon) final Closure closure, final FieldInfo<?>... fieldInfos) {
        this.closure = closure
        this.aClass = aClass
        this.hasSec = true
        this
    }

    /**
     * Visit the filter (see {@link IUiFilterVisitor})
     *
     * @param filterVisitor the visitor
     */
    void visitFilter(final IUiFilterVisitor filterVisitor) {
        if (filterVisitor && closure) {
            filterVisitor.visitFilter(aClass, additionalParams)
            if (hasSec) closure.delegate = new FilterCommon(filterVisitor)
            else closure.delegate = new FilterSpec(filterVisitor)
            closure.call()
            filterVisitor.visitFilterEnd()
        }
    }

    /**
     * Allow to append filters together
     *
     * @param filter filter to append
     */
    void join(UiFilterSpecifier filter) {
        if (filter) closure = closure << filter.closure
    }
}