package taack.jdbc.client;

import java.math.BigDecimal;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;

public class TaackResultSetMetaData implements ResultSetMetaData {
//    final static Logger logger = LoggerFactory.getLogger(TaackResultSetMetaData.class);

    final TaackResultSetJdbc resultSetJdbc;

    TaackResultSetMetaData(TaackResultSetJdbc resultSetJdbc) {
        logInfo("new TaackResultSetMetaData");
        this.resultSetJdbc = resultSetJdbc;
    }

    private void logInfo(String message) {
        System.out.println("TaackDatabaseMetaData::" + message);
    }

    @Override
    public int getColumnCount() throws SQLException {
        //logInfo("getColumnCount");
        return resultSetJdbc.numberOfColumn;
    }

    @Override
    public boolean isAutoIncrement(int column) throws SQLException {
        logInfo("isAutoIncrement " + column);
        return false;
    }

    @Override
    public boolean isCaseSensitive(int column) throws SQLException {
        logInfo("isCaseSensitive " + column);
        return true;
    }

    @Override
    public boolean isSearchable(int column) throws SQLException {
        logInfo("isSearchable " + column);
        return true;
    }

    @Override
    public boolean isCurrency(int column) throws SQLException {
        logInfo("isCurrency " + column);
        return false;
    }

    @Override
    public int isNullable(int column) throws SQLException {
        logInfo("isNullable " + column);
        var c = resultSetJdbc.taackResultSetProto.getColumns(column - 1);
        logInfo("c.getIsNullable: " + c.getIsNullable());
        return c.getIsNullable() ? columnNullable : columnNoNulls;
    }

    @Override
    public boolean isSigned(int column) throws SQLException {
        logInfo("isSigned " + column);
        return false;
    }

    @Override
    public int getColumnDisplaySize(int column) throws SQLException {
        logInfo("getColumnDisplaySize " + column);
        var c = resultSetJdbc.taackResultSetProto.getColumns(column - 1);
        logInfo("c.getDisplaySize: " + c.getDisplaySize());
        return c.getDisplaySize();
    }

    @Override
    public String getColumnLabel(int column) throws SQLException {
        logInfo("getColumnLabel " + column);
        var c = resultSetJdbc.taackResultSetProto.getColumns(column - 1);
        logInfo("c.getName (getColumnLabel): " + c.getName());
        return c.getName();
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        logInfo("getColumnName " + column);
        var c = resultSetJdbc.taackResultSetProto.getColumns(column - 1);
        logInfo("c.getName (getColumnLabel): " + c.getName());
        return c.getName();
    }

    @Override
    public String getSchemaName(int column) throws SQLException {
        logInfo("getSchemaName " + column);
        return "public";
    }

    @Override
    public int getPrecision(int column) throws SQLException {
        logInfo("getPrecision " + column);
        var c = resultSetJdbc.taackResultSetProto.getColumns(column - 1);
        int r = 0;
        switch (c.getJavaType()) {
            case DATE:
            case BIG_DECIMAL:
                r = 6;
                break;
            case LONG:
            case STRING:
            case BOOL:
            case BYTE:
            case SHORT:
            case INT:
            case BYTES:
            case UNRECOGNIZED:
                break;
        }
        logInfo("c.getPrecision: " + r);
        return r;
    }

    @Override
    public int getScale(int column) throws SQLException {
        logInfo("getScale " + column);
        return 0;
    }

    @Override
    public String getTableName(int column) throws SQLException {
        logInfo("getTableName " + column);
        var c = resultSetJdbc.taackResultSetProto.getColumns(column - 1);
        logInfo("c.getTableName: " + c.getTableName());
        return c.getTableName();
    }

    @Override
    public String getCatalogName(int column) throws SQLException {
        logInfo("getCatalogName " + column);
        return "default";
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        //logInfo("getColumnType " + column);
        var c = resultSetJdbc.taackResultSetProto.getColumns(column - 1);
        int r = 0;
        switch (c.getJavaType()) {
            case DATE:
                r = 93;
                break;
            case LONG:
                r = -5;
                break;
            case BIG_DECIMAL:
                r = 2;
                break;
            case STRING:
                r = 12;
                break;
            case BOOL:
                r = -7;
                break;
            case BYTE:
            case SHORT:
            case INT:
                r = 4;
                break;
            case BYTES:
            case UNRECOGNIZED:
                break;
        }
        //logInfo("c.getSqlType: " + r);
        return r;
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        //logInfo("getColumnTypeName " + column);
        var c = resultSetJdbc.taackResultSetProto.getColumns(column - 1);
        String r = "";
        switch (c.getJavaType()) {
            case DATE:
                r = "timestamp";
                break;
            case LONG:
                r = "int8";
                break;
            case BIG_DECIMAL:
                r = "numeric";
                break;
            case STRING:
                r = "varchar";
                break;
            case BOOL:
                r = "bool";
                break;
            case BYTE:
            case SHORT:
            case INT:
                r = "int4";
                break;
            case BYTES:
            case UNRECOGNIZED:
                break;
        }
        //logInfo("c.getSqlTypeName: " + r);
        return r;
    }

    @Override
    public boolean isReadOnly(int column) throws SQLException {
        logInfo("isReadOnly " + column);
        return true;
    }

    @Override
    public boolean isWritable(int column) throws SQLException {
        logInfo("isWritable " + column);
        return false;
    }

    @Override
    public boolean isDefinitelyWritable(int column) throws SQLException {
        logInfo("isDefinitelyWritable " + column);
        return false;
    }

    @Override
    public String getColumnClassName(int column) throws SQLException {
        //logInfo("getColumnClassName " + column);
        var c = resultSetJdbc.taackResultSetProto.getColumns(column - 1);
        String r = "";
        switch (c.getJavaType()) {
            case DATE:
                r = Date.class.getName();
                break;
            case LONG:
                r = Long.class.getName();
                break;
            case BIG_DECIMAL:
                r = BigDecimal.class.getName();
                break;
            case STRING:
                r = String.class.getName();
                break;
            case BOOL:
                r = Boolean.class.getName();
                break;
            case BYTE:
            case SHORT:
            case INT:
                r = Integer.class.getName();
                break;
            case BYTES:
            case UNRECOGNIZED:
                break;
        }

        //logInfo("c.getJavaTypeName: " + r);
        return r;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        logInfo("unwrap " + iface);
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        logInfo("isWrapperFor " + iface);
        return false;
    }
}
