package taack.jdbc.client;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.http.protocol.BasicHttpContext;
import org.apache.hc.core5.http.protocol.HttpContext;
import taack.jdbc.common.TaackResultSetOuterClass;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.concurrent.Executor;

public final class TaackConnection implements Connection {

//    final static Logger logger = LoggerFactory.getLogger(TaackConnection.class);

    final HttpContext httpContext = new BasicHttpContext();
    final String url;
    final String user;
    final String password;
    boolean closed = false;

    TaackStatement statement = null;
    TaackConnection(String url, String username, String password) throws UnsupportedEncodingException {
        // assume SLF4J is bound to logback in the current environment
//        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        // print logback's internal status
//        StatusPrinter.print(lc);

        logInfo("TaackConnection " + url + " u: " + username + " p: " + password);

        this.url = url;
        this.password = password;
        this.user = username;
        String protocol = url.startsWith("localhost:") ? "http://" : "https://";
        HttpPost req = new HttpPost(protocol + url + "/login/authenticate");
        req.setEntity(
                new UrlEncodedFormEntity(
                        Arrays.asList(new BasicNameValuePair("username", username),
                                new BasicNameValuePair("password", password)),
                        StandardCharsets.UTF_8
                ));
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            BasicCookieStore cookieStore = new BasicCookieStore();
            httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
            var response = httpClient.execute(req, httpContext);
            logInfo("Status " + response.getCode() + " R: " + response.getReasonPhrase());
            String responseString = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
            logInfo("responseString: " + responseString);
            HttpPost req2 = new HttpPost(protocol + url + "/taackJdbc/initConn");
            var response2 = httpClient.execute(req2, httpContext);
            System.out.println("Status2 " + response2.getCode() + " R: " + response2.getReasonPhrase());
            String responseString2 = new String(response2.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
            System.out.println("responseString2: " + responseString2);

        } catch (IOException e) {
            logError("Exception " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void logInfo(String message) {
        System.out.println("TaackResultSetJdbc:INFO :" + message);
    }

    private void logError(String message) {
        System.out.println("TaackResultSetJdbc:ERROR:" + message);
    }

    private byte[] executeReqAction(Map<String, String> params, String action) throws UnsupportedEncodingException {
        logInfo("executeReqAction " + params + " action: " + action);
        String protocol = url.startsWith("localhost:") ? "http://" : "https://";
        HttpPost req = new HttpPost(protocol + url + "/taackJdbc/" + action);
        CloseableHttpResponse response;
        ArrayList<BasicNameValuePair> valuePairs = new ArrayList<>();
        for (Map.Entry<String, String> e : params.entrySet()) {
            valuePairs.add(new BasicNameValuePair(e.getKey(), e.getValue()));
        }
        req.setEntity(new UrlEncodedFormEntity(valuePairs, StandardCharsets.UTF_8));
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            response = httpClient.execute(req, httpContext);
            return response.getEntity().getContent().readAllBytes();
        } catch (IOException e) {
            logError("AUO 333 IOException");
            throw new RuntimeException(e);
        }
    }

    byte[] executeReq(Map<String, String> params) throws UnsupportedEncodingException {
        logInfo("executeReq " + params);
        return executeReqAction(params, "query");
    }

    ResultSet tables(String catalog, String schemaPattern, String tableNamePattern) {
        logInfo("tables c: " + catalog + " s: " + schemaPattern + " t: " + tableNamePattern);
        byte[] b = new byte[0];
        try {
            b = executeReqAction(Map.of(
                    "schemaPattern", schemaPattern == null ? "public" : schemaPattern,
                    "tableNamePattern", tableNamePattern
            ), "tables");
        } catch (UnsupportedEncodingException e) {
            logError("AUO 444 UnsupportedEncodingException");
            throw new RuntimeException(e);
        }
        try {
            var rs = new TaackResultSetJdbc(TaackResultSetOuterClass.TaackResultSet.parseFrom(b));
            System.out.println("RS: \n" + TaackDriver.printRS(rs));
            return rs;
        } catch (InvalidProtocolBufferException e) {
            logError("AUO 555 InvalidProtocolBufferException");
            throw new RuntimeException(e);
        }
    }

    ResultSet getColumnRSMetaData(String tableName) {
        logInfo("getColumnRSMetaData " + " t: " + tableName);
        byte[] b = new byte[0];
        try {
            b = executeReqAction(Map.of(
                    "tableName", tableName
            ), "columnsMetaData");
        } catch (UnsupportedEncodingException e) {
            logError("AUO 666 UnsupportedEncodingException");
            throw new RuntimeException(e);
        }
        try {
            var rs = new TaackResultSetJdbc(TaackResultSetOuterClass.TaackResultSet.parseFrom(b));
            System.out.println("RS: \n" + TaackDriver.printRS(rs));
            return rs;
        } catch (InvalidProtocolBufferException e) {
            logError("AUO 777 InvalidProtocolBufferException");
            throw new RuntimeException(e);
        }
    }
    ResultSet getColumns(String schemaPattern, String tableNamePattern, String columnNamePattern) {
        logInfo("getColumn " + " t: " + tableNamePattern);
        byte[] b = new byte[0];
        try {
            b = executeReqAction(Map.of(
                    "schemaPattern", schemaPattern == null ? "public" : schemaPattern,
                    "tableNamePattern", tableNamePattern,
                    "columnNamePattern", columnNamePattern
            ), "columns");
        } catch (UnsupportedEncodingException e) {
            logError("AUO 666 UnsupportedEncodingException");
            throw new RuntimeException(e);
        }
        try {
            var rs = new TaackResultSetJdbc(TaackResultSetOuterClass.TaackResultSet.parseFrom(b));
            System.out.println("RS: \n" + TaackDriver.printRS(rs));
            return rs;
        } catch (InvalidProtocolBufferException e) {
            logError("AUO 777 InvalidProtocolBufferException");
            throw new RuntimeException(e);
        }
    }

    ResultSet indexInfoRSMetaData(String tableName) {
        logInfo("getIndexInfoRSMetaData " + " t: " + tableName);
        byte[] b = new byte[0];
        try {
//            b = executeReqAction(Map.of(
//                    "schemaPattern", schemaPattern == null ? "public" : schemaPattern,
//                    "tableNamePattern", tableNamePattern,
//                    "columnNamePattern", columnNamePattern
//            ), "columns");
            b = executeReqAction(Map.of(
                    "tableName", tableName
            ), "indexInfoMetaData");
        } catch (UnsupportedEncodingException e) {
            logError("AUO 666 UnsupportedEncodingException");
            throw new RuntimeException(e);
        }
        try {
            var rs = new TaackResultSetJdbc(TaackResultSetOuterClass.TaackResultSet.parseFrom(b));
            System.out.println("RS: \n" + TaackDriver.printRS(rs));
            return rs;
        } catch (InvalidProtocolBufferException e) {
            logError("AUO 777 InvalidProtocolBufferException");
            throw new RuntimeException(e);
        }
    }

    ResultSet pk(String tableName) {
        logInfo("pk " + " t: " + tableName);
        byte[] b = new byte[0];
        try {
            b = executeReqAction(Map.of(
                    "tableName", tableName
            ), "pk");
        } catch (UnsupportedEncodingException e) {
            logError("AUO 666 UnsupportedEncodingException");
            throw new RuntimeException(e);
        }
        try {
            var rs = new TaackResultSetJdbc(TaackResultSetOuterClass.TaackResultSet.parseFrom(b));
            System.out.println("RS: \n" + TaackDriver.printRS(rs));
            return rs;
        } catch (InvalidProtocolBufferException e) {
            logError("AUO 777 InvalidProtocolBufferException");
            throw new RuntimeException(e);
        }
    }

    ResultSet indexInfo(String tableName) {
        logInfo("indexInfo " + " t: " + tableName);
        byte[] b = new byte[0];
        try {
            b = executeReqAction(Map.of(
                    "tableName", tableName
            ), "indexInfo");
        } catch (UnsupportedEncodingException e) {
            logError("AUO 666 UnsupportedEncodingException");
            throw new RuntimeException(e);
        }
        try {
            var rs = new TaackResultSetJdbc(TaackResultSetOuterClass.TaackResultSet.parseFrom(b));
            System.out.println("RS: \n" + TaackDriver.printRS(rs));
            return rs;
        } catch (InvalidProtocolBufferException e) {
            logError("AUO 777 InvalidProtocolBufferException");
            throw new RuntimeException(e);
        }
    }
    @Override
    public Statement createStatement() throws SQLException {
        logInfo("createStatement");
        if (statement == null) statement = new TaackStatement(this);
//        statement = new TaackStatement(this);
        return statement;
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        logInfo("prepareStatement " + sql);
        return new TaackPrepareStatement(sql, this);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        logInfo("prepareCall " + sql);
        return null;
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        logInfo("nativeSQL " + sql);
        return null;
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        logInfo("setAutoCommit " + autoCommit);

    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        logInfo("getAutoCommit");

        return false;
    }

    @Override
    public void commit() throws SQLException {
        logInfo("commit");

    }

    @Override
    public void rollback() throws SQLException {
        logInfo("rollback");

    }


    @Override
    public void close() throws SQLException {
        logInfo("close");
        statement = null;
        closed = true;
    }

    @Override
    public boolean isClosed() throws SQLException {
        logInfo("isClosed " + closed);

        return closed;
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        logInfo("getMetaData");

        return new TaackDatabaseMetaData(this);
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        logInfo("setReadOnly " + readOnly);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        logInfo("isReadOnly");

        return false;
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        logInfo("setCatalog " + catalog);
    }

    @Override
    public String getCatalog() throws SQLException {
        logInfo("getCatalog");

        return "default";
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        logInfo("setTransactionIsolation " + level);


    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        logInfo("getTransactionIsolation");

        return Connection.TRANSACTION_NONE;
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
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        logInfo("createStatement " + " rst: " + resultSetType + " rsc: " + resultSetConcurrency + " statement: " + statement);
        statement = new TaackStatement(this);
        return statement;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        logInfo("prepareStatement " + sql + " rst: " + resultSetType + " rsc: " + resultSetConcurrency);
        return new TaackPrepareStatement(sql, this);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        logInfo("prepareCall " + sql + " rst: " + resultSetType + " rsc: " + resultSetConcurrency);
        return null;
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        logInfo("getTypeMap");
        return null;
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        logInfo("setTypeMap " + map);

    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        logInfo("setHoldability " + holdability);

    }

    @Override
    public int getHoldability() throws SQLException {
        logInfo("getHoldability");
        return 0;
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        logInfo("setSavepoint");
        return null;
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        logInfo("setSavepoint " + name);
        return null;
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        logInfo("rollback " + savepoint);

    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        logInfo("releaseSavepoint " + savepoint);

    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        logInfo("createStatement " + resultSetType + " rsc: " + resultSetConcurrency + " rsh: " + resultSetHoldability);
        if (statement == null) statement = new TaackStatement(this);
        return statement;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        logInfo("prepareStatement " + sql + " rst: " + resultSetType + " rsc: " + resultSetConcurrency + " rsh: " + resultSetHoldability);
        return new TaackPrepareStatement(sql, this);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        logInfo("prepareCall " + sql + " rst: " + resultSetType + " rsc: " + resultSetConcurrency + " rsh: " + resultSetHoldability);
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        logInfo("prepareCall " + sql + " agk: " + autoGeneratedKeys);
        return new TaackPrepareStatement(sql, this);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        logInfo("prepareStatement " + sql + " ci: " + Arrays.toString(columnIndexes));
        return new TaackPrepareStatement(sql, this);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        logInfo("prepareStatement " + sql + " cn: " + Arrays.toString(columnNames));
        return new TaackPrepareStatement(sql, this);
    }

    @Override
    public Clob createClob() throws SQLException {
        logInfo("createClob");
        return null;
    }

    @Override
    public Blob createBlob() throws SQLException {
        logInfo("createBlob");
        return null;
    }

    @Override
    public NClob createNClob() throws SQLException {
        logInfo("createNClob");
        return null;
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        logInfo("createSQLXML");
        return null;
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        logInfo("isValid " + timeout);
        return !closed;
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        logInfo("setClientInfo " + name + " v: " + value);

    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        logInfo("setClientInfo " + properties);

    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        logInfo("getClientInfo " + name);
        return null;
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        logInfo("getClientInfo");
        return null;
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        logInfo("prepareCall " + typeName + " " + elements);
        return null;
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        logInfo("createStruct " + typeName + " " + attributes);
        return null;
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        logInfo("setSchema " + schema);

    }

    @Override
    public String getSchema() throws SQLException {
        logInfo("getSchema");
        return "public";
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        logInfo("abort " + executor);

    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        logInfo("setNetworkTimeout " + executor + " " + milliseconds);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        logInfo("getNetworkTimeout");
        return 0;
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
