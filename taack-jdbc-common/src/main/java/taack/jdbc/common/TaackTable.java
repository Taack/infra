package taack.jdbc.common;

public enum TaackTable {
    META_TABLE("meta_description");

    TaackTable(String tableName) {
        this.tableName = tableName;
    }

    final String tableName;
}
