package taack.jdbc.client;

import com.google.protobuf.InvalidProtocolBufferException;
import taack.jdbc.common.TaackResultSetOuterClass;

import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.Arrays;
import java.util.Map;

public final class TaackStatement implements Statement {

//    private static final Logger logger = LoggerFactory.getLogger(TaackStatement.class);
    private boolean closed = false;
    final TaackConnection connection;
    int maxFieldSize = 4096;
    int timeout = 0;
    Integer offsetInRow = 0;
    TaackResultSetJdbc taackResultSetJdbc;
    String req;

    Integer maxRow = 0;
    int direction = 0;
    Integer fetchSize = 100;
    Integer countRowsToUpdate = 0;

    TaackStatement(TaackConnection connection) {
        logInfo("new TaackStatement " + connection);
        this.connection = connection;
    }

    private void logInfo(String message) {
        System.out.println("TaackStatement:INFO :" + message);
    }

    private void logError(String message) {
        System.out.println("TaackStatement:ERROR:" + message);
    }


    private TaackResultSetOuterClass.TaackResultSet getProto() throws SQLException {
        logInfo("getProto " + req);
        byte[] b = new byte[0];
        try {
//            int offsetReq = offsetInRow < 0 ? 0 : offsetInRow;
//            String max = maxRow == 0 ? "mr" : "maxRow";
            String max = "maxRow";
            b = connection.executeReq(Map.of(
                    "req", req,
                    "offset", Integer.toString(offsetInRow),
                    max, maxRow.toString()
            ));
            logInfo("b.length: " + b.length);
        } catch (UnsupportedEncodingException e) {
            logError("AUO 222 UnsupportedEncodingException");
            throw new RuntimeException(e);
        }
        if (b.length == 0) {
            throw new SQLSyntaxErrorException("SQL Req: " + req);
        }
        try {
            var rs =  TaackResultSetOuterClass.TaackResultSet.parseFrom(b);
            if (rs.hasProcessingError()) {
                throw new SQLException("STEP: " + rs.getProcessingError().getProcessingStep() + " MSG: " + rs.getProcessingError().getErrorMessage());
            }
            countRowsToUpdate = rs.getCellsCount() / rs.getColumnsCount();
            return rs;
        } catch (InvalidProtocolBufferException e) {
            logError("AUO 111 InvalidProtocolBufferException");
            throw new RuntimeException(e);
        }
    }

    private TaackResultSetJdbc convertToResultSet(TaackResultSetOuterClass.TaackResultSet taackResultSet) {
        logInfo("convertToResultSet .................................");
        taackResultSetJdbc = new TaackResultSetJdbc(taackResultSet);
        taackResultSetJdbc.fetchSizeInRow = maxRow;
        return taackResultSetJdbc;
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        logInfo("executeQuery " + sql);
        req = sql;
        if (taackResultSetJdbc != null) {
            offsetInRow = taackResultSetJdbc.offsetInRowInProto + taackResultSetJdbc.currentCellInProto / taackResultSetJdbc.numberOfColumn;
        }
        var p = getProto();
        taackResultSetJdbc = convertToResultSet(p);
        return taackResultSetJdbc;
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        logInfo("executeUpdate " + sql);
        return 0;
    }

    @Override
    public void close() throws SQLException {
        logInfo("close ");
        taackResultSetJdbc = null;
        offsetInRow = 0;
        closed = true;
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        logInfo("getMaxFieldSize");
        return maxFieldSize;
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        logInfo("setMaxFieldSize " + max);
        maxFieldSize = max;
    }

    @Override
    public int getMaxRows() throws SQLException {
        logInfo("getMaxRows");
        return maxRow;
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        logInfo("setMaxRows " + max);
        maxRow = max;
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        logInfo("setEscapeProcessing " + enable);
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        logInfo("getQueryTimeout ");
        return timeout;
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        logInfo("setQueryTimeout " + seconds);
        timeout = seconds;
    }

    @Override
    public void cancel() throws SQLException {
        logInfo("cancel ");

    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        logInfo("getWarnings ");

        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {
        logInfo("clearWarnings ");

    }

    @Override
    public void setCursorName(String name) throws SQLException {
        logInfo("setCursorName " + name);
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        logInfo("execute " + sql);
        this.req = sql;
        executeQuery(sql);
        return true;
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        logInfo("getResultSet ");
        if (taackResultSetJdbc.isClosed)
            taackResultSetJdbc = convertToResultSet(getProto());

        return taackResultSetJdbc;
    }

    @Override
    public int getUpdateCount() throws SQLException {
        /*if (!updateCountCalled && taackResultSetJdbc != null && taackResultSetJdbc.currentCellInProto < taackResultSetJdbc.numberOfCellsInProto) {
            updateCountCalled = true;
            if (taackResultSetJdbc.currentCellInProto == -1) return -1;
            if (taackResultSetJdbc.numberOfColumn == 0) return 0;
            int currentResult = taackResultSetJdbc.numberOfCellsInProto / taackResultSetJdbc.numberOfColumn;
            logInfo("getUpdateCount ret: " + currentResult);

            return currentResult;
            //return 0;
        }*/
        var ret = countRowsToUpdate;
        countRowsToUpdate = -1;
        logInfo("getUpdateCount ret: " + ret);
        return -1;
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        /*if (taackResultSetJdbc == null) {
            logInfo("getMoreResults call getProto ...");
            taackResultSetJdbc = convertToResultSet(getProto());
        }
        if (taackResultSetJdbc.numberOfColumn == 0) return false;
        offsetInRow = taackResultSetJdbc.absoluteLineNumber;
        logInfo("getMoreResults offsetInRow: " + offsetInRow + " " + taackResultSetJdbc.offsetInRowInProto + " " + taackResultSetJdbc.currentCellInProto);
        if (offsetInRow < taackResultSetJdbc.numberOfRowsFromQuery) {
            taackResultSetJdbc.close();

//            taackResultSetJdbc = convertToResultSet(getProto());
            return true;
//            return false;
        }
         */
        logInfo("getMoreResults returns false ...");
        return false;
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        logInfo("setFetchDirection " + direction);

        this.direction = direction;
    }

    @Override
    public int getFetchDirection() throws SQLException {
        logInfo("getFetchDirection ");

        return ResultSet.FETCH_FORWARD;
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        logInfo("setFetchSize " + rows);

        fetchSize = rows;
    }

    @Override
    public int getFetchSize() throws SQLException {
        logInfo("getFetchSize ");

        return fetchSize;
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        logInfo("getResultSetConcurrency ");
        return ResultSet.CONCUR_READ_ONLY;
    }

    @Override
    public int getResultSetType() throws SQLException {
        logInfo("getResultSetType ");
        return ResultSet.TYPE_SCROLL_SENSITIVE;
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        logInfo("addBatch " + sql);

    }

    @Override
    public void clearBatch() throws SQLException {
        logInfo("clearBatch ");

    }

    @Override
    public int[] executeBatch() throws SQLException {
        logInfo("executeBatch ");
        return new int[0];
    }

    @Override
    public Connection getConnection() throws SQLException {
        logInfo("getConnection ");
        return connection;
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        logInfo("getMoreResults +++ " + current);
        return getMoreResults();
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        logInfo("getGeneratedKeys ");
        return null;
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        logInfo("executeUpdate " + sql + " " + autoGeneratedKeys);
        return 0;
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        logInfo("executeUpdate " + sql + " " + Arrays.toString(columnIndexes));
        return 0;
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        logInfo("executeUpdate " + sql + " " + Arrays.toString(columnNames));
        return 0;
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        logInfo("execute " + sql + " " + autoGeneratedKeys);
        return false;
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        logInfo("execute " + sql + " " + Arrays.toString(columnIndexes));
        return false;
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        logInfo("execute " + sql + " " + Arrays.toString(columnNames));
        return false;
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        logInfo("getResultSetHoldability ");
        return 0;
    }

    @Override
    public boolean isClosed() throws SQLException {
        logInfo("isClosed ");
        return closed;
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        logInfo("setPoolable " + poolable);

    }

    @Override
    public boolean isPoolable() throws SQLException {
        logInfo("isPoolable ");
        return false;
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        logInfo("closeOnCompletion ");
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        logInfo("isCloseOnCompletion ");
        return false;
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
