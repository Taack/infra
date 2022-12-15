package taack.jdbc.client;

import taack.jdbc.common.TaackResultSetOuterClass;

import java.sql.*;
import java.util.Arrays;

public class TaackDatabaseMetaData implements DatabaseMetaData {

//    final static Logger logger = LoggerFactory.getLogger(TaackDatabaseMetaData.class);

    final TaackConnection taackConnection;
    ResultSet currentResultSet;

    TaackDatabaseMetaData(TaackConnection taackConnection) {
        this.taackConnection = taackConnection;
    }

    
    private void logInfo(String message) {
        System.out.println("TaackDatabaseMetaData::" + message);
    }
    @Override
    public boolean allProceduresAreCallable() throws SQLException {
        logInfo("allProceduresAreCallable");
        return false;
    }

    @Override
    public boolean allTablesAreSelectable() throws SQLException {
        logInfo("allTablesAreSelectable");
        return true;
    }

    @Override
    public String getURL() throws SQLException {
        logInfo("getURL");
        return taackConnection.url;
    }

    @Override
    public String getUserName() throws SQLException {
        logInfo("getUserName");
        return taackConnection.user;
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        logInfo("isReadOnly");
        return true;
    }

    @Override
    public boolean nullsAreSortedHigh() throws SQLException {
        logInfo("nullsAreSortedHigh");
        return false;
    }

    @Override
    public boolean nullsAreSortedLow() throws SQLException {
        logInfo("nullsAreSortedLow");
        return false;
    }

    @Override
    public boolean nullsAreSortedAtStart() throws SQLException {
        logInfo("nullsAreSortedAtStart");
        return false;
    }

    @Override
    public boolean nullsAreSortedAtEnd() throws SQLException {
        logInfo("nullsAreSortedAtEnd");
        return false;
    }

    @Override
    public String getDatabaseProductName() throws SQLException {
        logInfo("getDatabaseProductName");
        return "Taack";
    }

    @Override
    public String getDatabaseProductVersion() throws SQLException {
        logInfo("getDatabaseProductVersion");
        return "GormTaack.1.1";
    }

    @Override
    public String getDriverName() throws SQLException {
        logInfo("getDriverName");
        return "Taack";
    }

    @Override
    public String getDriverVersion() throws SQLException {
        logInfo("getDriverVersion");
        return "v1.1";
    }

    @Override
    public int getDriverMajorVersion() {
        logInfo("getDriverMajorVersion");
        return TaackDriver.INSTANCE.getMajorVersion();
    }

    @Override
    public int getDriverMinorVersion() {
        logInfo("getDriverMinorVersion");
        return TaackDriver.INSTANCE.getMinorVersion();
    }

    @Override
    public boolean usesLocalFiles() throws SQLException {
        logInfo("usesLocalFiles");
        return false;
    }

    @Override
    public boolean usesLocalFilePerTable() throws SQLException {
        logInfo("usesLocalFilePerTable");
        return false;
    }

    @Override
    public boolean supportsMixedCaseIdentifiers() throws SQLException {
        logInfo("supportsMixedCaseIdentifiers");
        return false;
    }

    @Override
    public boolean storesUpperCaseIdentifiers() throws SQLException {
        logInfo("storesUpperCaseIdentifiers");
        return false;
    }

    @Override
    public boolean storesLowerCaseIdentifiers() throws SQLException {
        logInfo("storesLowerCaseIdentifiers");
        return false;
    }

    @Override
    public boolean storesMixedCaseIdentifiers() throws SQLException {
        logInfo("storesMixedCaseIdentifiers");
        return false;
    }

    @Override
    public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
        logInfo("supportsMixedCaseQuotedIdentifiers");
        return true;
    }

    @Override
    public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
        logInfo("storesUpperCaseQuotedIdentifiers");
        return false;
    }

    @Override
    public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
        logInfo("storesLowerCaseQuotedIdentifiers");
        return false;
    }

    @Override
    public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
        logInfo("storesMixedCaseQuotedIdentifiers");
        return false;
    }

    @Override
    public String getIdentifierQuoteString() throws SQLException {
        logInfo("getIdentifierQuoteString");
        return "'";
    }

    @Override
    public String getSQLKeywords() throws SQLException {
        logInfo("getSQLKeywords");
        return "";
    }

    @Override
    public String getNumericFunctions() throws SQLException {
        logInfo("getNumericFunctions");
        return "";
    }

    @Override
    public String getStringFunctions() throws SQLException {
        logInfo("getStringFunctions");
        return "";
    }

    @Override
    public String getSystemFunctions() throws SQLException {
        logInfo("getSystemFunctions");
        return "";
    }

    @Override
    public String getTimeDateFunctions() throws SQLException {
        logInfo("getTimeDateFunctions");
        return "";
    }

    @Override
    public String getSearchStringEscape() throws SQLException {
        logInfo("getSearchStringEscape");
        return "";
    }

    @Override
    public String getExtraNameCharacters() throws SQLException {
        logInfo("getExtraNameCharacters");
        return ".";
    }

    @Override
    public boolean supportsAlterTableWithAddColumn() throws SQLException {
        logInfo("supportsAlterTableWithAddColumn");
        return false;
    }

    @Override
    public boolean supportsAlterTableWithDropColumn() throws SQLException {
        logInfo("supportsAlterTableWithDropColumn");
        return false;
    }

    @Override
    public boolean supportsColumnAliasing() throws SQLException {
        logInfo("supportsColumnAliasing");
        return false;
    }

    @Override
    public boolean nullPlusNonNullIsNull() throws SQLException {
        logInfo("nullPlusNonNullIsNull");
        return false;
    }

    @Override
    public boolean supportsConvert() throws SQLException {
        logInfo("supportsConvert");
        return false;
    }

    @Override
    public boolean supportsConvert(int fromType, int toType) throws SQLException {
        logInfo("supportsConvert ft: " + fromType + " tt: " + toType);
        return false;
    }

    @Override
    public boolean supportsTableCorrelationNames() throws SQLException {
        logInfo("supportsTableCorrelationNames");
        return false;
    }

    @Override
    public boolean supportsDifferentTableCorrelationNames() throws SQLException {
        logInfo("supportsDifferentTableCorrelationNames");
        return false;
    }

    @Override
    public boolean supportsExpressionsInOrderBy() throws SQLException {
        logInfo("supportsExpressionsInOrderBy");
        return false;
    }

    @Override
    public boolean supportsOrderByUnrelated() throws SQLException {
        logInfo("supportsOrderByUnrelated");
        return false;
    }

    @Override
    public boolean supportsGroupBy() throws SQLException {
        logInfo("supportsGroupBy");
        return false;
    }

    @Override
    public boolean supportsGroupByUnrelated() throws SQLException {
        logInfo("supportsGroupByUnrelated");
        return false;
    }

    @Override
    public boolean supportsGroupByBeyondSelect() throws SQLException {
        logInfo("supportsGroupByBeyondSelect");
        return false;
    }

    @Override
    public boolean supportsLikeEscapeClause() throws SQLException {
        logInfo("supportsLikeEscapeClause");
        return false;
    }

    @Override
    public boolean supportsMultipleResultSets() throws SQLException {
        logInfo("supportsMultipleResultSets");
        return false;
    }

    @Override
    public boolean supportsMultipleTransactions() throws SQLException {
        logInfo("supportsMultipleTransactions");
        return false;
    }

    @Override
    public boolean supportsNonNullableColumns() throws SQLException {
        logInfo("supportsNonNullableColumns");
        return false;
    }

    @Override
    public boolean supportsMinimumSQLGrammar() throws SQLException {
        logInfo("supportsMinimumSQLGrammar");
        return true;
    }

    @Override
    public boolean supportsCoreSQLGrammar() throws SQLException {
        logInfo("supportsCoreSQLGrammar");
        return false;
    }

    @Override
    public boolean supportsExtendedSQLGrammar() throws SQLException {
        logInfo("supportsExtendedSQLGrammar");
        return false;
    }

    @Override
    public boolean supportsANSI92EntryLevelSQL() throws SQLException {
        logInfo("supportsANSI92EntryLevelSQL");
        return false;
    }

    @Override
    public boolean supportsANSI92IntermediateSQL() throws SQLException {
        logInfo("supportsANSI92IntermediateSQL");
        return false;
    }

    @Override
    public boolean supportsANSI92FullSQL() throws SQLException {
        logInfo("supportsANSI92FullSQL");
        return false;
    }

    @Override
    public boolean supportsIntegrityEnhancementFacility() throws SQLException {
        logInfo("supportsIntegrityEnhancementFacility");
        return false;
    }

    @Override
    public boolean supportsOuterJoins() throws SQLException {
        logInfo("supportsOuterJoins");
        return false;
    }

    @Override
    public boolean supportsFullOuterJoins() throws SQLException {
        logInfo("supportsFullOuterJoins");
        return false;
    }

    @Override
    public boolean supportsLimitedOuterJoins() throws SQLException {
        logInfo("supportsLimitedOuterJoins");
        return false;
    }

    @Override
    public String getSchemaTerm() throws SQLException {
        logInfo("getSchemaTerm");
        return "schemas";
    }

    @Override
    public String getProcedureTerm() throws SQLException {
        logInfo("getProcedureTerm");
        return null;
    }

    @Override
    public String getCatalogTerm() throws SQLException {
        logInfo("getCatalogTerm");
        return "catalog";
    }

    @Override
    public boolean isCatalogAtStart() throws SQLException {
        logInfo("isCatalogAtStart");
        return true;
    }

    @Override
    public String getCatalogSeparator() throws SQLException {
        logInfo("getCatalogSeparator");
        return ".";
    }

    @Override
    public boolean supportsSchemasInDataManipulation() throws SQLException {
        logInfo("supportsSchemasInDataManipulation");
        return false;
    }

    @Override
    public boolean supportsSchemasInProcedureCalls() throws SQLException {
        logInfo("supportsSchemasInProcedureCalls");
        return false;
    }

    @Override
    public boolean supportsSchemasInTableDefinitions() throws SQLException {
        logInfo("supportsSchemasInTableDefinitions");
        return false;
    }

    @Override
    public boolean supportsSchemasInIndexDefinitions() throws SQLException {
        logInfo("supportsSchemasInIndexDefinitions");
        return false;
    }

    @Override
    public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
        logInfo("supportsSchemasInPrivilegeDefinitions");
        return false;
    }

    @Override
    public boolean supportsCatalogsInDataManipulation() throws SQLException {
        logInfo("supportsCatalogsInDataManipulation");
        return false;
    }

    @Override
    public boolean supportsCatalogsInProcedureCalls() throws SQLException {
        logInfo("supportsCatalogsInProcedureCalls");
        return false;
    }

    @Override
    public boolean supportsCatalogsInTableDefinitions() throws SQLException {
        logInfo("supportsCatalogsInTableDefinitions");
        return false;
    }

    @Override
    public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
        logInfo("supportsCatalogsInIndexDefinitions");
        return false;
    }

    @Override
    public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
        logInfo("supportsCatalogsInPrivilegeDefinitions");
        return false;
    }

    @Override
    public boolean supportsPositionedDelete() throws SQLException {
        logInfo("supportsPositionedDelete");
        return false;
    }

    @Override
    public boolean supportsPositionedUpdate() throws SQLException {
        logInfo("supportsPositionedUpdate");
        return false;
    }

    @Override
    public boolean supportsSelectForUpdate() throws SQLException {
        logInfo("supportsSelectForUpdate");
        return false;
    }

    @Override
    public boolean supportsStoredProcedures() throws SQLException {
        logInfo("supportsStoredProcedures");
        return false;
    }

    @Override
    public boolean supportsSubqueriesInComparisons() throws SQLException {
        logInfo("supportsSubqueriesInComparisons");
        return false;
    }

    @Override
    public boolean supportsSubqueriesInExists() throws SQLException {
        logInfo("supportsSubqueriesInExists");
        return false;
    }

    @Override
    public boolean supportsSubqueriesInIns() throws SQLException {
        logInfo("supportsSubqueriesInIns");
        return false;
    }

    @Override
    public boolean supportsSubqueriesInQuantifieds() throws SQLException {
        logInfo("supportsSubqueriesInQuantifieds");
        return false;
    }

    @Override
    public boolean supportsCorrelatedSubqueries() throws SQLException {
        logInfo("supportsCorrelatedSubqueries");
        return false;
    }

    @Override
    public boolean supportsUnion() throws SQLException {
        logInfo("supportsUnion");
        return false;
    }

    @Override
    public boolean supportsUnionAll() throws SQLException {
        logInfo("supportsUnionAll");
        return false;
    }

    @Override
    public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
        logInfo("supportsOpenCursorsAcrossCommit");
        return false;
    }

    @Override
    public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
        logInfo("supportsOpenCursorsAcrossRollback");
        return false;
    }

    @Override
    public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
        logInfo("supportsOpenStatementsAcrossCommit");
        return false;
    }

    @Override
    public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
        logInfo("supportsOpenStatementsAcrossRollback");
        return false;
    }

    @Override
    public int getMaxBinaryLiteralLength() throws SQLException {
        logInfo("getMaxBinaryLiteralLength");
        return 0;
    }

    @Override
    public int getMaxCharLiteralLength() throws SQLException {
        logInfo("getMaxCharLiteralLength");
        return 0;
    }

    @Override
    public int getMaxColumnNameLength() throws SQLException {
        logInfo("getMaxColumnNameLength");
        return 0;
    }

    @Override
    public int getMaxColumnsInGroupBy() throws SQLException {
        logInfo("getMaxColumnsInGroupBy");
        return 0;
    }

    @Override
    public int getMaxColumnsInIndex() throws SQLException {
        logInfo("getMaxColumnsInIndex");
        return 0;
    }

    @Override
    public int getMaxColumnsInOrderBy() throws SQLException {
        logInfo("getMaxColumnsInOrderBy");
        return 0;
    }

    @Override
    public int getMaxColumnsInSelect() throws SQLException {
        logInfo("getMaxColumnsInSelect");
        return 64;
    }

    @Override
    public int getMaxColumnsInTable() throws SQLException {
        logInfo("getMaxColumnsInTable");
        return 64;
    }

    @Override
    public int getMaxConnections() throws SQLException {
        logInfo("getMaxConnections");
        return 1;
    }

    @Override
    public int getMaxCursorNameLength() throws SQLException {
        logInfo("getMaxCursorNameLength");
        return 124;
    }

    @Override
    public int getMaxIndexLength() throws SQLException {
        logInfo("getMaxIndexLength");
        return 0;
    }

    @Override
    public int getMaxSchemaNameLength() throws SQLException {
        logInfo("getMaxSchemaNameLength");
        return 0;
    }

    @Override
    public int getMaxProcedureNameLength() throws SQLException {
        logInfo("getMaxProcedureNameLength");
        return 0;
    }

    @Override
    public int getMaxCatalogNameLength() throws SQLException {
        logInfo("getMaxCatalogNameLength");
        return 0;
    }

    @Override
    public int getMaxRowSize() throws SQLException {
        logInfo("getMaxRowSize");
        return 0;
    }

    @Override
    public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
        logInfo("doesMaxRowSizeIncludeBlobs");
        return false;
    }

    @Override
    public int getMaxStatementLength() throws SQLException {
        logInfo("getMaxStatementLength");
        return 0;
    }

    @Override
    public int getMaxStatements() throws SQLException {
        logInfo("getMaxStatements");
        return 0;
    }

    @Override
    public int getMaxTableNameLength() throws SQLException {
        logInfo("getMaxTableNameLength");
        return 0;
    }

    @Override
    public int getMaxTablesInSelect() throws SQLException {
        logInfo("getMaxTablesInSelect");
        return 8;
    }

    @Override
    public int getMaxUserNameLength() throws SQLException {
        logInfo("getMaxUserNameLength");
        return 0;
    }

    @Override
    public int getDefaultTransactionIsolation() throws SQLException {
        logInfo("getDefaultTransactionIsolation");
        return 0;
    }

    @Override
    public boolean supportsTransactions() throws SQLException {
        logInfo("supportsTransactions");
        return false;
    }

    @Override
    public boolean supportsTransactionIsolationLevel(int level) throws SQLException {
        logInfo("supportsTransactionIsolationLevel");
        return false;
    }

    @Override
    public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
        logInfo("supportsDataDefinitionAndDataManipulationTransactions");
        return false;
    }

    @Override
    public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
        logInfo("supportsDataManipulationTransactionsOnly");
        return false;
    }

    @Override
    public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
        logInfo("dataDefinitionCausesTransactionCommit");
        return false;
    }

    @Override
    public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
        logInfo("dataDefinitionIgnoredInTransactions");
        return false;
    }

    @Override
    public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern) throws SQLException {
        logInfo("getProcedures c: " + catalog + " s: " + schemaPattern + " p: " + procedureNamePattern);
        throw new SQLFeatureNotSupportedException("Not implemented yet");
    }

    @Override
    public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern) throws SQLException {
        logInfo("getProcedureColumns c: " + catalog + " s: " + schemaPattern + " p: " + procedureNamePattern + " c: " + columnNamePattern);
        throw new SQLFeatureNotSupportedException("Not implemented yet");
    }

    @Override
    public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {
        /*
        TABLE_CAT: null,  TABLE_SCHEM: public,  TABLE_NAME: action_log_pkey,  TABLE_TYPE: INDEX,  REMARKS: null,  TYPE_CAT: ,  TYPE_SCHEM: ,  TYPE_NAME: ,  SELF_REFERENCING_COL_NAME: ,  REF_GENERATION:
        [. . .]
        TABLE_CAT: null,  TABLE_SCHEM: public,  TABLE_NAME: document_heading_body,  TABLE_TYPE: TABLE,  REMARKS: null,  TYPE_CAT: ,  TYPE_SCHEM: ,  TYPE_NAME: ,  SELF_REFERENCING_COL_NAME: ,  REF_GENERATION:
        TABLE_CAT: null,  TABLE_SCHEM: public,  TABLE_NAME: document_heading_heading,  TABLE_TYPE: TABLE,  REMARKS: null,  TYPE_CAT: ,  TYPE_SCHEM: ,  TYPE_NAME: ,  SELF_REFERENCING_COL_NAME: ,  REF_GENERATION:
        TABLE_CAT: null,  TABLE_SCHEM: public,  TABLE_NAME: document_version,  TABLE_TYPE: TABLE,  REMARKS: null,  TYPE_CAT: ,  TYPE_SCHEM: ,  TYPE_NAME: ,  SELF_REFERENCING_COL_NAME: ,  REF_GENERATION:
        TABLE_CAT: null,  TABLE_SCHEM: public,  TABLE_NAME: document_version_attachment,  TABLE_TYPE: TABLE,  REMARKS: null,  TYPE_CAT: ,  TYPE_SCHEM: ,  TYPE_NAME: ,  SELF_REFERENCING_COL_NAME: ,  REF_GENERATION:
        TABLE_CAT: null,  TABLE_SCHEM: public,  TABLE_NAME: document_version_taacksec_user,  TABLE_TYPE: TABLE,  REMARKS: null,  TYPE_CAT: ,  TYPE_SCHEM: ,  TYPE_NAME: ,  SELF_REFERENCING_COL_NAME: ,  REF_GENERATION:
         */
        logInfo("getTables c: " + catalog + " s: " + schemaPattern + " t: " + tableNamePattern + " ts: " + Arrays.toString(types));
        currentResultSet = taackConnection.tables(catalog, schemaPattern, tableNamePattern);
        return currentResultSet;
    }

    @Override
    public ResultSet getSchemas() throws SQLException {
        logInfo("getSchemas");
        return getSchemas("%", "%");
    }
    private void createStringColumn(TaackResultSetOuterClass.TaackResultSet.Builder b, String name) {
        b.addColumns(
                TaackResultSetOuterClass.Column.newBuilder()
                        .setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING)
                        .setJavaTypeName(String.class.getName())
                        .setSqlType(12)
                        .setSqlTypeName("varchar")
                        .setName("TABLE_CAT")
        );
    }
    @Override
    public ResultSet getCatalogs() throws SQLException {
        // TABLE_CAT: crmauo
        logInfo("getCatalogs");
        TaackResultSetOuterClass.TaackResultSet.Builder b = TaackResultSetOuterClass.TaackResultSet.newBuilder();
        //b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("TABLE_CAT"));
        createStringColumn(b, "TABLE_CAT");
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue("default"));
        b.setCounter(1);
        var rs = new TaackResultSetJdbc(b.build());
        System.out.println("RS getCatalogs: \n" + TaackDriver.printRS(rs));
        return rs;
    }

    @Override
    public ResultSet getTableTypes() throws SQLException {
        logInfo("getTableTypes");
        TaackResultSetOuterClass.TaackResultSet.Builder b = TaackResultSetOuterClass.TaackResultSet.newBuilder();
        //b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("TABLE_TYPE"));
        createStringColumn(b, "TABLE_TYPE");
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue("TABLE"));
        b.setCounter(1);
        var rs = new TaackResultSetJdbc(b.build());
        System.out.println("RS getTableTypes: \n" + TaackDriver.printRS(rs));
        return rs;
    }

    @Override
    public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
        logInfo("getColumns c: " + catalog + " s: " + schemaPattern + " t: " + tableNamePattern + " c: " + columnNamePattern);
        // bug for lo return taackConnection.getColumn(catalog, schemaPattern, tableNamePattern);
        //return taackConnection.getColumns(schemaPattern, tableNamePattern, columnNamePattern);
        var rs = taackConnection.getColumnRSMetaData(tableNamePattern);
        //var rs = taackConnection.getColumns(schemaPattern, tableNamePattern, columnNamePattern);
        System.out.println("RS getColumns: \n" + TaackDriver.printRS(rs));
        return rs;
    }

    @Override
    public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern) throws SQLException {
        logInfo("getColumnPrivileges c: " + catalog + " s: " + schema + " t: " + table + " c: " + columnNamePattern);
        return null;
    }

    @Override
    public ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
        /*
        TABLE_CAT: null,  TABLE_SCHEM: public,  TABLE_NAME: action,  GRANTOR: postgres,  GRANTEE: postgres,  PRIVILEGE: DELETE,  IS_GRANTABLE: YES
        TABLE_CAT: null,  TABLE_SCHEM: public,  TABLE_NAME: action,  GRANTOR: postgres,  GRANTEE: postgres,  PRIVILEGE: INSERT,  IS_GRANTABLE: YES
        TABLE_CAT: null,  TABLE_SCHEM: public,  TABLE_NAME: action,  GRANTOR: postgres,  GRANTEE: postgres,  PRIVILEGE: REFERENCES,  IS_GRANTABLE: YES
        TABLE_CAT: null,  TABLE_SCHEM: public,  TABLE_NAME: action,  GRANTOR: postgres,  GRANTEE: postgres,  PRIVILEGE: SELECT,  IS_GRANTABLE: YES
        TABLE_CAT: null,  TABLE_SCHEM: public,  TABLE_NAME: action,  GRANTOR: postgres,  GRANTEE: postgres,  PRIVILEGE: TRIGGER,  IS_GRANTABLE: YES
        TABLE_CAT: null,  TABLE_SCHEM: public,  TABLE_NAME: action,  GRANTOR: postgres,  GRANTEE: postgres,  PRIVILEGE: TRUNCATE,  IS_GRANTABLE: YES
        TABLE_CAT: null,  TABLE_SCHEM: public,  TABLE_NAME: action,  GRANTOR: postgres,  GRANTEE: postgres,  PRIVILEGE: UPDATE,  IS_GRANTABLE: YES
         */
        logInfo("getTablePrivileges c: " + catalog + " s: " + schemaPattern + " t: " + tableNamePattern);
        return null;
    }

    @Override
    public ResultSet getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable) throws SQLException {
        // crash if scope == 0
        logInfo("getBestRowIdentifier c: " + catalog + " s: " + schema + " t: " + table + " s: " + scope + " n: " + nullable);
        return null;
    }

    @Override
    public ResultSet getVersionColumns(String catalog, String schema, String table) throws SQLException {
        //SCOPE: null,  COLUMN_NAME: ctid,  DATA_TYPE: 1111,  TYPE_NAME: tid,  COLUMN_SIZE: null,  BUFFER_LENGTH: null,  DECIMAL_DIGITS: null,  PSEUDO_COLUMN: 2
        logInfo("getVersionColumns c: " + catalog + " s: " + schema + " t: " + table);
        return null;
    }

    @Override
    public ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException {
        logInfo("getPrimaryKeys c: " + catalog + " s: " + schema + " t: " + table);
        var rs = taackConnection.pk(table);
        System.out.println("RS getPrimaryKeys: \n" + TaackDriver.printRS(rs));
        return rs;
    }

    @Override
    public ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException {
        /*
        pktable_cat: null,  pktable_schem: public,  pktable_name: address,  pkcolumn_name: id,  fktable_cat: null,  fktable_schem: public,  fktable_name: obstacle,  fkcolumn_name: address_id,  key_seq: 1,  update_rule: 3,  delete_rule: 3,  fk_name: fkdjaihmghxijk0ov2gov8sju0s,  pk_name: address_pkey,  deferrability: 7
        pktable_cat: null,  pktable_schem: public,  pktable_name: address,  pkcolumn_name: id,  fktable_cat: null,  fktable_schem: public,  fktable_name: pylon,  fkcolumn_name: address_id,  key_seq: 1,  update_rule: 3,  delete_rule: 3,  fk_name: fkl3kjs1k102hg4cx521ej3re4d,  pk_name: address_pkey,  deferrability: 7
        pktable_cat: null,  pktable_schem: public,  pktable_name: address,  pkcolumn_name: id,  fktable_cat: null,  fktable_schem: public,  fktable_name: contact_subscribedcms,  fkcolumn_name: address_id,  key_seq: 1,  update_rule: 3,  delete_rule: 3,  fk_name: fkl8fy03mwp66xrnk7qesep2vxl,  pk_name: address_pkey,  deferrability: 7
        pktable_cat: null,  pktable_schem: public,  pktable_name: address,  pkcolumn_name: id,  fktable_cat: null,  fktable_schem: public,  fktable_name: partner,  fkcolumn_name: address_id,  key_seq: 1,  update_rule: 3,  delete_rule: 3,  fk_name: fkmprivw72ex76bd8mtqiunmuw6,  pk_name: address_pkey,  deferrability: 7
        pktable_cat: null,  pktable_schem: public,  pktable_name: address,  pkcolumn_name: id,  fktable_cat: null,  fktable_schem: public,  fktable_name: warehouse,  fkcolumn_name: address_id,  key_seq: 1,  update_rule: 3,  delete_rule: 3,  fk_name: fkp7xymgre8vt94ihf75e9movyt,  pk_name: address_pkey,  deferrability: 7
        pktable_cat: null,  pktable_schem: public,  pktable_name: administrative_address,  pkcolumn_name: id,  fktable_cat: null,  fktable_schem: public,  fktable_name: address,  fkcolumn_name: administrative_address_id,  key_seq: 1,  update_rule: 3,  delete_rule: 3,  fk_name: fk176iw219bp2essefyn4ftufdu,  pk_name: administrative_address_pkey,  deferrability: 7
        pktable_cat: null,  pktable_schem: public,  pktable_name: administrative_address,  pkcolumn_name: id,  fktable_cat: null,  fktable_schem: public,  fktable_name: contact,  fkcolumn_name: address_administrative_address_id,  key_seq: 1,  update_rule: 3,  delete_rule: 3,  fk_name: fkre4rp5y77imnjw9em89lrs408,  pk_name: administrative_address_pkey,  deferrability: 7
         */
        logInfo("getImportedKeys c: " + catalog + " s: " + schema + " t: " + table);
        return null;
    }

    @Override
    public ResultSet getExportedKeys(String catalog, String schema, String table) throws SQLException {
        /*
        pktable_cat: null,  pktable_schem: public,  pktable_name: address,  pkcolumn_name: id,  fktable_cat: null,  fktable_schem: public,  fktable_name: obstacle,  fkcolumn_name: address_id,  key_seq: 1,  update_rule: 3,  delete_rule: 3,  fk_name: fkdjaihmghxijk0ov2gov8sju0s,  pk_name: address_pkey,  deferrability: 7
        pktable_cat: null,  pktable_schem: public,  pktable_name: address,  pkcolumn_name: id,  fktable_cat: null,  fktable_schem: public,  fktable_name: pylon,  fkcolumn_name: address_id,  key_seq: 1,  update_rule: 3,  delete_rule: 3,  fk_name: fkl3kjs1k102hg4cx521ej3re4d,  pk_name: address_pkey,  deferrability: 7
        pktable_cat: null,  pktable_schem: public,  pktable_name: address,  pkcolumn_name: id,  fktable_cat: null,  fktable_schem: public,  fktable_name: contact_subscribedcms,  fkcolumn_name: address_id,  key_seq: 1,  update_rule: 3,  delete_rule: 3,  fk_name: fkl8fy03mwp66xrnk7qesep2vxl,  pk_name: address_pkey,  deferrability: 7
        pktable_cat: null,  pktable_schem: public,  pktable_name: address,  pkcolumn_name: id,  fktable_cat: null,  fktable_schem: public,  fktable_name: partner,  fkcolumn_name: address_id,  key_seq: 1,  update_rule: 3,  delete_rule: 3,  fk_name: fkmprivw72ex76bd8mtqiunmuw6,  pk_name: address_pkey,  deferrability: 7
        pktable_cat: null,  pktable_schem: public,  pktable_name: address,  pkcolumn_name: id,  fktable_cat: null,  fktable_schem: public,  fktable_name: warehouse,  fkcolumn_name: address_id,  key_seq: 1,  update_rule: 3,  delete_rule: 3,  fk_name: fkp7xymgre8vt94ihf75e9movyt,  pk_name: address_pkey,  deferrability: 7
        pktable_cat: null,  pktable_schem: public,  pktable_name: administrative_address,  pkcolumn_name: id,  fktable_cat: null,  fktable_schem: public,  fktable_name: address,  fkcolumn_name: administrative_address_id,  key_seq: 1,  update_rule: 3,  delete_rule: 3,  fk_name: fk176iw219bp2essefyn4ftufdu,  pk_name: administrative_address_pkey,  deferrability: 7
        pktable_cat: null,  pktable_schem: public,  pktable_name: administrative_address,  pkcolumn_name: id,  fktable_cat: null,  fktable_schem: public,  fktable_name: contact,  fkcolumn_name: address_administrative_address_id,  key_seq: 1,  update_rule: 3,  delete_rule: 3,  fk_name: fkre4rp5y77imnjw9em89lrs408,  pk_name: administrative_address_pkey,  deferrability: 7
         */
        logInfo("getExportedKeys c: " + catalog + " s: " + schema + " t: " + table);
        return null;
    }

    @Override
    public ResultSet getCrossReference(String parentCatalog, String parentSchema, String parentTable, String foreignCatalog, String foreignSchema, String foreignTable) throws SQLException {
        /*
        pktable_cat: null,  pktable_schem: public,  pktable_name: taacksec_user,  pkcolumn_name: id,  fktable_cat: null,  fktable_schem: public,  fktable_name: sellable_item,  fkcolumn_name: user_last_updated_id,  key_seq: 1,  update_rule: 3,  delete_rule: 3,  fk_name: fkf4r951vdcay2nepwoqyccujb3,  pk_name: taacksec_user_pkey,  deferrability: 7
        pktable_cat: null,  pktable_schem: public,  pktable_name: taacksec_user,  pkcolumn_name: id,  fktable_cat: null,  fktable_schem: public,  fktable_name: table_header,  fkcolumn_name: user_last_updated_id,  key_seq: 1,  update_rule: 3,  delete_rule: 3,  fk_name: fkf52dwyqlmi4pq25a4bnp9235p,  pk_name: taacksec_user_pkey,  deferrability: 7
        pktable_cat: null,  pktable_schem: public,  pktable_name: taacksec_user,  pkcolumn_name: id,  fktable_cat: null,  fktable_schem: public,  fktable_name: article,  fkcolumn_name: user_last_updated_id,  key_seq: 1,  update_rule: 3,  delete_rule: 3,  fk_name: fkf5vk9jtdvl6h36c51f1kmdis7,  pk_name: taacksec_user_pkey,  deferrability: 7
        pktable_cat: null,  pktable_schem: public,  pktable_name: taacksec_user,  pkcolumn_name: id,  fktable_cat: null,  fktable_schem: public,  fktable_name: document_version_taacksec_user,  fkcolumn_name: user_id,  key_seq: 1,  update_rule: 3,  delete_rule: 3,  fk_name: fkfdwlfwrub3bkcwc4emwjeo6n9,  pk_name: taacksec_user_pkey,  deferrability: 7
        pktable_cat: null,  pktable_schem: public,  pktable_name: taacksec_user,  pkcolumn_name: id,  fktable_cat: null,  fktable_schem: public,  fktable_name: batch_process,  fkcolumn_name: user_created_id,  key_seq: 1,  update_rule: 3,  delete_rule: 3,  fk_name: fkfsjoeqywugk6p4kc7kmstdot1,  pk_name: taacksec_user_pkey,  deferrability: 7
         */
        logInfo("getCrossReference p: " + parentCatalog + " s: " + parentSchema + " t: " + parentTable + " fc: " + foreignCatalog + " fs: " + foreignSchema + " ft: " + foreignTable);
        return null;
    }

    @Override
    public ResultSet getTypeInfo() throws SQLException {
        logInfo("getTypeInfo");
        TaackResultSetOuterClass.TaackResultSet.Builder b = TaackResultSetOuterClass.TaackResultSet.newBuilder();
        b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("TYPE_NAME"));
        b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.INT).setName("DATA_TYPE"));
        b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.INT).setName("PRECISION"));
        b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("LITERAL_PREFIX"));
        b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("LITERAL_SUFFIX"));
        b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("CREATE_PARAMS"));
        b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.INT).setName("NULLABLE"));
        b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.BOOL).setName("CASE_SENSITIVE"));
        b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.BOOL).setName("SEARCHABLE"));
        b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.BOOL).setName("UNSIGNED_ATTRIBUTE"));
        b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.BOOL).setName("FIXED_PREC_SCALE"));
        b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.BOOL).setName("AUTO_INCREMENT"));
        b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("LOCAL_TYPE_NAME"));
        b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.INT).setName("MINIMUM_SCALE"));
        b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.INT).setName("MAXIMUM_SCALE"));
        b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.INT).setName("SQL_DATA_TYPE"));
        b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.INT).setName("SQL_DATETIME_SUB"));
        b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.INT).setName("NUM_PREC_RADIX"));

        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue("int8"));// TYPE_NAME
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(-5)); // DATA_TYPE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(0)); // PRECISION
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder()); // LITERAL_PREFIX
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder()); // LITERAL_SUFFIX
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder()); // CREATE_PARAMS
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setBoolValue(true)); // NULLABLE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setBoolValue(false)); // CASE_SENSITIVE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(typeSearchable)); // SEARCHABLE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setBoolValue(false)); // UNSIGNED_ATTRIBUTE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setBoolValue(false)); // FIXED_PREC_SCALE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setBoolValue(false)); // AUTO_INCREMENT
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder()); // LOCAL_TYPE_NAME
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(0)); // MINIMUM_SCALE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(0)); // MAXIMUM_SCALE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder()); // SQL_DATA_TYPE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder()); // SQL_DATETIME_SUB
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(10)); // NUM_PREC_RADIX

        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue("int4"));// TYPE_NAME
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(4)); // DATA_TYPE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(0)); // PRECISION
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder()); // LITERAL_PREFIX
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder()); // LITERAL_SUFFIX
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder()); // CREATE_PARAMS
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setBoolValue(true)); // NULLABLE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setBoolValue(false)); // CASE_SENSITIVE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(typeSearchable)); // SEARCHABLE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setBoolValue(false)); // UNSIGNED_ATTRIBUTE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setBoolValue(false)); // FIXED_PREC_SCALE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setBoolValue(false)); // AUTO_INCREMENT
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder()); // LOCAL_TYPE_NAME
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(0)); // MINIMUM_SCALE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(0)); // MAXIMUM_SCALE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder()); // SQL_DATA_TYPE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder()); // SQL_DATETIME_SUB
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(10)); // NUM_PREC_RADIX

        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue("bool"));// TYPE_NAME
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(-7)); // DATA_TYPE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(0)); // PRECISION
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue("'")); // LITERAL_PREFIX
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue("'")); // LITERAL_SUFFIX
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder()); // CREATE_PARAMS
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setBoolValue(true)); // NULLABLE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setBoolValue(false)); // CASE_SENSITIVE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(typeSearchable)); // SEARCHABLE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setBoolValue(true)); // UNSIGNED_ATTRIBUTE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setBoolValue(false)); // FIXED_PREC_SCALE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setBoolValue(false)); // AUTO_INCREMENT
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder()); // LOCAL_TYPE_NAME
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(0)); // MINIMUM_SCALE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(0)); // MAXIMUM_SCALE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder()); // SQL_DATA_TYPE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder()); // SQL_DATETIME_SUB
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(10)); // NUM_PREC_RADIX

        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue("varchar"));// TYPE_NAME
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(12)); // DATA_TYPE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(10485760)); // PRECISION
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue("'")); // LITERAL_PREFIX
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue("'")); // LITERAL_SUFFIX
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder()); // CREATE_PARAMS
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setBoolValue(true)); // NULLABLE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setBoolValue(true)); // CASE_SENSITIVE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(typeSearchable)); // SEARCHABLE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setBoolValue(true)); // UNSIGNED_ATTRIBUTE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setBoolValue(false)); // FIXED_PREC_SCALE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setBoolValue(false)); // AUTO_INCREMENT
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder()); // LOCAL_TYPE_NAME
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(0)); // MINIMUM_SCALE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(0)); // MAXIMUM_SCALE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder()); // SQL_DATA_TYPE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder()); // SQL_DATETIME_SUB
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(10)); // NUM_PREC_RADIX

        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue("numeric"));// TYPE_NAME
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(2)); // DATA_TYPE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(1000)); // PRECISION
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder()); // LITERAL_PREFIX
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder()); // LITERAL_SUFFIX
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder()); // CREATE_PARAMS
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setBoolValue(true)); // NULLABLE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setBoolValue(false)); // CASE_SENSITIVE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(typeSearchable)); // SEARCHABLE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setBoolValue(false)); // UNSIGNED_ATTRIBUTE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setBoolValue(false)); // FIXED_PREC_SCALE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setBoolValue(false)); // AUTO_INCREMENT
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder()); // LOCAL_TYPE_NAME
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(0)); // MINIMUM_SCALE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(1000)); // MAXIMUM_SCALE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder()); // SQL_DATA_TYPE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder()); // SQL_DATETIME_SUB
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(10)); // NUM_PREC_RADIX

        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue("timestamp"));// TYPE_NAME
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(93)); // DATA_TYPE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(6)); // PRECISION
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue("'")); // LITERAL_PREFIX
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue("'")); // LITERAL_SUFFIX
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder()); // CREATE_PARAMS
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setBoolValue(true)); // NULLABLE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setBoolValue(false)); // CASE_SENSITIVE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(typeSearchable)); // SEARCHABLE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setBoolValue(true)); // UNSIGNED_ATTRIBUTE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setBoolValue(false)); // FIXED_PREC_SCALE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setBoolValue(false)); // AUTO_INCREMENT
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder()); // LOCAL_TYPE_NAME
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(0)); // MINIMUM_SCALE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(0)); // MAXIMUM_SCALE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder()); // SQL_DATA_TYPE
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder()); // SQL_DATETIME_SUB
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(10)); // NUM_PREC_RADIX
        b.setCounter(6);
        var rs = new TaackResultSetJdbc(b.build());
        System.out.println("RS getTypeInfo: \n" + TaackDriver.printRS(rs));
        return rs;

    }

    @Override
    public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate) throws SQLException {
        logInfo("getIndexInfo c: " + catalog + " s: " + schema + " t: " + table + " u: " + unique + " a: " + approximate);
//        return taackConnection.indexInfoRSMetaData(table);
        var rs = taackConnection.indexInfo(table);
        System.out.println("RS getIndexInfo: \n" + TaackDriver.printRS(rs));
        return rs;
    }

    @Override
    public boolean supportsResultSetType(int type) throws SQLException {
        logInfo("supportsResultSetType t: " + type);
        return true;
    }

    @Override
    public boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException {
        logInfo("supportsResultSetConcurrency t: " + type + " c: " + concurrency);
        return false;
    }

    @Override
    public boolean ownUpdatesAreVisible(int type) throws SQLException {
        logInfo("ownUpdatesAreVisible t: " + type);
        return false;
    }

    @Override
    public boolean ownDeletesAreVisible(int type) throws SQLException {
        logInfo("ownDeletesAreVisible t: " + type);
        return false;
    }

    @Override
    public boolean ownInsertsAreVisible(int type) throws SQLException {
        logInfo("ownInsertsAreVisible t: " + type);
        return false;
    }

    @Override
    public boolean othersUpdatesAreVisible(int type) throws SQLException {
        logInfo("othersUpdatesAreVisible t: " + type);
        return false;
    }

    @Override
    public boolean othersDeletesAreVisible(int type) throws SQLException {
        logInfo("othersDeletesAreVisible t: " + type);
        return false;
    }

    @Override
    public boolean othersInsertsAreVisible(int type) throws SQLException {
        logInfo("othersInsertsAreVisible t: " + type);
        return false;
    }

    @Override
    public boolean updatesAreDetected(int type) throws SQLException {
        logInfo("updatesAreDetected t: " + type);
        return false;
    }

    @Override
    public boolean deletesAreDetected(int type) throws SQLException {
        logInfo("deletesAreDetected t: " + type);
        return false;
    }

    @Override
    public boolean insertsAreDetected(int type) throws SQLException {
        logInfo("insertsAreDetected t: " + type);
        return false;
    }

    @Override
    public boolean supportsBatchUpdates() throws SQLException {
        logInfo("supportsBatchUpdates");
        return false;
    }

    @Override
    public ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types) throws SQLException {
        /*
        type_cat: null,  type_schem: public,  type_name: item_range_inline_template,  class_name: null,  data_type: 2002,  remarks: null,  base_type: null
        type_cat: null,  type_schem: public,  type_name: item_range_value,  class_name: null,  data_type: 2002,  remarks: null,  base_type: null
        type_cat: null,  type_schem: public,  type_name: item_receipt,  class_name: null,  data_type: 2002,  remarks: null,  base_type: null
        type_cat: null,  type_schem: public,  type_name: item_related,  class_name: null,  data_type: 2002,  remarks: null,  base_type: null
        type_cat: null,  type_schem: public,  type_name: item_restricted_published_origins,  class_name: null,  data_type: 2002,  remarks: null,  base_type: null
         */
        logInfo("getUDTs c: " + catalog + " s: " + schemaPattern + " t: " + typeNamePattern + " ts: " + types);
        return null;
    }

    @Override
    public Connection getConnection() throws SQLException {
        logInfo("getConnection");
        return taackConnection;
    }

    @Override
    public boolean supportsSavepoints() throws SQLException {
        logInfo("supportsSavepoints");
        return false;
    }

    @Override
    public boolean supportsNamedParameters() throws SQLException {
        logInfo("supportsNamedParameters");
        return false;
    }

    @Override
    public boolean supportsMultipleOpenResults() throws SQLException {
        logInfo("supportsMultipleOpenResults");
        return false;
    }

    @Override
    public boolean supportsGetGeneratedKeys() throws SQLException {
        logInfo("supportsGetGeneratedKeys");
        return false;
    }

    @Override
    public ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern) throws SQLException {
        logInfo("getSuperTypes c: " + catalog + " s: " + schemaPattern + " t: " + typeNamePattern);
        throw new SQLFeatureNotSupportedException("Not implemented yet");
    }

    @Override
    public ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
        logInfo("getSuperTables c: " + catalog + " s: " + schemaPattern + " t: " + tableNamePattern);
        throw new SQLFeatureNotSupportedException("Not implemented yet");
    }

    @Override
    public ResultSet getAttributes(String catalog, String schemaPattern, String typeNamePattern, String attributeNamePattern) throws SQLException {
        logInfo("getAttributes c: " + catalog + " s: " + schemaPattern + " t: " + typeNamePattern + " a: " + attributeNamePattern);
        throw new SQLFeatureNotSupportedException("Not implemented yet");
    }

    @Override
    public boolean supportsResultSetHoldability(int holdability) throws SQLException {
        logInfo("supportsResultSetHoldability h: " + holdability);
        return false;
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        logInfo("getResultSetHoldability");
        return 0;
    }

    @Override
    public int getDatabaseMajorVersion() throws SQLException {
        logInfo("getDatabaseMajorVersion");
        return TaackDriver.INSTANCE.getMajorVersion();
    }

    @Override
    public int getDatabaseMinorVersion() throws SQLException {
        logInfo("getDatabaseMinorVersion");
        return TaackDriver.INSTANCE.getMinorVersion();
    }

    @Override
    public int getJDBCMajorVersion() throws SQLException {
        logInfo("getJDBCMajorVersion");
        return TaackDriver.INSTANCE.getMajorVersion();
    }

    @Override
    public int getJDBCMinorVersion() throws SQLException {
        logInfo("getJDBCMinorVersion");
        return TaackDriver.INSTANCE.getMinorVersion();
    }

    @Override
    public int getSQLStateType() throws SQLException {
        logInfo("getSQLStateType");
        return 0;
    }

    @Override
    public boolean locatorsUpdateCopy() throws SQLException {
        logInfo("locatorsUpdateCopy");
        return false;
    }

    @Override
    public boolean supportsStatementPooling() throws SQLException {
        logInfo("supportsStatementPooling");
        return false;
    }

    @Override
    public RowIdLifetime getRowIdLifetime() throws SQLException {
        logInfo("getRowIdLifetime");
        return null;
    }

    @Override
    public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException {
        /*
        table_schem: information_schema,  table_catalog: null
        table_schem: pg_catalog,  table_catalog: null
        table_schem: public,  table_catalog: null
         */

        logInfo("getSchemas c: " + catalog + " s: " + schemaPattern);

        TaackResultSetOuterClass.TaackResultSet.Builder b = TaackResultSetOuterClass.TaackResultSet.newBuilder();
//        b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("TABLE_SCHEM"));
        createStringColumn(b, "TABLE_SCHEM");
//        b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("TABLE_CATALOG"));
        createStringColumn(b, "TABLE_CATALOG");
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue("public"));
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue("default"));
        b.setCounter(1);
        var rs = new TaackResultSetJdbc(b.build());
        System.out.println("RS getSchemas: \n" + TaackDriver.printRS(rs));
        return rs;

    }

    @Override
    public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
        logInfo("supportsStoredFunctionsUsingCallSyntax");
        return false;
    }

    @Override
    public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
        logInfo("autoCommitFailureClosesAllResultSets");
        return false;
    }

    @Override
    public ResultSet getClientInfoProperties() throws SQLException {
        logInfo("getClientInfoProperties");
        return null;
    }

    @Override
    public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern) throws SQLException {
        logInfo("getFunctions c: " + catalog + " s: " + schemaPattern + " f: " + functionNamePattern);
        return null;
    }

    @Override
    public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern) throws SQLException {
        logInfo("getFunctionColumns c: " + catalog + " s: " + schemaPattern + " f: " + functionNamePattern + " c:" + columnNamePattern);
        throw new SQLFeatureNotSupportedException("Not implemented yet");
    }

    @Override
    public ResultSet getPseudoColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
        logInfo("getPseudoColumns c: " + catalog + " s: " + schemaPattern + " t: " + tableNamePattern + " c:" + columnNamePattern);
        return null;
    }

    @Override
    public boolean generatedKeyAlwaysReturned() throws SQLException {
        logInfo("generatedKeyAlwaysReturned");
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
