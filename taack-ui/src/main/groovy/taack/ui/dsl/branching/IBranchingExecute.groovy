package taack.ui.dsl.branching

interface IBranchingExecute {
    void ifCondition(ConditionBool condition, Closure cOk)
    void ifNotCondition(ConditionBool condition, Closure cOk)
    void elseCondition(Closure cElse)
}