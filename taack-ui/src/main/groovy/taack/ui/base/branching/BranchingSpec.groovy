package taack.ui.base.branching

import groovy.transform.CompileStatic

final class ConditionBool {
    final String name
    final Closure<Boolean> condition

    ConditionBool(String name, Closure<Boolean> condition) {
        this.name = name
        this.condition = condition
    }
}

@CompileStatic
trait BranchingSpec {

    IBranchingExecute branchingExecute = new BranchingExecute()

    BranchingSpec ifCondition(ConditionBool conditionSpec, Closure cOk) {
        branchingExecute.ifCondition conditionSpec, cOk
        this
    }

    BranchingSpec ifNotCondition(ConditionBool conditionSpec, Closure cNotOk) {
        branchingExecute.ifNotCondition conditionSpec, cNotOk
        this
    }

    BranchingSpec elseCondition(Closure cElse) {
        branchingExecute.elseCondition cElse
        this
    }


}
