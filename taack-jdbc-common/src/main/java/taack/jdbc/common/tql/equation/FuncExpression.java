package taack.jdbc.common.tql.equation;


import java.math.BigDecimal;
import java.math.RoundingMode;

public class FuncExpression implements EquationNode {
    enum Sign {
        COS, TAN, SIN, ACOS, ATAN, ASIN, LOG, LN, SQRT, IF, ISBLANK, CONCATENATE,
        SUBSTITUTE, NUMBERVALUE, LEFT, SEARCH, AVERAGE, ROUND, LEN
    }

    Sign sign;

    EquationNode arg1 = null;
    EquationNode arg2 = null;
    EquationNode arg3 = null;

    @Override
    public Kind getKind() {
        switch (sign) {
            case IF:
                return arg2.getKind();
            case ISBLANK:
                return Kind.BOOLEAN;
            case SEARCH:
            case CONCATENATE:
            case SUBSTITUTE:
            case LEFT:
                return Kind.STRING;
            case AVERAGE:
            case NUMBERVALUE:
            case LN:
            case COS:
            case LEN:
            case LOG:
            case SIN:
            case TAN:
            case ACOS:
            case ASIN:
            case ATAN:
            case SQRT:
            case ROUND:
                return Kind.BIG_DECIMAL;
        };
        return null;
    }

    @Override
    public Object getValue() throws Exception {
        switch (sign) {
            case IF:
                if (arg1.getKind() != Kind.BOOLEAN)
                    throw new Exception("Boolean is required as first IF arg");
                if ((Boolean) arg1.getValue()) {
                    return arg2.getValue();
                } else {
                    return arg3.getValue();
                }
            case ISBLANK:
                return arg1.getValue() == null;
            case SEARCH:
                if (arg1.getKind() != Kind.STRING ||
                        arg2.getKind() != Kind.STRING ||
                        (arg3 == null || arg3.getKind() != Kind.BIG_DECIMAL)
                ) {
                    throw new Exception("SEARCH args of wrong data types");
                }
                String toFind = arg1.getValue().toString();
                String text = arg2.getValue().toString();
                Integer pos = arg3 != null ? ((BigDecimal) arg3.getValue()).intValue() : null;
                return pos != null ? text.indexOf(toFind, pos) : text.indexOf(toFind);
            case CONCATENATE:
                String ret = arg1.getValue().toString();
                if (arg2 != null) ret += arg2.getValue().toString();
                if (arg3 != null) ret += arg3.getValue().toString();
                return ret;
            case SUBSTITUTE:
                if (arg1.getKind() != Kind.STRING ||
                        arg2.getKind() != Kind.STRING ||
                        arg3.getKind() != Kind.STRING
                ) {
                    throw new Exception("SUBSTITUTE args of wrong data types");
                }
                toFind = arg1.getValue().toString();
                text = arg2.getValue().toString();
                String replaceTo = arg3.getValue().toString();
                return text.replaceAll(toFind, replaceTo);
            case LEFT:
                if (arg1.getKind() != Kind.STRING || arg2.getKind() != Kind.BIG_DECIMAL)
                    throw new IllegalArgumentException("LEFT has Wrong arguments");
                return arg1.getValue().toString().substring(0, ((BigDecimal) arg2.getValue()).intValue() - 1);
            case LEN:
                if (arg1.getKind() != Kind.STRING)
                    throw new IllegalArgumentException("LEN has Wrong arguments");
                return new BigDecimal(arg1.getValue().toString().length());
            case NUMBERVALUE:
                if (arg1.getKind() != Kind.BIG_DECIMAL)
                    throw new IllegalArgumentException("LEFT has Wrong arguments");
                return new BigDecimal(arg1.getValue().toString());
            case ROUND:
                if (arg1.getKind() != Kind.BIG_DECIMAL || arg2.getKind() != Kind.BIG_DECIMAL)
                    throw new IllegalArgumentException("LEFT has Wrong arguments");
//                MathContext m = new MathContext(((BigDecimal) arg2.getValue()).intValue());
//                return ((BigDecimal) arg1.getValue()).round(m);
                return ((BigDecimal) arg1.getValue()).setScale(((BigDecimal) arg2.getValue()).intValue(), RoundingMode.HALF_DOWN);
            case AVERAGE:
                throw new Exception("AVERAGE not implemented yet");
            case LN:
                if (arg1.getKind() != Kind.BIG_DECIMAL)
                    throw new IllegalArgumentException("LN has Wrong arguments type");
                return BigDecimal.valueOf(Math.log(((BigDecimal) arg1.getValue()).doubleValue()));
            case COS:
                if (arg1.getKind() != Kind.BIG_DECIMAL)
                    throw new IllegalArgumentException("COS has Wrong arguments type");
                return BigDecimal.valueOf(Math.cos(((BigDecimal) arg1.getValue()).doubleValue()));
            case LOG:
                if (arg1.getKind() != Kind.BIG_DECIMAL)
                    throw new IllegalArgumentException("LOG has Wrong arguments type");
                return BigDecimal.valueOf(Math.log10(((BigDecimal) arg1.getValue()).doubleValue()));
            case SIN:
                if (arg1.getKind() != Kind.BIG_DECIMAL)
                    throw new IllegalArgumentException("SIN has Wrong arguments type");
                return BigDecimal.valueOf(Math.sin(((BigDecimal) arg1.getValue()).doubleValue()));
            case TAN:
                if (arg1.getKind() != Kind.BIG_DECIMAL)
                    throw new IllegalArgumentException("TAN has Wrong arguments type");
                return BigDecimal.valueOf(Math.tan(((BigDecimal) arg1.getValue()).doubleValue()));
            case ACOS:
                if (arg1.getKind() != Kind.BIG_DECIMAL)
                    throw new IllegalArgumentException("ACOS has Wrong arguments type");
                return BigDecimal.valueOf(Math.acos(((BigDecimal) arg1.getValue()).doubleValue()));
            case ASIN:
                if (arg1.getKind() != Kind.BIG_DECIMAL)
                    throw new IllegalArgumentException("ASIN has Wrong arguments type");
                return BigDecimal.valueOf(Math.asin(((BigDecimal) arg1.getValue()).doubleValue()));
            case ATAN:
                if (arg1.getKind() != Kind.BIG_DECIMAL)
                    throw new IllegalArgumentException("ATAN has Wrong arguments type");
                return BigDecimal.valueOf(Math.atan(((BigDecimal) arg1.getValue()).doubleValue()));
            case SQRT:
                if (arg1.getKind() != Kind.BIG_DECIMAL)
                    throw new IllegalArgumentException("SQRT has Wrong arguments type");
                return BigDecimal.valueOf(Math.sqrt(((BigDecimal) arg1.getValue()).doubleValue()));
        }
        return null;
    }

    @Override
    public void addSiblingChildren(EquationNode equationNode) {
        if (arg1 == null) arg1 = equationNode;
        else if (arg2 == null) arg2 = equationNode;
        else arg3 = equationNode;
    }

    @Override
    public void addTerminal(String terminal) {
        switch (terminal) {
            case "cos": {
                sign = Sign.COS;
            }
            case "sin": {
                sign = Sign.SIN;
            }
            case "tan": {
                sign = Sign.TAN;
            }
            case "acos": {
                sign = Sign.ACOS;
            }
            case "asin": {
                sign = Sign.ASIN;
            }
            case "atan": {
                sign = Sign.ATAN;
            }
            case "ln": {
                sign = Sign.LN;
            }
            case "log": {
                sign = Sign.LOG;
            }
            case "sqrt": {
                sign = Sign.SQRT;
            }
            case "if": {
                sign = Sign.IF;
            }
            case "isBlank": {
                sign = Sign.ISBLANK;
            }
            case "concatenate": {
                sign = Sign.CONCATENATE;
            }
            case "substitute": {
                sign = Sign.SUBSTITUTE;
            }
            case "numberValue": {
                sign = Sign.NUMBERVALUE;
            }
            case "left": {
                sign = Sign.LEFT;
            }
            case "search": {
                sign = Sign.SEARCH;
            }
            case "average": {
                sign = Sign.AVERAGE;
            }
            case "round": {
                sign = Sign.ROUND;
            }
            case "len": sign = Sign.LEN;
        }
    }
}
