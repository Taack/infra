package taack.jdbc.common.tql.equation.common;

import taack.jdbc.common.tql.equation.EquationNode;

public final class Helper {
    public static EquationNode.Kind getKind(String value) {
        if (value.matches("[0-9.]*"))
            return EquationNode.Kind.BIG_DECIMAL;
        else if (value.equals("true") || value.equals("false"))
            return EquationNode.Kind.BOOLEAN;
        else return EquationNode.Kind.STRING;
    }

    public static String getTableName(String variableName) {
        return variableName.substring(0, variableName.lastIndexOf('.'));
    }

    public static String getSimpleVariableName(String variableName) {
        return variableName.substring(variableName.lastIndexOf('.') + 1);
    }
}
