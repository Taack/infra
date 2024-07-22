package taack.jdbc.client;

import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Collectors;

public final class TaackDriver implements Driver {
    protected static final Driver INSTANCE = new TaackDriver();
//    private static final Logger parentLogger = LoggerFactory.getLogger("taack.jdbc.client");
//    private static final Logger logger = LoggerFactory.getLogger(TaackDriver.class);
    private static boolean registered;
    public TaackDriver() {}

    private static void logInfo(String message) {
        System.out.println("TaackDriver:INFO :" + message);
    }

    public static String printRS(ResultSet resultSet) {
//        try {
//            resultSet.beforeFirst();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        StringBuilder ret = new StringBuilder();
//        ResultSetMetaData rsmd = null;
//        try {
//            rsmd = resultSet.getMetaData();
//            int columnsNumber = rsmd.getColumnCount();
//            while (resultSet.next()) {
//                for (int i = 1; i <= columnsNumber; i++) {
//                    if (i > 1) ret.append(",  ");
//                    String columnValue = resultSet.getString(i);
//                    ret.append(rsmd.getColumnName(i)).append("[").append(rsmd.getColumnClassName(i)).append(",").append(rsmd.getColumnType(i)).append("]").append(": ").append(columnValue);
//                }
//                ret.append("\n");
//            }
//            try {
//                resultSet.beforeFirst();
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }
//            return ret.toString();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
    return "";
    }

    @Override
    public Connection connect(String url, Properties info) {
        logInfo("connect " + url + " info " + info);
        String[] parts = url.split(":");

        if (parts.length < 2 ||	!parts[0].toLowerCase().equals("jdbc") || !parts[1].toLowerCase().equals("taack"))
            return null;

        String serverDb = Arrays.stream(parts).skip(2).collect(Collectors.joining(":"));
        String server = serverDb.substring(0, serverDb.lastIndexOf('/'));
        try {
            return new TaackConnection(server, info.getProperty("username"), info.getProperty("password"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private static Properties parseURL(String url) {
        logInfo("parseURL " + url);
        if (!url.startsWith("jdbc:taack:")) {
            logInfo("JDBC URL must start with \"jdbc:taack:\" but was: " + url);
            return null;
        }
        return new Properties();
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        logInfo("acceptsURL " + url);
        return !url.startsWith("jdbc:taack:");
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        logInfo("getPropertyInfo " + url + " info " + info);
        return new DriverPropertyInfo[0];
    }

    @Override
    public int getMajorVersion() {
        logInfo("getMajorVersion ");
        return 1;
    }

    @Override
    public int getMinorVersion() {
        logInfo("getMinorVersion ");
        return 1;
    }

    @Override
    public boolean jdbcCompliant() {
        logInfo("jdbcCompliant ");
        return false;
    }

    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        logInfo("getParentLogger ");
        return null;
    }

    static synchronized Driver load() {
        logInfo("load ");
        if (!registered) {
            registered = true;
            try {
                DriverManager.registerDriver(INSTANCE);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        return INSTANCE;
    }

    static {
        load();
    }

}
