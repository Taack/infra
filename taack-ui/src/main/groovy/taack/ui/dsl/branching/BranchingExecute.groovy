package taack.ui.dsl.branching

import groovy.transform.CompileStatic

@CompileStatic
final class BranchingExecute implements IBranchingExecute {

    @Override
    void ifCondition(ConditionBool condition, Closure cOk) {
        if (condition.condition.call()) cOk.call()
    }

    @Override
    void ifNotCondition(ConditionBool condition, Closure cNotOk) {
        if (!condition.condition.call()) cNotOk.call()
    }

    @Override
    void elseCondition(Closure cElse) {
        cElse.call()
    }
}