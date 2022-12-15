package taack.jdbc.common.tql.equation;

import java.math.BigDecimal;

public class SignedExpression implements EquationNode {
    enum Sign {
        PLUS, MINUS
    }

    EquationNode left = null;
    Sign sign = null;

    @Override
    public Kind getKind() {
        return left.getKind();
    }

    @Override
    public Object getValue() throws Exception {
        if (sign == null || sign == Sign.PLUS)
            return left.getValue();
        else return ((BigDecimal)left.getValue()).negate();
    }

    @Override
    public void addSiblingChildren(EquationNode equationNode) {
        left = equationNode;
    }

    @Override
    public void addTerminal(String terminal) {
        sign = terminal.equals("+") ? Sign.PLUS : Sign.MINUS;
    }
}
