package taack.jdbc.common.tql.equation;

import taack.jdbc.common.tql.equation.common.Helper;

import java.math.BigDecimal;

public class LiteralExpression implements EquationNode {
    String left;

    @Override
    public Kind getKind() {
        return Helper.getKind(left);
    }

    @Override
    public Object getValue() throws Exception {
        switch (getKind()) {
            case BIG_DECIMAL:
                return new BigDecimal(left);
            case BOOLEAN:
                return left.equals("true");
            case STRING:
                return left;
        };
        return null;
    }

    @Override
    public void addSiblingChildren(EquationNode equationNode) {
        // Not applicable here
    }

    @Override
    public void addTerminal(String terminal) {
        left = terminal;
    }
}
