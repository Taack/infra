package taack.jdbc.common.tql.equation;

import java.lang.invoke.WrongMethodTypeException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AdditionExpression implements EquationNode {
    enum Sign {
        PLUS, MINUS
    }

    List<EquationNode> left = new ArrayList<>();
    List<Sign> signs = new ArrayList<>();

    @Override
    public Kind getKind() {
        Kind kind = Kind.BIG_DECIMAL;
        for (var l : left) {
            if (l.getKind() == Kind.BOOLEAN) kind = Kind.BOOLEAN;
            if (l.getKind() == Kind.STRING) {
                kind = Kind.STRING;
                break;
            }
        }
        return kind;
    }

    private String minus(String left, String other) {
        char[] l = left.toCharArray();
        char[] r = other.toCharArray();
        int len = l.length;
        int i = len - 1;
        for (; i >= 0; i--) {
            if (l[i] != r[len - 1 - i]) break;
        }
        return left.substring(0, i);
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
                String tmp = leftIterator.next().getValue().toString();
                while (leftIterator.hasNext()) {
                    EquationNode l = leftIterator.next();
                    var current = l.getValue().toString();
                    var s = signIterator.next();
                    if (s == Sign.MINUS) {
                        tmp = minus(tmp, current);
                    } else {
                        tmp += current;
                    }
                }
                return tmp;
            case BOOLEAN: throw new WrongMethodTypeException("+ operator does not accept Booleans");
            case BIG_DECIMAL:
                BigDecimal d = (BigDecimal) leftIterator.next().getValue();
                while (leftIterator.hasNext()) {
                    EquationNode l = leftIterator.next();
                    var current = (BigDecimal) l.getValue();
                    var s = signIterator.next();
                    if (s == Sign.MINUS) {
                        d = d.subtract(current);
                    } else {
                        d = d.add(current);
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
        signs.add(terminal.equals("+")? Sign.PLUS : Sign.MINUS);
    }
}
