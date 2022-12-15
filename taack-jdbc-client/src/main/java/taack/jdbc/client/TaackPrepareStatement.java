package taack.jdbc.client;


import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;

public class TaackPrepareStatement implements PreparedStatement {

//    final static Logger logger = LoggerFactory.getLogger(TaackPrepareStatement.class);
    final TaackConnection connection;
    final String sql;

    TaackPrepareStatement(String sql, TaackConnection connection) {
        logInfo("new TaackPrepareStatement " + sql);
        this.sql = sql;
        this.connection = connection;
    }
    
    private void logInfo(String message) {
        System.out.println("TaackResultSetJdbc:INFO :" + message);
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        logInfo("executeQuery " + sql);
        logInfo("AUO connection " + connection);
        logInfo("AUO connection.statement " + connection.statement);
        if (connection.statement == null) connection.statement = new TaackStatement(connection);
        connection.statement.taackResultSetJdbc = null;
        return connection.statement.executeQuery(sql);
    }

    @Override
    public int executeUpdate() throws SQLException {
        logInfo("executeUpdate");
        return 0;
    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        logInfo("setNull");

    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        logInfo("setBoolean");

    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        logInfo("setByte");

    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        logInfo("setShort");

    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        logInfo("setInt");

    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        logInfo("setLong");

    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        logInfo("setFloat");

    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        logInfo("setDouble");

    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        logInfo("setBigDecimal");

    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        logInfo("setString");

    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        logInfo("setBytes");

    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        logInfo("setDate");

    }

    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        logInfo("setTime");

    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        logInfo("setTimestamp");

    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        logInfo("setAsciiStream");

    }

    @Override
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        logInfo("setUnicodeStream");

    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        logInfo("setBinaryStream");

    }

    @Override
    public void clearParameters() throws SQLException {
        logInfo("clearParameters");

    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        logInfo("setObject");

    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {
        logInfo("setObject");

    }

    @Override
    public boolean execute() throws SQLException {
        logInfo("execute");

        return false;
    }

    @Override
    public void addBatch() throws SQLException {
        logInfo("addBatch");

    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        logInfo("setCharacterStream");

    }

    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException {
        logInfo("setRef");

    }

    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        logInfo("setBlob");

    }

    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        logInfo("setClob");

    }

    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException {
        logInfo("setArray");

    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        logInfo("getMetaData");
        return connection.statement.taackResultSetJdbc.getMetaData();
    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        logInfo("setDate");

    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        logInfo("setTime");

    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        logInfo("setTimestamp");

    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        logInfo("setNull");

    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {
        logInfo("setURL");

    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        logInfo("getParameterMetaData");
        return null;
    }

    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        logInfo("setRowId");

    }

    @Override
    public void setNString(int parameterIndex, String value) throws SQLException {

        logInfo("setNString");
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        logInfo("setNCharacterStream");

    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        logInfo("setNClob");

    }

    @Override
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        logInfo("setClob");

    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        logInfo("setBlob");

    }

    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        logInfo("setNClob");

    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        logInfo("setSQLXML");

    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        logInfo("setObject");

    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        logInfo("setAsciiStream");

    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        logInfo("setBinaryStream");

    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        logInfo("setCharacterStream");

    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {

        logInfo("setAsciiStream");
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        logInfo("setBinaryStream");

    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        logInfo("setCharacterStream");

    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        logInfo("setNCharacterStream");

    }

    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        logInfo("setClob");

    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        logInfo("setBlob");

    }

    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        logInfo("setNClob");

    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        logInfo("executeQuery(sql) " + sql);
        if (connection.statement == null) {
            connection.statement = new TaackStatement(connection);
        }
        return connection.statement.executeQuery(sql);
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        logInfo("executeUpdate");
        return 0;
    }

    @Override
    public void close() throws SQLException {
        logInfo("close");

    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        logInfo("getMaxFieldSize");
        return 0;
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        logInfo("setMaxFieldSize");

    }

    @Override
    public int getMaxRows() throws SQLException {
        logInfo("getMaxRows");
        return 0;
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        logInfo("setMaxRows");

    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        logInfo("setEscapeProcessing");

    }

    @Override
    public int getQueryTimeout() throws SQLException {
        logInfo("getQueryTimeout");
        return 0;
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        logInfo("setQueryTimeout");

    }

    @Override
    public void cancel() throws SQLException {
        logInfo("cancel");

    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        logInfo("getWarnings");
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {
        logInfo("clearWarnings");

    }

    @Override
    public void setCursorName(String name) throws SQLException {
        logInfo("setCursorName");

    }

    @Override
    public boolean execute(String sql) throws SQLException {
        logInfo("execute");
        return false;
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        logInfo("getResultSet");
        return null;
    }

    @Override
    public int getUpdateCount() throws SQLException {
        logInfo("getUpdateCount");
        return 0;
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        logInfo("getMoreResults");
        return false;
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        logInfo("setFetchDirection");
    }

    @Override
    public int getFetchDirection() throws SQLException {
        logInfo("getFetchDirection");
        return 0;
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        logInfo("setFetchSize");

    }

    @Override
    public int getFetchSize() throws SQLException {
        logInfo("getFetchSize");
        return 0;
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        logInfo("getResultSetConcurrency");
        return 0;
    }

    @Override
    public int getResultSetType() throws SQLException {
        logInfo("getResultSetType");
        return 0;
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        logInfo("addBatch");

    }

    @Override
    public void clearBatch() throws SQLException {
        logInfo("clearBatch");

    }

    @Override
    public int[] executeBatch() throws SQLException {
        logInfo("executeBatch");
        return new int[0];
    }

    @Override
    public Connection getConnection() throws SQLException {
        logInfo("getConnection");
        return null;
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        logInfo("getMoreResults");
        return false;
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        logInfo("getGeneratedKeys");
        return null;
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        logInfo("executeUpdate");
        return 0;
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        logInfo("executeUpdate");
        return 0;
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        logInfo("executeUpdate");
        return 0;
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        logInfo("execute");
        return false;
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        logInfo("execute");
        return false;
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        logInfo("execute");
        return false;
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        logInfo("getResultSetHoldability");
        return 0;
    }

    @Override
    public boolean isClosed() throws SQLException {
        logInfo("isClosed");
        return false;
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        logInfo("setPoolable");

    }

    @Override
    public boolean isPoolable() throws SQLException {
        logInfo("isPoolable");
        return false;
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        logInfo("closeOnCompletion");

    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        logInfo("isCloseOnCompletion");
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        logInfo("unwrap");
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        logInfo("isWrapperFor");
        return false;
    }
}
