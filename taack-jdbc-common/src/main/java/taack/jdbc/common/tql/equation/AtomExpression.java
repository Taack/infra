package taack.jdbc.common.tql.equation;


import taack.jdbc.common.tql.equation.common.Helper;

import java.lang.invoke.WrongMethodTypeException;

public class AtomExpression implements EquationNode {
    EquationNode passThrough;
    String left = null;

    @Override
    public Kind getKind() {
        return left != null && !left.equals("(") && !left.equals("!") ? Helper.getKind(left) : passThrough.getKind();
    }

    @Override
    public Object getValue() throws Exception {
        if (left != null && left.equals("!")) {
            if (passThrough.getKind() == Kind.BOOLEAN) {
                Boolean b = (Boolean) passThrough.getValue();
                return !b;
            } else {
                throw new WrongMethodTypeException("'!' expect a Boolean value as argument");
            }
        }
        return left != null && !left.equals("(") ? left.substring(1, left.length() - 1) : passThrough.getValue();
    }

    @Override
    public void addSiblingChildren(EquationNode equationNode) {
        passThrough = equationNode;
    }

    @Override
    public void addTerminal(String terminal) {
        if (left == null) left = terminal;
    }
}
