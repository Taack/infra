package taack.jdbc.common.tql.equation;

import java.math.BigDecimal;

public class RootExpression implements EquationNode {
    enum Sign {
        EQ, LT, GT, LE, GE
    }

    public EquationNode left;
    public EquationNode right;
    public Sign sign;

    @Override
    public Kind getKind() {
        return right != null ? Kind.BOOLEAN : left.getKind();
    }

    @Override
    public Object getValue() throws Exception {
        if (right == null) {
            return left.getValue();
        }
        if (sign == Sign.EQ) return left.getValue().equals(right.getValue());

        switch (left.getKind()) {
            case STRING: throw new Exception("Cannot compare Strings");
            case BOOLEAN: throw new Exception("Cannot compare Boolean");
            case BIG_DECIMAL: {
                var ld = (BigDecimal) left.getValue();
                var rd = (BigDecimal) right.getValue();
                switch (sign) {
                    case EQ: return ld.equals(rd);
                    case GE: return ld.compareTo(rd) >= 0;
                    case GT: return ld.compareTo(rd) > 0;
                    case LT: return ld.compareTo(rd) < 0;
                    case LE: return ld.compareTo(rd) <= 0;
                };
            }
        }
        return null;
    }

    @Override
    public void addSiblingChildren(EquationNode equationNode) {
        if (left == null) left = equationNode;
        else right = equationNode;
    }

    @Override
    public void addTerminal(String terminal) {
        switch (terminal) {
            case ">": sign = Sign.GT; break;
            case ">=": sign = Sign.GE; break;
            case "<": sign = Sign.LT; break;
            case "<=": sign = Sign.LE; break;
        }
    }
}
