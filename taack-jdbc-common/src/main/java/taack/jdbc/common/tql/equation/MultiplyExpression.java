package taack.jdbc.common.tql.equation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MultiplyExpression implements EquationNode {
    enum Sign {
        MULT, DIV
    }

    List<EquationNode> left = new ArrayList<>();
    List<Sign> signs = new ArrayList<>();

    @Override
    public Kind getKind() {
        return left.get(0).getKind();
    }

    @Override
    public Object getValue() throws Exception {
        if (left.size() == 1) {
            return left.get(0).getValue();
        }
        Iterator<Sign> signIterator = signs.iterator();
        Iterator<EquationNode> leftIterator = left.iterator();

        switch (getKind()) {
            case STRING:
            case BOOLEAN: throw new Exception("Cannot multiply Boolean or String");
            case BIG_DECIMAL: {
                BigDecimal d = (BigDecimal) leftIterator.next().getValue();
                while (leftIterator.hasNext()) {
                    EquationNode l = leftIterator.next();
                    var current = (BigDecimal) l.getValue();
                    var s = signIterator.next();
                    if (s == Sign.MULT) {
                        d = d.multiply(current);
                    } else {
                        d = d.divide(current, 5, RoundingMode.CEILING);
                    }
                }
                return d;
            }
        }
        return null;
    }

    @Override
    public void addSiblingChildren(EquationNode equationNode) {
        left.add(equationNode);
    }

    @Override
    public void addTerminal(String terminal) {
        signs.add(terminal.equals("*")? Sign.MULT : Sign.DIV);
    }
}
