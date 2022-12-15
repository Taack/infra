package taack.jdbc.common.tql.equation;

public interface EquationNode {
    enum Kind {
        BOOLEAN,
        BIG_DECIMAL,
        STRING
    }

    Kind getKind();
    Object getValue() throws Exception;
    void addSiblingChildren(EquationNode equationNode);
    void addTerminal(String terminal);
}
