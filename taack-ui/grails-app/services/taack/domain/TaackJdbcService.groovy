package taack.domain

import grails.compiler.GrailsCompileStatic
import grails.util.Pair
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ConsoleErrorListener
import org.antlr.v4.runtime.tree.ParseTreeWalker
import org.grails.datastore.gorm.GormEntity
import org.grails.datastore.gorm.GormStaticApi
import org.grails.datastore.mapping.core.Datastore
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import taack.ast.type.FieldConstraint
import taack.ast.type.FieldInfo
import taack.ast.type.GetMethodReturn
import taack.jdbc.TaackANTLRErrorListener
import taack.jdbc.TaackJdbcError
import taack.jdbc.common.TaackResultSetOuterClass
import taack.jdbc.common.TaackResultSetOuterClass.TaackResultSet
import taack.jdbc.common.tql.gen.TQLLexer
import taack.jdbc.common.tql.gen.TQLParser
import taack.jdbc.common.tql.listener.TQLTranslator

import java.sql.DatabaseMetaData
/**
 * Service managing JDBC connection. Queries support TQL. It can be viewed as a subset of the HQL.
 * <p>Target supported features:
 * <ul>
 *     <li>dotted fields</li>
 *     <li>basic arithmetic</li>
 *     <li>wildcard support t.*</li>
 *     <li>very basic security check (WiP)</li>
 * </ul>
 * <p>Sample TQL Queries include:
 * <p>select u.* from User u;
 * <p>select t.userCreated.username from Task t;
 */
@GrailsCompileStatic
final class TaackJdbcService {

    @Autowired
    SessionFactory sessionFactory

    @Autowired
    Datastore datastore

    private class ColumnDesc {
        final FieldInfo fieldInfo
        final GetMethodReturn methodReturn
        final String alias
        final String colName

        ColumnDesc(FieldInfo fieldInfo, String alias, String colName) {
            this.fieldInfo = fieldInfo
            this.alias = alias
            this.colName = colName
            this.methodReturn = null
        }

        ColumnDesc(GetMethodReturn methodReturn, String alias, String colName) {
            this.fieldInfo = null
            this.alias = alias
            this.colName = colName
            this.methodReturn = methodReturn
        }

        Class getFieldType() {
            if (methodReturn)
                return methodReturn.method.returnType

            if (fieldInfo == null) return BigDecimal
            if (!colName.contains('.')) fieldInfo.fieldConstraint.field.type
            else {
                def colDatum = colName.split('.')
                if (fieldInfo.fieldConstraint.field.type.isAssignableFrom(GormEntity) && colDatum.size() > 1) {
                    def f1 = fieldInfoMap[fieldInfo.fieldConstraint.field.type]?.find {
                        it.fieldName == colDatum[1]
                    }
                    if (!f1) return String
                    if (!fieldInfo.fieldConstraint.field.type.isAssignableFrom(GormEntity)) return f1.fieldConstraint.field.type
                    else if (colDatum.size() > 2) {
                        def f2 = fieldInfoMap[f1.fieldConstraint.field.type]?.find {
                            it.fieldName == colDatum[2]
                        }
                        if (!f2) return String
                        if (!f2.fieldConstraint.field.type.isAssignableFrom(GormEntity)) return f2.fieldConstraint.field.type
                        else if (colDatum.size() > 3) {
                            def f3 = fieldInfoMap[f2.fieldConstraint.field.type]?.find {
                                it.fieldName == colDatum[3]
                            }
                            if (!f3) return String
                            if (!f3.fieldConstraint.field.type.isAssignableFrom(GormEntity)) return f3.fieldConstraint.field.type
                        }
                    }
                }
                return String
            }
        }
    }
    private final static Map<Class<? extends GormEntity>, FieldInfo[]> fieldInfoMap = [:]
    private final static Map<Class<? extends GormEntity>, GetMethodReturn[]> methodMap = [:]
    private final static Map<String, Class<? extends GormEntity>> gormClassesMap = [:]
    private final static Map<String, Class<? extends GormEntity>> gormRealClassesMap = [:]

    /**
     * Allow to register a GormEntity class as base class for TQL queries.
     *
     * <pre>{@code
     *  @PostConstruct
     *  private static void init() {
     *      def u = new User()
     *      Jdbc.registerClass( User,
     *                          u.username_, u.mail_, u.mainSubsidiary_, u.firstName_,
     *                          u.lastName_, u.businessUnit_, u.enabled_)
     *   }
     * }</pre>
     */
    final static class Jdbc {
        static final void registerClassProperties(Class<? extends GormEntity> aClass, FieldInfo... fieldInfos) {
            registerJdbcClass(aClass, fieldInfos)
        }

        static final void registerClassGetters(Class<? extends GormEntity> aClass, GetMethodReturn... methodReturns) {
            registerJdbcClass(aClass, methodReturns)
        }

        static final Map<Class<? extends GormEntity>, FieldInfo[]> getFieldInfoMap() {
            TaackJdbcService.fieldInfoMap
        }
    }

    final static void registerJdbcClass(Class<? extends GormEntity> aClass, FieldInfo... fieldInfos) {
        def fieldInfoWithId = fieldInfos.toList()
        fieldInfoWithId.add 0, new FieldInfo(new FieldConstraint(new FieldConstraint.Constraints("", false, false, null, null), aClass.getDeclaredField('id'), null), 'id', (Long) 0)
        fieldInfoMap.put(aClass, fieldInfoWithId as FieldInfo[])
        gormClassesMap.put(aClass.simpleName, aClass)
        gormRealClassesMap.put(aClass.name, aClass)
    }

    final static void registerJdbcClass(Class<? extends GormEntity> aClass, GetMethodReturn... methodReturns) {
        methodMap.put(aClass, methodReturns)
        gormClassesMap.put(aClass.simpleName, aClass)
        gormRealClassesMap.put(aClass.name, aClass)
    }

    private static final String hqlFromTranslator(TQLTranslator translator, boolean count = false) {
        StringBuffer out = new StringBuffer(512)
        if (!count) {
            if (!translator.columns?.isEmpty()) {
                out.append('select ')
                out.append(translator.selectClause)
            }
        } else {
            if (translator.groupByClause) {
                out.append("select count(${translator.groupByClauseColumns}) ")
            } else {
                if (translator.tables.size() == 1) out.append('select count(distinct id) ')
                else out.append('select count(*)')// + translator.tabAliasMap[translator.tables.last()] + '.id) ')
            }
        }
        out.append(' from ')
        if (!count) out.append(translator.fromClause)
        else out.append(translator.fromClause)
        if (translator.whereClause) out.append(translator.whereClause)
        if (!count && translator.groupByClause) out.append(translator.groupByClause)
        println "HQL: ${out.toString()}"
        out.toString()
    }

    static final TQLTranslator translatorFromTql(String tql) throws TaackJdbcError {
        def lexer = new TQLLexer(CharStreams.fromString(tql))
        def errors = new TaackANTLRErrorListener()
        lexer.removeErrorListener(ConsoleErrorListener.INSTANCE)
        lexer.addErrorListener(errors)
        def tokens = new CommonTokenStream(lexer)
        def parser = new TQLParser(tokens)
        parser.removeErrorListener(ConsoleErrorListener.INSTANCE)
        parser.addErrorListener(errors)
        def tree = parser.tql()
        def walker = new ParseTreeWalker()
        def translator = new TQLTranslator(tql)
        walker.walk(translator, tree)
        translator
    }

    final TaackResultSet protoFromTranslator(TQLTranslator translator, Integer maxRow, int offset) {
        def b = TaackResultSet.newBuilder()
        b.offset = offset
        b.max = maxRow == null ? offset : maxRow

        List<ColumnDesc> columnDescs = []
        def simpleName = translator.tables.first()
        if (translator.isStar()) {
            for (def f in fieldInfoMap[gormClassesMap[simpleName]]) columnDescs << new ColumnDesc(f, f.fieldName, f.fieldName)
            translator.columns.removeAll { true }
            translator.addColumnNames(fieldInfoMap[gormClassesMap[simpleName]]*.fieldName as List<String>, "t")
            translator.tabAliasMap.put(simpleName, "t")
        } else {
            List<TQLTranslator.Col> columns = []
            columns.addAll translator.columns
            columns.each { TQLTranslator.Col col ->
                def ct = col.colType
                def cn = translator.getUnaliasedColumnName(col.colName)
                def sn = simpleName
                def alias = translator.getAliasFromColumnName(col.colName)
                if (alias) {
                    sn = translator.tabAliasMap.find { it.value == alias }.key
                }
                def gec = gormClassesMap[sn]
                if (!gec) throw new TaackJdbcError("From clause analysis", "No table ${sn}, avalaible tables ${gormClassesMap.keySet()}")
                def f = fieldInfoMap[gec].find { cn.startsWith(it.fieldName) }
                if (f) {
                    columnDescs << new ColumnDesc(f, translator.colAliasMap[col], cn)
                } else if (!f && ct == TQLTranslator.Col.Type.FORMULA) {
                    // Complex case like t.* or *
//                    def fi = new FieldInfo(new FieldConstraint(new FieldConstraint.Constraints("", false, false, null, null), null, null), 'id', (BigDecimal) 0)
                    columnDescs << new ColumnDesc(null as FieldInfo, translator.colAliasMap[col], col.colName)
                } else if (translator.isStar()) {
                    for (def fi in fieldInfoMap[gec]) columnDescs << new ColumnDesc(fi, fi.fieldName, fi.fieldName)
                    translator.columns.removeAll { true }
                    translator.addColumnNames(fieldInfoMap[gec]*.fieldName as List<String>, "t")
                    translator.tabAliasMap.put(sn, "t")
                } else if (translator.hasAliasedStar()) {
                    for (def fi in fieldInfoMap[gec]) columnDescs << new ColumnDesc(fi, fi.fieldName, fi.fieldName)
                    translator.columns.removeAll { true }
                    translator.addColumnNames(fieldInfoMap[gec]*.fieldName as List<String>, alias)
                } else {
                    def m = methodMap[gec].find { (cn == it.method.getName().substring(3).uncapitalize()) }
                    if (m) {
                        columnDescs << new ColumnDesc(m, translator.colAliasMap[col], cn)
                        col.colType = TQLTranslator.Col.Type.GETTER
                    } else
                    throw new TaackJdbcError("Select clause analysis", "No column ${col.colName}, avalaible columns ${fieldInfoMap[gec]*.fieldName as List<String>}")
                }
            }
        }
        int colNumber = 1

        def r = listFromTranslator(translator, maxRow == 0 || maxRow == null ? null : maxRow, offset)
        b.counter = r.bValue

        for (def columnDesc : columnDescs) {
            def c = TaackResultSetOuterClass.Column.newBuilder()
            c.name = columnDesc.alias ?: columnDesc.colName
            c.colNumber = colNumber++
            c.setIsNullable(columnDesc.fieldInfo?.fieldConstraint?.nullable ?: false)
            c.setTableName(simpleName)

            switch (columnDesc.fieldType) {
                case String:
                    c.javaType = TaackResultSetOuterClass.Column.JavaType.STRING
                    c.sqlType = 12
                    c.sqlTypeName = 'varchar'
                    c.displaySize = 255
                    c.precision = 0
                    c.javaTypeName = String.name
                    break
                case Date:
                    c.javaType = TaackResultSetOuterClass.Column.JavaType.DATE
                    c.sqlType = 93
                    c.sqlTypeName = 'timestamp'
                    c.displaySize = 29
                    c.precision = 6
                    c.javaTypeName = Date.name
                    break
                case Long:
                    c.javaType = TaackResultSetOuterClass.Column.JavaType.LONG
                    c.sqlType = -5
                    c.sqlTypeName = 'int8'
                    c.displaySize = 19
                    c.precision = 0
                    c.javaTypeName = Long.name
                    break
                case BigDecimal:
                    c.javaType = TaackResultSetOuterClass.Column.JavaType.BIG_DECIMAL
                    c.sqlType = 2
                    c.sqlTypeName = 'numeric'
                    c.displaySize = 19
                    c.precision = 6
                    c.javaTypeName = BigDecimal.name
                    break
                case Boolean:
                    c.javaType = TaackResultSetOuterClass.Column.JavaType.BOOL
                    c.sqlType = -7
                    c.sqlTypeName = 'bool'
                    c.displaySize = 1
                    c.precision = 0
                    c.javaTypeName = Boolean.name
                    break
                case Byte:
                    c.javaType = TaackResultSetOuterClass.Column.JavaType.BYTE
                    c.sqlType = 4
                    c.sqlTypeName = 'int4'
                    c.displaySize = 10
                    c.precision = 0
                    c.javaTypeName = Integer.name
                    break
                case Short:
                    c.javaType = TaackResultSetOuterClass.Column.JavaType.SHORT
                    c.sqlType = 4
                    c.sqlTypeName = 'int4'
                    c.displaySize = 10
                    c.precision = 0
                    c.javaTypeName = Integer.name
                    break
                case Integer:
                    c.javaType = TaackResultSetOuterClass.Column.JavaType.INT
                    c.sqlType = 4
                    c.sqlTypeName = 'int4'
                    c.displaySize = 10
                    c.precision = 0
                    c.javaTypeName = Integer.name
                    break
                case Byte[]:
                    c.javaType = TaackResultSetOuterClass.Column.JavaType.BYTES
                    break
                default: // Default to String
                    c.javaType = TaackResultSetOuterClass.Column.JavaType.STRING
                    c.sqlType = 12
                    c.sqlTypeName = 'varchar'
                    c.displaySize = 255
                    c.precision = 0
                    c.javaTypeName = String.name
            }
            b.addColumns(c)
        }

        if (maxRow == null) {
            b.counter = 0
            return b.build()
        }

        def itRow = r.aValue
        for (def row in itRow) {
            def itColList = b.columnsList.iterator()
            if (row instanceof String) { // only 1 column
                def c = TaackResultSetOuterClass.Cell.newBuilder()
                c.setStringValue(row as String)
                b.addCells(c)
            } else
                for (def cell : row) {
                    if (!itColList.hasNext()) itColList = b.columnsList.iterator()
                    def c = TaackResultSetOuterClass.Cell.newBuilder()
                    def col = itColList.next()

                    switch (col.javaType) {
                        case TaackResultSetOuterClass.Column.JavaType.DATE:
                            if (cell) c.dateValue = (cell as Date).time
                            break
                        case TaackResultSetOuterClass.Column.JavaType.LONG:
                            if (cell) c.longValue = cell as Long
                            break
                        case TaackResultSetOuterClass.Column.JavaType.BIG_DECIMAL:
                            if (cell) c.setBigDecimal(cell as String)
                            break
                        case TaackResultSetOuterClass.Column.JavaType.STRING:
                            if (cell) c.setStringValue(cell as String)
                            break
                        case TaackResultSetOuterClass.Column.JavaType.BOOL:
                            if (cell) c.boolValue = cell as Boolean
                            break
                        case TaackResultSetOuterClass.Column.JavaType.BYTE:
                            if (cell) c.byteValue = cell as Byte
                            break
                        case TaackResultSetOuterClass.Column.JavaType.SHORT:
                            if (cell) c.shortValue = cell as Short
                            break
                        case TaackResultSetOuterClass.Column.JavaType.INT:
                            if (cell) c.intValue = cell as Integer
                            break
                        case TaackResultSetOuterClass.Column.JavaType.BYTES:
//                        c.setBytesValue()
                            break
                        default:
                            println "AUOAUOAOU2 default ${col}"

                    }
                    b.addCells(c)
                }
        }
        return b.build()
    }


    final Pair<List<Object[]>, Long> listFromTranslator(TQLTranslator translator, Integer max, int offset) {
        def simpleName = translator.tables.first()
        Class<? extends GormEntity> aClass = gormClassesMap[simpleName]
        TaackFilter tf = new TaackFilter.FilterBuilder<>(aClass, sessionFactory, null).build()
        def res = tf.executeQuery(hqlFromTranslator(translator), [:], max, offset)
        if (methodMap[aClass]) {
            List<Integer> mi = []
            List<String> ni = []
            GetMethodReturn[] m = methodMap[aClass]
            translator.columns.eachWithIndex{ TQLTranslator.Col entry, int i ->
                if (entry.colType == TQLTranslator.Col.Type.GETTER) {
                    mi << i
                    ni << translator.colAliasMap[entry]
                }
            }
            res.each { line ->
                mi.eachWithIndex { index, i ->
                    Long o = line[index] as Long
                    GetMethodReturn mo = m[mi[i]]
                    def aObject = new GormStaticApi(aClass, datastore, null).read(o)
                    String ov = mo.method.invoke(aObject)
                    line[index] = ov
                }
            }

        }
        def num = tf.executeQueryUniqueResult(Long, [:], hqlFromTranslator(translator, true)) as Long
        log.info "AUO num: ${num}, #ofRes: ${res.size()}"
        return new Pair<List<Object[]>, Long>(res, num)
    }

    final private TaackResultSet protoFromTql(String tql, Integer max, int offset) {
        protoFromTranslator(translatorFromTql(tql), max, offset)
    }

    final private static void addTableToResultSet(TaackResultSet.Builder b, String name) {
        if (gormClassesMap.containsKey(name)) {
            b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue('default'))
            b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue('public'))
            b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue(name))
            b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue('TABLE'))
            b.addCells(TaackResultSetOuterClass.Cell.newBuilder())
            b.addCells(TaackResultSetOuterClass.Cell.newBuilder())
            b.addCells(TaackResultSetOuterClass.Cell.newBuilder())
            b.addCells(TaackResultSetOuterClass.Cell.newBuilder())
            b.addCells(TaackResultSetOuterClass.Cell.newBuilder())
            b.addCells(TaackResultSetOuterClass.Cell.newBuilder())
        }
    }
    final private static TaackResultSet getTables(String schemasPattern, String tableNamePattern) {
        if (!schemasPattern || schemasPattern.trim().empty || schemasPattern == 'public') {
            TaackResultSet.Builder b = TaackResultSet.newBuilder()
            b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("TABLE_CAT"))
            b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("TABLE_SCHEM"))
            b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("TABLE_NAME"))
            b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("TABLE_TYPE"))
            b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("REMARKS"))
            b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("TYPE_CAT"))
            b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("TYPE_SCHEM"))
            b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("TYPE_NAME"))
            b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("SELF_REFERENCING_COL_NAME"))
            b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("REF_GENERATION"))

            if (!tableNamePattern || tableNamePattern == '%') {
                int i = 0;
                for (def k in gormClassesMap.keySet()) {
                    addTableToResultSet(b, k)
                    i++
                }
                b.counter = i
            } else if (tableNamePattern.contains('%')) {
                return b.build()
            } else {
                addTableToResultSet(b, tableNamePattern)
                b.counter = 1
            }
            b.offset = 0
            return b.build()
        }
        return null
    }

    final private static void addColumnToResultSet(TaackResultSet.Builder b, String name, FieldInfo col, int ordinal) {
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue('default'))
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue('public'))
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue(name))
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue(col.fieldName))
        int charOctetLength = 1
        switch (col.fieldConstraint.field.type) {
            case String:
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(12)) // DATA_TYPE
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue("varchar")) // TYPE_NAME
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(255)) // COLUMN_SIZE
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder()) // BUFFER_LENGTH
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(0)) // DECIMAL_DIGITS
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(10)) // NUM_PREC_RADIX
                charOctetLength = 255
                break
            case Date:
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(93))
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue("timestamp"))
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(29))
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder())
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(6))
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(10))
                charOctetLength = 29
                break
            case Long:
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(-5))
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue("int8"))
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(19))
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder())
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(0))
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(10))
                charOctetLength = 19
                break
            case BigDecimal:
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(2))
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue("numeric"))
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(19))
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder())
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(6))
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(10))
                charOctetLength = 19
                break
            case Boolean:
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(-7))
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue("bool"))
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(1))
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder())
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(0))
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(10))
                break
            case Byte:
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(4))
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue("int4"))
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(10))
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder())
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(0))
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(10))
                break
            case Short:
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(4))
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue("int4"))
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(10))
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder())
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(0))
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(10))
                break
            case Integer:
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(4))
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue("int4"))
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(10))
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder())
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(0))
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(10))
                charOctetLength = 19
                break
            case Byte[]:
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder())
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder())
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder())
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder())
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder())
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder())
                break
            default:
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(12)) // DATA_TYPE
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue("varchar")) // TYPE_NAME
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(255)) // COLUMN_SIZE
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder()) // BUFFER_LENGTH
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(0)) // DECIMAL_DIGITS
                b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(10)) // NUM_PREC_RADIX
                charOctetLength = 255
        }
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(col.fieldConstraint.nullable ? 1 : 0))
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder())
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder())
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder())
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder())
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(charOctetLength))
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setIntValue(ordinal))
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue(col.fieldConstraint.nullable ? "YES" : "NO"))
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder())
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder())
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder())
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder())
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue("NO"))
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue("NO"))
    }

    final private static int addTableColumnsToResultSet(TaackResultSet.Builder b, String name, String columnPattern) {
        if (gormClassesMap.containsKey(name)) {
            def columns = fieldInfoMap[gormClassesMap[name]]
            int counter = 0
            columns.eachWithIndex { FieldInfo entry, int i ->
                counter++
                addColumnToResultSet(b, name, entry, i)
            }
            return counter
        }
        return 0
    }

    final private static TaackResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) {
        if (schemaPattern == null || schemaPattern.isEmpty() || schemaPattern == 'public' || schemaPattern == '%') {
            TaackResultSet.Builder b = TaackResultSet.newBuilder()
            b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("TABLE_CAT"))
            b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("TABLE_SCHEM"))
            b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("TABLE_NAME"))
            b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("COLUMN_NAME"))
            b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.INT).setName("DATA_TYPE"))
            b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("TYPE_NAME"))
            b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.INT).setName("COLUMN_SIZE"))
            b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("BUFFER_LENGTH"))
            b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.INT).setName("DECIMAL_DIGITS"))
            b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.INT).setName("NUM_PREC_RADIX"))
            b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.INT).setName("NULLABLE"))
            b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("REMARKS"))
            b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("COLUMN_DEF"))
            b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.INT).setName("SQL_DATA_TYPE"))
            b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.INT).setName("SQL_DATETIME_SUB"))
            b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("CHAR_OCTET_LENGTH"))
            b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.INT).setName("ORDINAL_POSITION"))
            b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("IS_NULLABLE"))
            b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("SCOPE_CATALOG"))
            b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("SCOPE_SCHEMA"))
            b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("SCOPE_TABLE"))
            b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.INT).setName("SOURCE_DATA_TYPE"))
            b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("IS_AUTOINCREMENT"))
            b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("IS_GENERATEDCOLUMN"))
            int counter = 0
            if (tableNamePattern == null || tableNamePattern.isEmpty() || tableNamePattern == '%') {
                gormClassesMap.keySet().each {
                    counter += addTableColumnsToResultSet(b, it, columnNamePattern)
                }
            } else if (!tableNamePattern.contains('%')) {
                counter += addTableColumnsToResultSet(b, tableNamePattern, columnNamePattern)
            }
            b.offset = 0
            b.counter = counter
            return b.build()
        }
        return null
    }

    final byte[] getProtoColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) {
        def p = getColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern)
        p.toByteArray()
    }

    final byte[] getProtoTables(String schemaPattern, String tableNamePattern) {
        def p = getTables(schemaPattern, tableNamePattern)
        p.toByteArray()
    }


    final byte[] getBufFromTql(String tql, Integer max, int offset) {
        try {
            def p = protoFromTranslator(translatorFromTql(tql), max, offset)
            p.toByteArray()
        } catch(TaackJdbcError taackJdbcError) {
            def b = TaackResultSet.newBuilder()
            def e = TaackResultSetOuterClass.ProcessingError.newBuilder()
            e.processingStep = taackJdbcError.errorStep
            e.errorMessage = taackJdbcError.msg
            b.setProcessingError(e)
            b.build().toByteArray()
        }
    }

    final byte[] getPingMessage() {
        TaackResultSet.Builder b = TaackResultSet.newBuilder()
        b.counter = 0
        b.offset = 0
        b.max = 100
        b.build().toByteArray()
    }

    final byte[] getPrimaryKey(String table) {
        TaackResultSet.Builder b = TaackResultSet.newBuilder()
        b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("TABLE_CAT"))
        b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("TABLE_SCHEM"))
        b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("TABLE_NAME"))
        b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("COLUMN_NAME"))
        b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.SHORT).setName("KEY_SEQ"))
        b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("PK_NAME"))

        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue("default"))
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue("public"))
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue(table))
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue("id"))
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setShortValue(1))
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder())

        b.offset = 0
        b.max = 1
        b.counter = 1
        b.build().toByteArray()
    }

    final byte[] getIndexInfo(String table) {
        TaackResultSet.Builder b = TaackResultSet.newBuilder()
        b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("TABLE_CAT"))
        b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("TABLE_SCHEM"))
        b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("TABLE_NAME"))
        b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.BOOL).setName("NON_UNIQUE"))
        b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("INDEX_QUALIFIER"))
        b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("INDEX_NAME"))
        b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.SHORT).setName("TYPE"))
        b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.SHORT).setName("ORDINAL_POSITION"))
        b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("COLUMN_NAME"))
        b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("ASC_OR_DESC"))
        b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.LONG).setName("CARDINALITY"))
        b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.LONG).setName("PAGES"))
        b.addColumns(TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.STRING).setName("FILTER_CONDITION"))

        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue("default"))
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue("public"))
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue(table))
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setBoolValue(false))
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder())
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder())
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setShortValue(DatabaseMetaData.tableIndexStatistic))
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setShortValue(0))
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setStringValue("id"))
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder())

        TaackFilter tf = new TaackFilter.FilterBuilder<>(GormEntity, sessionFactory, null).build()
        def count = tf.executeQueryUniqueResult(Long, [:], "select count(id) from ${table}") as Long
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setLongValue(count)) // number of rows in the table
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder().setLongValue(10)) // number of pages in the table
        b.addCells(TaackResultSetOuterClass.Cell.newBuilder())
        b.offset = 0
        b.max = 1
        b.counter = 1
        b.build().toByteArray()
    }

    final byte[] getIndexInfoRSMetaData(String table) {
        TaackResultSet.Builder b = TaackResultSet.newBuilder()
        TaackResultSetOuterClass.Column.Builder cId = TaackResultSetOuterClass.Column.newBuilder().setJavaType(TaackResultSetOuterClass.Column.JavaType.LONG).setName('id')
        cId.setColNumber(1)
        cId.setSqlType(-5)
        cId.setSqlTypeName("int8")
        cId.setTableName(table)
        cId.setScale(0)
        cId.setIsNullable(false)
        cId.setJavaTypeName(Long.name)
        cId.setDisplaySize(10)
        cId.setPrecision(0)
        b.addColumns(cId)
        b.build().toByteArray()
    }

    final static Map<Class<? extends GormEntity>, FieldInfo[]> getFieldInfoMapDesc() {
        fieldInfoMap
    }

    final static FieldInfo[] gormFields(String className) {
        fieldInfoMap[gormRealClassesMap[className]]
    }
}
