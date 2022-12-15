package taack.jdbc.client;

import taack.jdbc.common.TaackResultSetOuterClass;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public final class TaackResultSetJdbc implements ResultSet {
//    final static Logger logger = LoggerFactory.getLogger(TaackResultSetJdbc.class);

    final TaackResultSetOuterClass.TaackResultSet taackResultSetProto;
    final int numberOfColumn;
    final int numberOfCellsInProto;
    final Map<String, Integer> columnNames;
    int offset = 0;
    int fetchSizeInRow = 0;
    int direction = 1;
    final int offsetInRowInProto;
    int currentCellInProto = 0;
    int absoluteLineNumber = 0;
    final long numberOfRowsFromQuery;
    final long numberOfCellsFromQuery;
    boolean isClosed = false;

    private boolean itWasNull = false;

    TaackResultSetJdbc(TaackResultSetOuterClass.TaackResultSet taackResultSetProto) {
        this.taackResultSetProto = taackResultSetProto;
        numberOfColumn = taackResultSetProto.getColumnsCount();
        offsetInRowInProto = (int) taackResultSetProto.getOffset();
        numberOfCellsInProto = taackResultSetProto.getCellsCount();
        numberOfRowsFromQuery = taackResultSetProto.getCounter();
        numberOfCellsFromQuery = numberOfRowsFromQuery * numberOfColumn;
        currentCellInProto = -numberOfColumn;
        absoluteLineNumber = offsetInRowInProto;
        columnNames = new HashMap<>();
        int colIndex = 0;
        for (var c : taackResultSetProto.getColumnsList()) {
            columnNames.put(c.getName(), colIndex++);
        }
        logInfo("new TaackResultSetJdbc numberOfCellsInProto: " + numberOfCellsInProto
                + " numberOfColumn: " + numberOfColumn
                + " numberOfRowsFromQuery: " + numberOfRowsFromQuery
                + " numberOfCellsFromQuery: " + numberOfCellsFromQuery
        );
    }

    private void logInfo(String message) {
        System.out.println("TaackResultSetJdbc:INFO :" + message);
    }

    private void logError(String message) {
        System.out.println("TaackResultSetJdbc:ERROR:" + message);
    }

    @Override
    public boolean next() throws SQLException {
        logInfo("next offsetInRowInProto: " + offsetInRowInProto +
                " currentCellInProto: " + currentCellInProto +
                " numberOfRowsFromQuery: " + numberOfRowsFromQuery +
                " numberOfCellsInProto: " + numberOfCellsInProto +
                " numberOfColumn: " + numberOfColumn +
                " " + (currentCellInProto < numberOfCellsInProto));
//        if (currentCellInProto < fetchSizeInRow * numberOfColumn && offsetInRowInProto + currentCellInProto + numberOfColumn < numberOfRowsFromQuery  * numberOfColumn) {
//            currentCellInProto += numberOfColumn;
//            return true;
//        } else return false;
        if (numberOfColumn == 0) return false;
        currentCellInProto = (absoluteLineNumber - offsetInRowInProto) * numberOfColumn;
        int currentRow = currentCellInProto / numberOfColumn;
        currentCellInProto = currentRow * numberOfColumn;
        absoluteLineNumber++;
        logInfo("currentRow: " + currentRow + " currentCellInProto: " + currentCellInProto + " absoluteLineNumber: " + absoluteLineNumber);
        return currentCellInProto < numberOfCellsInProto && currentCellInProto >= -1;
    }

    @Override
    public void close() throws SQLException {
        logInfo("close");
        isClosed = true;
    }

    @Override
    public boolean wasNull() throws SQLException {
        logInfo("wasNull " + itWasNull);
        boolean r = itWasNull;
        itWasNull = false;
        return r;
    }

    @Override
    public String getString(int columnIndex) throws SQLException {
        logInfo("getString " + columnIndex + " currentCellInProto: " + currentCellInProto);
        if (columnIndex > 0) columnIndex -= 1;
        if (taackResultSetProto.getColumns(columnIndex).getJavaType() == TaackResultSetOuterClass.Column.JavaType.STRING) {
            var v = taackResultSetProto.getCells(currentCellInProto + columnIndex).getStringValue();
            var b = taackResultSetProto.getCells(currentCellInProto + columnIndex).hasStringValue();
            if (!b) {
                itWasNull = true;
                return null;
            }
            return v;
        }
        // LibreOffice bug
        if (taackResultSetProto.getColumns(columnIndex).getJavaType() == TaackResultSetOuterClass.Column.JavaType.LONG) {
            var v = taackResultSetProto.getCells(currentCellInProto + columnIndex).getLongValue();
            var b = taackResultSetProto.getCells(currentCellInProto + columnIndex).hasLongValue();
            if (!b) {
                itWasNull = true;
                return null;
            }
            return ((Long) v).toString();
        }
        // LibreOffice bug
        if (taackResultSetProto.getColumns(columnIndex).getJavaType() == TaackResultSetOuterClass.Column.JavaType.BIG_DECIMAL) {
            var v = taackResultSetProto.getCells(currentCellInProto + columnIndex).getBigDecimal();
            var b = taackResultSetProto.getCells(currentCellInProto + columnIndex).hasBigDecimal();
            if (!b) {
                itWasNull = true;
                return null;
            }
            return v;
        }
        logError("THROW EXCEPTION ... " + taackResultSetProto.getColumns(columnIndex));
        throw new SQLException("Column #" + columnIndex + " is not a String, it is a " + taackResultSetProto.getColumns(columnIndex).getJavaType());
    }

    @Override
    public boolean getBoolean(int columnIndex) throws SQLException {
        logInfo("getBoolean " + columnIndex);
        columnIndex -= 1;
        if (taackResultSetProto.getColumns(columnIndex).getJavaType() == TaackResultSetOuterClass.Column.JavaType.BOOL)
            return taackResultSetProto.getCells(currentCellInProto + columnIndex).getBoolValue();
        throw new SQLException("Column #" + columnIndex + " is not a Bool, it is a " + taackResultSetProto.getColumns(columnIndex).getJavaType());
    }

    @Override
    public byte getByte(int columnIndex) throws SQLException {
        logInfo("getByte " + columnIndex);
        columnIndex -= 1;
        if (taackResultSetProto.getColumns(columnIndex).getJavaType() == TaackResultSetOuterClass.Column.JavaType.BYTE)
            return ((byte) taackResultSetProto.getCells(currentCellInProto + columnIndex).getByteValue());
        throw new SQLException("Column #" + columnIndex + " is not a Byte, it is a " + taackResultSetProto.getColumns(columnIndex).getJavaType());
    }

    @Override
    public short getShort(int columnIndex) throws SQLException {
        logInfo("getShort " + columnIndex);
        columnIndex -= 1;
        if (taackResultSetProto.getColumns(columnIndex).getJavaType() == TaackResultSetOuterClass.Column.JavaType.SHORT)
            return ((short) taackResultSetProto.getCells(currentCellInProto + columnIndex).getShortValue());
        throw new SQLException("Column #" + columnIndex + " is not a Short, it is a " + taackResultSetProto.getColumns(columnIndex).getJavaType());
    }

    @Override
    public int getInt(int columnIndex) throws SQLException {
        logInfo("getInt " + columnIndex);
        columnIndex -= 1;
        if (taackResultSetProto.getColumns(columnIndex).getJavaType() == TaackResultSetOuterClass.Column.JavaType.INT)
            return taackResultSetProto.getCells(currentCellInProto + columnIndex).getIntValue();
        throw new SQLException("Column #" + columnIndex + " is not a Int, it is a " + taackResultSetProto.getColumns(columnIndex).getJavaType());
    }

    @Override
    public long getLong(int columnIndex) throws SQLException {
        logInfo("getLong " + columnIndex);
        columnIndex -= 1;
        if (taackResultSetProto.getColumns(columnIndex).getJavaType() == TaackResultSetOuterClass.Column.JavaType.LONG)
            return taackResultSetProto.getCells(currentCellInProto + columnIndex).getLongValue();
        throw new SQLException("Column #" + columnIndex + " is not a Long, it is a " + taackResultSetProto.getColumns(columnIndex).getJavaType());
    }

    @Override
    public float getFloat(int columnIndex) throws SQLException {
        logInfo("getFloat " + columnIndex);
        columnIndex -= 1;
        if (taackResultSetProto.getColumns(columnIndex).getJavaType() == TaackResultSetOuterClass.Column.JavaType.BIG_DECIMAL)
            return Float.parseFloat(taackResultSetProto.getCells(currentCellInProto + columnIndex).getBigDecimal());
        throw new SQLException("Column #" + columnIndex + " is not a Float, it is a " + taackResultSetProto.getColumns(columnIndex).getJavaType());
    }

    @Override
    public double getDouble(int columnIndex) throws SQLException {
        logInfo("getDouble " + columnIndex);
        columnIndex -= 1;
        if (taackResultSetProto.getColumns(columnIndex).getJavaType() == TaackResultSetOuterClass.Column.JavaType.BIG_DECIMAL)
            return Double.parseDouble(taackResultSetProto.getCells(currentCellInProto + columnIndex).getBigDecimal());
        throw new SQLException("Column #" + columnIndex + " is not a Double, it is a " + taackResultSetProto.getColumns(columnIndex).getJavaType());
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        logInfo("getBigDecimal " + columnIndex);
        columnIndex -= 1;
        if (taackResultSetProto.getColumns(columnIndex).getJavaType() == TaackResultSetOuterClass.Column.JavaType.BIG_DECIMAL)
            return new BigDecimal(taackResultSetProto.getCells(currentCellInProto + columnIndex).getBigDecimal()).setScale(scale);
        throw new SQLException("Column #" + columnIndex + " is not a BigDecimal, it is a " + taackResultSetProto.getColumns(columnIndex).getJavaType());
    }

    @Override
    public byte[] getBytes(int columnIndex) throws SQLException {
        logInfo("getBytes " + columnIndex);
        columnIndex -= 1;
        if (taackResultSetProto.getColumns(columnIndex).getJavaType() == TaackResultSetOuterClass.Column.JavaType.BYTES)
            return taackResultSetProto.getCells(currentCellInProto + columnIndex).getBytesValue().toByteArray();
        throw new SQLException("Column #" + columnIndex + " is not a byte[], it is a " + taackResultSetProto.getColumns(columnIndex).getJavaType());
    }

    @Override
    public Date getDate(int columnIndex) throws SQLException {
        logInfo("getDate " + columnIndex);
        columnIndex -= 1;
        if (taackResultSetProto.getColumns(columnIndex).getJavaType() == TaackResultSetOuterClass.Column.JavaType.DATE)
            return new Date(taackResultSetProto.getCells(currentCellInProto + columnIndex).getDateValue());
        throw new SQLException("Column #" + columnIndex + " is not a Date, it is a " + taackResultSetProto.getColumns(columnIndex).getJavaType());
    }

    @Override
    public Time getTime(int columnIndex) throws SQLException {
        logInfo("getTime " + columnIndex);
        columnIndex -= 1;
        if (taackResultSetProto.getColumns(columnIndex).getJavaType() == TaackResultSetOuterClass.Column.JavaType.DATE)
            return new Time(taackResultSetProto.getCells(currentCellInProto + columnIndex).getDateValue());
        throw new SQLException("Column #" + columnIndex + " is not a Time, it is a " + taackResultSetProto.getColumns(columnIndex).getJavaType());
    }

    @Override
    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        logInfo("getTimestamp " + columnIndex);
        columnIndex -= 1;
        if (taackResultSetProto.getColumns(columnIndex).getJavaType() == TaackResultSetOuterClass.Column.JavaType.DATE)
            return new Timestamp(taackResultSetProto.getCells(currentCellInProto + columnIndex).getDateValue());
        throw new SQLException("Column #" + columnIndex + " is not a Timestamp, it is a " + taackResultSetProto.getColumns(columnIndex).getJavaType());
    }

    @Override
    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        logInfo("getAsciiStream " + columnIndex);
        columnIndex -= 1;
        return null;
    }

    @Override
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        logInfo("getUnicodeStream " + columnIndex);
        columnIndex -= 1;
        return null;
    }

    @Override
    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        logInfo("getBinaryStream " + columnIndex);
        columnIndex -= 1;
        return null;
    }

    @Override
    public String getString(String columnLabel) throws SQLException {
        logInfo("getString " + columnLabel);
        return getString(columnNames.get(columnLabel) + 1);
    }

    @Override
    public boolean getBoolean(String columnLabel) throws SQLException {
        logInfo("getBoolean " + columnLabel);
        return getBoolean(columnNames.get(columnLabel) + 1);
    }

    @Override
    public byte getByte(String columnLabel) throws SQLException {
        logInfo("getByte " + columnLabel);
        return getByte(columnNames.get(columnLabel) + 1);
    }

    @Override
    public short getShort(String columnLabel) throws SQLException {
        logInfo("getShort " + columnLabel);
        return getShort(columnNames.get(columnLabel) + 1);
    }

    @Override
    public int getInt(String columnLabel) throws SQLException {
        logInfo("getInt " + columnLabel);
        return getInt(columnNames.get(columnLabel) + 1);
    }

    @Override
    public long getLong(String columnLabel) throws SQLException {
        logInfo("getLong " + columnLabel);
        return getLong(columnNames.get(columnLabel) + 1);
    }

    @Override
    public float getFloat(String columnLabel) throws SQLException {
        logInfo("getFloat " + columnLabel);
        return getFloat(columnNames.get(columnLabel) + 1);
    }

    @Override
    public double getDouble(String columnLabel) throws SQLException {
        logInfo("getDouble " + columnLabel);
        return getDouble(columnNames.get(columnLabel) + 1);
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        logInfo("getBigDecimal " + columnLabel + " s: " + scale);
        return getBigDecimal(columnNames.get(columnLabel) + 1, scale);
    }

    @Override
    public byte[] getBytes(String columnLabel) throws SQLException {
        logInfo("getBytes " + columnLabel);
        return getBytes(columnNames.get(columnLabel) + 1);
    }

    @Override
    public Date getDate(String columnLabel) throws SQLException {
        logInfo("getDate " + columnLabel);
        return getDate(columnNames.get(columnLabel) + 1);
    }

    @Override
    public Time getTime(String columnLabel) throws SQLException {
        logInfo("getTime " + columnLabel);
        return getTime(columnNames.get(columnLabel) + 1);
    }

    @Override
    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        logInfo("getTimestamp " + columnLabel);
        return getTimestamp(columnNames.get(columnLabel) + 1);
    }

    @Override
    public InputStream getAsciiStream(String columnLabel) throws SQLException {
        logInfo("getAsciiStream " + columnLabel);
        return null;
    }

    @Override
    public InputStream getUnicodeStream(String columnLabel) throws SQLException {
        logInfo("getUnicodeStream " + columnLabel);
        return null;
    }

    @Override
    public InputStream getBinaryStream(String columnLabel) throws SQLException {
        logInfo("getBinaryStream " + columnLabel);
        return null;
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
    public String getCursorName() throws SQLException {
        logInfo("getCursorName");
        return null;
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        logInfo("getMetaData ");
        return new TaackResultSetMetaData(this);
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {
        logInfo("getObject " + columnIndex);
        columnIndex -= 1;
        if (taackResultSetProto.getColumns(columnIndex).getJavaType() == TaackResultSetOuterClass.Column.JavaType.LONG)
            return taackResultSetProto.getCells(currentCellInProto + columnIndex).getLongValue();
        else if (taackResultSetProto.getColumns(columnIndex).getJavaType() == TaackResultSetOuterClass.Column.JavaType.STRING)
            return taackResultSetProto.getCells(currentCellInProto + columnIndex).getStringValue();
        else if (taackResultSetProto.getColumns(columnIndex).getJavaType() == TaackResultSetOuterClass.Column.JavaType.BOOL)
            return taackResultSetProto.getCells(currentCellInProto + columnIndex).getBoolValue();
        else if (taackResultSetProto.getColumns(columnIndex).getJavaType() == TaackResultSetOuterClass.Column.JavaType.BIG_DECIMAL)
            return taackResultSetProto.getCells(currentCellInProto + columnIndex).getBigDecimal();
        throw new SQLException("Column #" + columnIndex + " is not a Object, it is a " + taackResultSetProto.getColumns(columnIndex).getJavaType());
    }

    @Override
    public Object getObject(String columnLabel) throws SQLException {
        logInfo("getObject " + columnLabel);
        return null;
    }

    @Override
    public int findColumn(String columnLabel) throws SQLException {
        logInfo("findColumn " + columnLabel);
        return columnNames.get(columnLabel) + 1;
    }

    @Override
    public Reader getCharacterStream(int columnIndex) throws SQLException {
        logInfo("getCharacterStream " + columnIndex);
        return null;
    }

    @Override
    public Reader getCharacterStream(String columnLabel) throws SQLException {
        logInfo("getCharacterStream " + columnLabel);
        return null;
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        logInfo("getBigDecimal " + columnIndex);
        columnIndex -= 1;
        if (taackResultSetProto.getColumns(columnIndex).getJavaType() == TaackResultSetOuterClass.Column.JavaType.BIG_DECIMAL)
            return new BigDecimal(taackResultSetProto.getCells(currentCellInProto + columnIndex).getBigDecimal());
        throw new SQLException("Column #" + columnIndex + " is not a BigDecimal, it is a " + taackResultSetProto.getColumns(columnIndex).getJavaType());
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        logInfo("getBigDecimal " + columnLabel);
        return getBigDecimal(columnNames.get(columnLabel) + 1);
    }

    @Override
    public boolean isBeforeFirst() throws SQLException {
        logInfo("isBeforeFirst ");
        return currentCellInProto < 0;
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        logInfo("isAfterLast ");
        return currentCellInProto > numberOfCellsInProto;
    }

    @Override
    public boolean isFirst() throws SQLException {
        return currentCellInProto == -numberOfColumn;
    }

    @Override
    public boolean isLast() throws SQLException {
        logInfo("isLast ");
        return currentCellInProto == numberOfCellsInProto - numberOfColumn;
    }

    @Override
    public void beforeFirst() throws SQLException {
        logInfo("beforeFirst ");
        currentCellInProto = -numberOfColumn;
        absoluteLineNumber = 0;
    }

    @Override
    public void afterLast() throws SQLException {
        logInfo("afterLast ");
        currentCellInProto = numberOfCellsInProto;
    }

    @Override
    public boolean first() throws SQLException {
        logInfo("first ");
        currentCellInProto = 0;
        absoluteLineNumber = offsetInRowInProto;
        return numberOfCellsInProto < numberOfColumn;
    }

    @Override
    public boolean last() throws SQLException {
        logInfo("last " + (numberOfCellsInProto < numberOfColumn));
        currentCellInProto = numberOfCellsInProto - numberOfColumn;
        absoluteLineNumber = offsetInRowInProto + currentCellInProto / numberOfColumn;
        return numberOfCellsInProto < numberOfColumn;
    }

    @Override
    public int getRow() throws SQLException {
        logInfo("getRow " + absoluteLineNumber);
        return absoluteLineNumber;
    }
    private boolean absoluteLineNumberInRS() {
        return absoluteLineNumber >= offsetInRowInProto && absoluteLineNumber < offsetInRowInProto + numberOfCellsInProto / numberOfColumn;
    }
    @Override
    public boolean absolute(int row) throws SQLException {
        logInfo("absolute " + row + " offsetInRowInProto: " + offsetInRowInProto);
        if (row == 1) {
            first();
            return true;
        } else if (row == -1) {
            last();
            return true;
        }
        if (row > 0) absoluteLineNumber = row;
        else if (row < 0) absoluteLineNumber = (int)numberOfRowsFromQuery + row;
        else beforeFirst();
        logInfo("absolute returns: " + absoluteLineNumberInRS() + " currentCellInProto: " + currentCellInProto);
        return absoluteLineNumberInRS();
    }

    @Override
    public boolean relative(int rows) throws SQLException {
        if (rows == 1) {
            return next();
        } else if (rows == -1) {
            return previous();
        } else if (rows == 0) return true;

        logInfo("relative " + rows);
        absoluteLineNumber += rows;
        return absoluteLineNumber > offsetInRowInProto && absoluteLineNumber < offsetInRowInProto + numberOfCellsInProto / numberOfColumn;
    }

    @Override
    public boolean previous() throws SQLException {
        logInfo("previous ");
        currentCellInProto = numberOfColumn * getRow() - numberOfColumn;
        absoluteLineNumber ++;
        return currentCellInProto < numberOfCellsInProto && currentCellInProto > 0;
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        logInfo("setFetchDirection " + direction);
        this.direction = direction;
    }

    @Override
    public int getFetchDirection() throws SQLException {
        logInfo("getFetchDirection ");
        return FETCH_FORWARD;
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        logInfo("setFetchSize ");
        this.fetchSizeInRow = rows;
    }

    @Override
    public int getFetchSize() throws SQLException {
        logInfo("getFetchSize ");
        return fetchSizeInRow;
    }

    @Override
    public int getType() throws SQLException {
        logInfo("getType ");
        return ResultSet.TYPE_SCROLL_SENSITIVE;
    }

    @Override
    public int getConcurrency() throws SQLException {
        logInfo("getConcurrency ");
        return ResultSet.CONCUR_READ_ONLY;
    }

    @Override
    public boolean rowUpdated() throws SQLException {
        logInfo("rowUpdated ");
        return false;
    }

    @Override
    public boolean rowInserted() throws SQLException {
        logInfo("rowInserted ");
        return false;
    }

    @Override
    public boolean rowDeleted() throws SQLException {
        logInfo("rowDeleted ");
        return false;
    }

    @Override
    public void updateNull(int columnIndex) throws SQLException {
        logInfo("updateNull ");

    }

    @Override
    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        logInfo("updateBoolean ");

    }

    @Override
    public void updateByte(int columnIndex, byte x) throws SQLException {
        logInfo("updateByte ");

    }

    @Override
    public void updateShort(int columnIndex, short x) throws SQLException {
        logInfo("updateShort ");

    }

    @Override
    public void updateInt(int columnIndex, int x) throws SQLException {
        logInfo("updateInt ");

    }

    @Override
    public void updateLong(int columnIndex, long x) throws SQLException {
        logInfo("updateLong ");

    }

    @Override
    public void updateFloat(int columnIndex, float x) throws SQLException {
        logInfo("updateFloat ");

    }

    @Override
    public void updateDouble(int columnIndex, double x) throws SQLException {
        logInfo("updateDouble ");

    }

    @Override
    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        logInfo("updateBigDecimal ");

    }

    @Override
    public void updateString(int columnIndex, String x) throws SQLException {
        logInfo("updateString ");

    }

    @Override
    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        logInfo("updateBytes ");

    }

    @Override
    public void updateDate(int columnIndex, Date x) throws SQLException {
        logInfo("updateDate ");

    }

    @Override
    public void updateTime(int columnIndex, Time x) throws SQLException {
        logInfo("updateTime ");

    }

    @Override
    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        logInfo("updateTimestamp ");

    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        logInfo("updateAsciiStream ");

    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        logInfo("updateBinaryStream ");

    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        logInfo("updateCharacterStream ");

    }

    @Override
    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
        logInfo("updateObject ");

    }

    @Override
    public void updateObject(int columnIndex, Object x) throws SQLException {
        logInfo("updateObject ");

    }

    @Override
    public void updateNull(String columnLabel) throws SQLException {
        logInfo("updateNull ");

    }

    @Override
    public void updateBoolean(String columnLabel, boolean x) throws SQLException {
        logInfo("updateBoolean ");

    }

    @Override
    public void updateByte(String columnLabel, byte x) throws SQLException {
        logInfo("updateByte ");

    }

    @Override
    public void updateShort(String columnLabel, short x) throws SQLException {
        logInfo("updateShort ");

    }

    @Override
    public void updateInt(String columnLabel, int x) throws SQLException {
        logInfo("updateInt ");

    }

    @Override
    public void updateLong(String columnLabel, long x) throws SQLException {
        logInfo("updateLong ");

    }

    @Override
    public void updateFloat(String columnLabel, float x) throws SQLException {
        logInfo("updateFloat ");

    }

    @Override
    public void updateDouble(String columnLabel, double x) throws SQLException {
        logInfo("updateDouble ");

    }

    @Override
    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
        logInfo("updateBigDecimal ");

    }

    @Override
    public void updateString(String columnLabel, String x) throws SQLException {
        logInfo("updateString ");

    }

    @Override
    public void updateBytes(String columnLabel, byte[] x) throws SQLException {
        logInfo("updateBytes ");

    }

    @Override
    public void updateDate(String columnLabel, Date x) throws SQLException {
        logInfo("updateDate ");

    }

    @Override
    public void updateTime(String columnLabel, Time x) throws SQLException {
        logInfo("updateTime ");

    }

    @Override
    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
        logInfo("updateTimestamp ");

    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
        logInfo("updateAsciiStream ");

    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
        logInfo("updateBinaryStream ");

    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
        logInfo("updateCharacterStream ");

    }

    @Override
    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
        logInfo("updateObject ");

    }

    @Override
    public void updateObject(String columnLabel, Object x) throws SQLException {
        logInfo("updateObject ");

    }

    @Override
    public void insertRow() throws SQLException {
        logInfo("insertRow ");

    }

    @Override
    public void updateRow() throws SQLException {
        logInfo("updateRow ");

    }

    @Override
    public void deleteRow() throws SQLException {
        logInfo("deleteRow ");

    }

    @Override
    public void refreshRow() throws SQLException {
        logInfo("refreshRow ");

    }

    @Override
    public void cancelRowUpdates() throws SQLException {
        logInfo("cancelRowUpdates ");

    }

    @Override
    public void moveToInsertRow() throws SQLException {
        logInfo("moveToInsertRow ");

    }

    @Override
    public void moveToCurrentRow() throws SQLException {
        logInfo("moveToCurrentRow ");

    }

    @Override
    public Statement getStatement() throws SQLException {
        logInfo("getStatement ");
        return null;
    }

    @Override
    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        logInfo("getObject ");
        return null;
    }

    @Override
    public Ref getRef(int columnIndex) throws SQLException {
        logInfo("getRef ");
        return null;
    }

    @Override
    public Blob getBlob(int columnIndex) throws SQLException {
        logInfo("getBlob ");
        return null;
    }

    @Override
    public Clob getClob(int columnIndex) throws SQLException {
        logInfo("getClob ");
        return null;
    }

    @Override
    public Array getArray(int columnIndex) throws SQLException {
        logInfo("getArray ");
        return null;
    }

    @Override
    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        logInfo("getObject ");
        return null;
    }

    @Override
    public Ref getRef(String columnLabel) throws SQLException {
        logInfo("getRef ");
        return null;
    }

    @Override
    public Blob getBlob(String columnLabel) throws SQLException {
        logInfo("getBlob ");
        return null;
    }

    @Override
    public Clob getClob(String columnLabel) throws SQLException {
        logInfo("getClob ");
        return null;
    }

    @Override
    public Array getArray(String columnLabel) throws SQLException {
        logInfo("getArray ");
        return null;
    }

    @Override
    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        logInfo("getDate ");
        return getDate(columnIndex);
    }

    @Override
    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
        logInfo("getDate ");
        return getDate(columnLabel);
    }

    @Override
    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        logInfo("getTime ");
        return null;
    }

    @Override
    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
        logInfo("getTime ");
        return null;
    }

    @Override
    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        logInfo("getTimestamp ");
        return getTimestamp(columnIndex);
    }

    @Override
    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        logInfo("getTimestamp ");
        return getTimestamp(columnLabel);
    }

    @Override
    public URL getURL(int columnIndex) throws SQLException {
        logInfo("getURL ");
        return null;
    }

    @Override
    public URL getURL(String columnLabel) throws SQLException {
        logInfo("getURL ");
        return null;
    }

    @Override
    public void updateRef(int columnIndex, Ref x) throws SQLException {
        logInfo("updateRef ");

    }

    @Override
    public void updateRef(String columnLabel, Ref x) throws SQLException {
        logInfo("updateRef ");

    }

    @Override
    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        logInfo("updateBlob ");

    }

    @Override
    public void updateBlob(String columnLabel, Blob x) throws SQLException {
        logInfo("updateBlob ");

    }

    @Override
    public void updateClob(int columnIndex, Clob x) throws SQLException {
        logInfo("updateClob ");

    }

    @Override
    public void updateClob(String columnLabel, Clob x) throws SQLException {
        logInfo("updateClob ");

    }

    @Override
    public void updateArray(int columnIndex, Array x) throws SQLException {
        logInfo("updateArray ");

    }

    @Override
    public void updateArray(String columnLabel, Array x) throws SQLException {
        logInfo("updateArray ");

    }

    @Override
    public RowId getRowId(int columnIndex) throws SQLException {
        logInfo("getRowId ");
        return new TaackRowId(taackResultSetProto.getCells(getRow()).getLongValue());
    }

    @Override
    public RowId getRowId(String columnLabel) throws SQLException {
        logInfo("getRowId ");
        return new TaackRowId(taackResultSetProto.getCells(getRow()).getLongValue());
    }

    @Override
    public void updateRowId(int columnIndex, RowId x) throws SQLException {
        logInfo("updateRowId ");

    }

    @Override
    public void updateRowId(String columnLabel, RowId x) throws SQLException {
        logInfo("updateRowId ");

    }

    @Override
    public int getHoldability() throws SQLException {
        logInfo("getHoldability ");
        return ResultSet.CLOSE_CURSORS_AT_COMMIT;
    }

    @Override
    public boolean isClosed() throws SQLException {
        logInfo("isClosed ");
        return isClosed;
    }

    @Override
    public void updateNString(int columnIndex, String nString) throws SQLException {
        logInfo("updateNString ");

    }

    @Override
    public void updateNString(String columnLabel, String nString) throws SQLException {
        logInfo("updateNString ");

    }

    @Override
    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
        logInfo("updateNClob ");

    }

    @Override
    public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
        logInfo("updateNClob ");

    }

    @Override
    public NClob getNClob(int columnIndex) throws SQLException {
        logInfo("getNClob ");
        return null;
    }

    @Override
    public NClob getNClob(String columnLabel) throws SQLException {
        logInfo("getNClob ");
        return null;
    }

    @Override
    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        logInfo("getSQLXML ");
        return null;
    }

    @Override
    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        logInfo("getSQLXML ");
        return null;
    }

    @Override
    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
        logInfo("updateSQLXML ");

    }

    @Override
    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
        logInfo("updateSQLXML ");

    }

    @Override
    public String getNString(int columnIndex) throws SQLException {
        logInfo("getNString ");
        return null;
    }

    @Override
    public String getNString(String columnLabel) throws SQLException {
        logInfo("getNString ");
        return null;
    }

    @Override
    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        logInfo("getNCharacterStream ");
        return null;
    }

    @Override
    public Reader getNCharacterStream(String columnLabel) throws SQLException {
        logInfo("getNCharacterStream ");
        return null;
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        logInfo("updateNCharacterStream ");

    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        logInfo("updateNCharacterStream ");

    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
        logInfo("updateAsciiStream ");

    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
        logInfo("updateBinaryStream ");

    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        logInfo("updateCharacterStream ");

    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
        logInfo("updateAsciiStream ");

    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
        logInfo("updateBinaryStream ");

    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        logInfo("updateCharacterStream ");

    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
        logInfo("updateBlob ");

    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
        logInfo("updateBlob ");

    }

    @Override
    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        logInfo("updateClob ");

    }

    @Override
    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        logInfo("updateClob ");

    }

    @Override
    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        logInfo("updateNClob ");

    }

    @Override
    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        logInfo("updateNClob ");

    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
        logInfo("updateNCharacterStream ");

    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
        logInfo("updateNCharacterStream ");

    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
        logInfo("updateAsciiStream ");

    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
        logInfo("updateBinaryStream ");

    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
        logInfo("updateCharacterStream ");

    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
        logInfo("updateAsciiStream ");

    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
        logInfo("updateBinaryStream ");

    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
        logInfo("updateCharacterStream ");

    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        logInfo("updateBlob ");

    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        logInfo("updateBlob ");

    }

    @Override
    public void updateClob(int columnIndex, Reader reader) throws SQLException {
        logInfo("updateClob ");

    }

    @Override
    public void updateClob(String columnLabel, Reader reader) throws SQLException {
        logInfo("updateClob ");

    }

    @Override
    public void updateNClob(int columnIndex, Reader reader) throws SQLException {
        logInfo("updateNClob ");

    }

    @Override
    public void updateNClob(String columnLabel, Reader reader) throws SQLException {
        logInfo("updateNClob ");

    }

    @Override
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        logInfo("getObject ");
        return null;
    }

    @Override
    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        logInfo("getObject ");
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        logInfo("unwrap ");
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        logInfo("isWrapperFor ");
        return false;
    }
}
