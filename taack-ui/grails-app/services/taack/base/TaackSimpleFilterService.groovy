package taack.base

import grails.util.Pair
import grails.web.api.WebAttributes
import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity
import org.hibernate.SessionFactory
import org.hibernate.query.Query
import taack.ast.type.FieldInfo
import taack.ast.type.GetMethodReturn
import taack.ui.base.UiFilterSpecifier
import taack.ui.base.UiTableSpecifier
import taack.ui.base.filter.ITaackFilterExtension
import taack.ui.base.filter.UiFilterVisitorImpl
import taack.ui.base.filter.expression.FilterExpression
import taack.ui.base.filter.expression.Operator
import taack.ui.base.table.ColumnHeaderFieldSpec
import taack.ui.dump.RawHtmlFilterDump
import taack.ui.dump.RawMapFilterDump
import taack.utils.DateFormat

import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType
/**
 * Service allowing to automatically filter data in a tableFilter. It is typically
 * used in a table block. It uses params given from the {@link UiFilterSpecifier} to filter data.
 *
 * <p>The main methods are named {@link TaackSimpleFilterService#list(Class < T >)}. The uniq mandatory
 * parameter is the Class we want the list for. Optional params includes allowed ids or passing a filter directly
 * allow to restrict the list of objects returned.
 */
@CompileStatic
class TaackSimpleFilterService implements WebAttributes {
    SessionFactory sessionFactory

    final static Map<Class, ITaackFilterExtension> filterExtensionMap = [:]

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
    final static class TaackSimpleFilterExtension {
        static final void registerStringExt(Class<? extends GormEntity> aClass, ITaackFilterExtension filterExtension) {
            filterExtensionMap[aClass] = filterExtension
        }
    }

    private Map<String, JoinEntity> joinEntityMap = [:]
    private int joinEntityOrder = 0

    private final class JoinEntity {
        final String joinName
        final int order

        JoinEntity(String jn, int o) {
            joinName = jn
            order = o
        }

        JoinEntity getParent() {
            String tmp = joinName.contains('.') ? joinName.substring(0, joinName.lastIndexOf('.')) : joinName
            joinEntityMap[tmp]
        }

        Collection<JoinEntity> getChildren(boolean direct = false) {
            joinEntityMap.findAll { Map.Entry<String, JoinEntity> e ->
                boolean isChildren = e.key.startsWith(joinName) && e.key != joinName
                if (direct && isChildren) isChildren = (e.key - joinName).count('.') == 1
                isChildren
            }?.values()
        }
    }

    private final Collection<JoinEntity> getRoots() {
        joinEntityMap.findAll {
            it.key.count('.') == 0
        }?.values()
    }

    private final void addJoinEntity(String path) {
        if (!path.contains('.')) return

        String tmp = ''
        String last = path.substring(path.lastIndexOf('.') + 1)
        for (String it in path.split('\\.')) {
            if (it == last) return
            String joinName = tmp + (tmp.isEmpty() ? '' : '.') + it
            tmp = joinName
            JoinEntity joinEntity = joinEntityMap[joinName]
            if (!joinEntity) {
                joinEntity = new JoinEntity(joinName, joinEntityOrder++)
                joinEntityMap.put(joinName, joinEntity)
            }
        }
    }

    private static String removeBrackets(String join) {
        return join.replaceAll("\\[[0-9]?\\]", '')
    }

    private final JoinEntity getJoinEntity(String path) {
        if (!path.contains('.')) return null
        joinEntityMap[path.substring(0, path.lastIndexOf('.'))]
    }

    private final StringBuffer buildJoinClause(StringBuffer join = null, JoinEntity joinEntity = null) {
        Collection<JoinEntity> joins
        if (!join) {
            join = new StringBuffer()
        }
        if (!joinEntity) joins = getRoots()
        else joins = joinEntity.getChildren(true)

        joins.each {
            String path = it.order == 0 ? it.joinName : it.joinName.substring(it.joinName.lastIndexOf('.') + 1)
            join.append(" join sc${joinEntity ? joinEntity.order : ''}.${path} sc${it.order} ")
            buildJoinClause(join, it)
        }
        join
    }

    private final static Field getTheField(Class aClass, String key) {
        Field f = null
        try {
            removeBrackets(key).tokenize('.').each { token ->
                f = aClass.declaredFields.find { it.name == token }
                if (f == null) {
                    f = aClass.superclass.declaredFields.find { it.name == token }
                }
                if (Collection.isAssignableFrom(f.type)) {
                    aClass = Class.forName((f.genericType as ParameterizedType).getActualTypeArguments()[0].typeName)
                } else {
                    aClass = f.type
                }
            }
        } catch (ignore) {
            println "getTheField exception $ignore"
        }
        f
    }

    /**
     * Helper method allowing to execute a query
     * @param query
     * @param max max number of results
     * @param offset
     * @return the list of objects
     */
    final List<Object[]> executeQuery(final String query, final Map<String, Object> namedParams, final Integer max = null, final Integer offset = null) {
        Query q = sessionFactory.currentSession.createQuery(query, Object[])
        namedParams.each { q.setParameter(it.key, it.value) }
        if (max != null) q.maxResults = max
        if (offset) q.firstResult = offset
        q.resultList
    }

    /**
     * Helper method allowing to execute a query, after a type, returning an uniq result.
     * @param aClass
     * @param query
     * @param max
     * @param offset
     * @return
     */
    final <T> T executeQueryUniqueResult(final Class<T> aClass, Map<String, Object> namedParams, final String query, final Integer max = null, final Integer offset = null) {
        Query<T> q = sessionFactory.currentSession.createQuery(query, aClass)
        namedParams.each { q.setParameter(it.key, it.value) }
        if (max) q.maxResults = max
        if (offset) q.firstResult = offset
        q.uniqueResult()
    }

    private final static List<String> filterMeta = ['offset', 'max', 'sort', 'order', 'grouping']

    private static String escapeHqlParameter(final String hqlParam) {
        hqlParam?.replace("'", "''")
    }

    /**
     * list entities and number of results
     *
     * @param aClass type we want to list
     * @param max (optional) max number of results
     * @param sortableDirection (optional) sort parameter
     * @param tInstance object of the same class as aClass, allowing to add filter criteria
     * @return pair that contains the max results and the total number of objects reached by the filter
     */
    final <T extends GormEntity> Pair<List<T>, Long> list(final Class<T> aClass, final int max = 20, final ColumnHeaderFieldSpec.SortableDirection sortableDirection, final T tInstance = null) {
        list(aClass, max, null, tInstance, sortableDirection, null)
    }

    /**
     * list entities and number of results
     *
     * @param aClass type we want to list
     * @param max (optional) max number of results
     * @param sortableDirection (optional) sort parameter
     * @param idsInList restrict the results to those ids
     * @return pair that contains the max results and the total number of objects reached by the filter
     */
    final <T extends GormEntity> Pair<List<T>, Long> list(final Class<T> aClass, final int max = 20, final ColumnHeaderFieldSpec.SortableDirection sortableDirection = null, final Collection<Long> idsInList) {
        list(aClass, max, null, null, sortableDirection, idsInList)
    }

    private static final int visitFilterFieldExpressionBool(FilterExpression filterExpression, int occ, List<String> where, Map namedParams) {
        final String fieldName = filterExpression.fieldName.replace("selfObject", "id")

        if (!filterExpression.isCollection) {
            switch (filterExpression.operator) {
                case Operator.IN:
                    if (filterExpression.value instanceof GetMethodReturn) {
                        GetMethodReturn methodReturn = filterExpression.value as GetMethodReturn
                        List<Long> listOfLongs = (methodReturn.value as List<? extends GormEntity>)*.ident() as List<Long>
                        if (filterExpression.fieldName.endsWith("selfObject") && !listOfLongs.isEmpty()) {
                            where << ("sc.${fieldName} in (${listOfLongs.join(',')})" as String)
                            occ++
                        }
                    } else if (filterExpression.value instanceof FieldInfo) {
                        FieldInfo fieldInfo = filterExpression.value as FieldInfo
                        if (fieldInfo && fieldInfo.value) {
                            where << ("sc.${filterExpression.fieldName} in ('${(fieldInfo.value as Collection)?.join("','")}')" as String)
                            occ++
                        }
                    } else if (filterExpression.value instanceof Collection) {
                        where << ("sc.${fieldName} in :${'npfe' + occ}" as String)
                        namedParams.put('npfe' + occ, filterExpression.value)
                        occ++
                    }
                    break
                case Operator.NI:
                    if (filterExpression.value instanceof Collection) {
                        if (!GormEntity.isAssignableFrom((filterExpression.value as Collection).first()?.class)) {
                            where << ("sc.${filterExpression.fieldName} not in :${'npfe' + occ}" as String)
                            namedParams.put('npfe' + occ, filterExpression.value)
                            occ++
                        } else {
                            List<Long> listOfLongs = (filterExpression.value as List<? extends GormEntity>)*.ident() as List<Long>
                            if (filterExpression.fieldName.endsWith("selfObject") && !listOfLongs.isEmpty()) {
                                where << ("sc.${filterExpression.fieldName.replace("selfObject", "id")} not in (${listOfLongs.join(',')})" as String)
                                occ++
                            }
                        }
                    } else if (filterExpression.value instanceof Long) {
                        where << ("sc.${filterExpression.fieldName} <> ${filterExpression.value}" as String)
                        occ++
                    }
                    break
                case Operator.EQ:
                    if (filterExpression.value instanceof FieldInfo) {
                        FieldInfo fieldInfo = filterExpression.value as FieldInfo
                        if (fieldInfo && fieldInfo.value) {
                            String filterSpecifierWhereClause
                            if (fieldInfo.fieldConstraint.field.type == Boolean) {
                                filterSpecifierWhereClause = fieldInfo.value ? "sc.${filterExpression.fieldName} is true" : "not sc.${filterExpression.fieldName} is false"
                            } else {
                                filterSpecifierWhereClause = "sc.${filterExpression.fieldName} = '${fieldInfo.value.toString()}'"
                            }
                            where << filterSpecifierWhereClause
                            occ++
                        }
                    } else if (filterExpression.value == null) {
                        where << ("sc.${filterExpression.fieldName} IS NULL" as String)
                        occ++
                    } else {
                        if (filterExpression.value instanceof Long || filterExpression.value instanceof Boolean) {
                            where << ("sc.${filterExpression.fieldName} = ${filterExpression.value}" as String)
                            occ++
                        } else if (filterExpression.value) {
                            where << ("sc.${filterExpression.fieldName} = :${'npfe' + occ}" as String)
                            namedParams.put('npfe' + occ, filterExpression.value)
                            occ++
                        }
                    }
                    break
                case Operator.NE:
                    if (filterExpression.value) {
                        where << ("sc.${filterExpression.fieldName} != '${filterExpression.value.toString()}'" as String)
                        occ++
                    } else if (filterExpression.value == null) {
                        where << ("sc.${filterExpression.fieldName} IS NOT NULL" as String)
                        occ++
                    }
                    break
                case Operator.LT:
                    if (filterExpression.value instanceof FieldInfo) {
                        FieldInfo fieldInfo = filterExpression.value as FieldInfo
                        if (fieldInfo.value) {
                            String filterSpecifierWhereClause
                            if (fieldInfo.fieldConstraint.field.type == Date) {
                                filterSpecifierWhereClause = " sc.${filterExpression.fieldName} <= '${fieldInfo.value.toString()}' "
                            } else {
                                filterSpecifierWhereClause = " sc.${filterExpression.fieldName} <= ${fieldInfo.value.toString()} "
                            }
                            where << filterSpecifierWhereClause
                            occ++
                        }
                    }
                    break
                case Operator.GT:
                    if (filterExpression.value instanceof FieldInfo) {
                        FieldInfo fieldInfo = filterExpression.value as FieldInfo
                        if (fieldInfo.value) {
                            String filterSpecifierWhereClause
                            if (fieldInfo.fieldConstraint.field.type == Date) {
                                filterSpecifierWhereClause = " sc.${filterExpression.fieldName} >= '${fieldInfo.value.toString()}' "
                            } else {
                                filterSpecifierWhereClause = " sc.${filterExpression.fieldName} >= ${fieldInfo.value.toString()} "
                            }
                            where << filterSpecifierWhereClause
                            occ++
                        }
                    } else {
                        if (filterExpression.value instanceof Date) {
                            where << ("sc.${filterExpression.fieldName} >= '${filterExpression.value.toString()}'" as String)
                            occ++
                        }
                    }
                    break
            }
        } else {
            switch (filterExpression.operator) {
                case Operator.EQ:
                    if (filterExpression.value instanceof Long) {
                        where << ("${filterExpression.value} in elements(sc.${filterExpression.fieldName})" as String)
                        occ++
                    }
                    break
                case Operator.IS_EMPTY:
                    where << ("sc.${filterExpression.fieldName} is empty" as String)
                    occ++
                    break
            }
        }
        return occ
    }

    /**
     * list entities and number of results
     *
     * @param aClass type we want to list
     * @param max (optional) max number of results
     * @param filterSpecifier (optional) additional filter criteria that overrides the ones passed via params
     * @param tInstance (optional) object of the same class as aClass, allowing to add filter criteria
     * @param sortableDirection (optional) sort parameter
     * @param idsInList (optional) restrict the results to those ids
     * @return pair that contains the max results and the total number of objects reached by the filter
     */
    final <T extends GormEntity> Pair<List<T>, Long> list(final Class<T> aClass, final int max = 20, final UiFilterSpecifier filterSpecifier = null, final T tInstance = null, final ColumnHeaderFieldSpec.SortableDirection sortableDirection = null, final Collection<Long> idsInList = null) {
        if (idsInList != null && idsInList.empty) return new Pair<>([], 0)
        joinEntityMap = [:]
        final Map<String, ? extends Object> theParams = params as Map<String, ? extends Object>
        def fieldNames = aClass.declaredFields*.name.findAll { it != "id" }
        def superClass = aClass.superclass
        if (superClass) {
            fieldNames.addAll superClass.declaredFields*.name.findAll { it != "id" }
        }
        Map<String, Object> filter = theParams.findAll {
            (filterMeta.contains(it.key) || (fieldNames.contains(it.key) && !(it.value instanceof Map))) ||
                    (it.key.contains('.') && !it.key.endsWith('.id') && !it.key.contains("Default")) ||
                    (it.key.startsWith('_reverse') || it.key.startsWith('_extension_'))
        }
        if (tInstance) {
            tInstance.class.getDeclaredFields().eachWithIndex { Field entry, int i ->
                if (tInstance[entry.name] != null && [String, Boolean, aClass].contains(entry.type)) filter.put(entry.name, tInstance[entry.name])
            }
        }

        String simpleClassName = aClass.name.substring(aClass.name.lastIndexOf(".") + 1)
        List<String> where = []
        if (idsInList) {
            if (!idsInList.empty) {
                where << ("sc.id in (${idsInList.join(",")})" as String)
            } else {
                where << 'sc.id is null'
            }
        }
        StringBuffer from = new StringBuffer("from ${simpleClassName} sc")
        StringBuffer join = new StringBuffer()
        int occ = 0

        Map<String, Object> namedParams = [:]
        filter?.each { Map.Entry<String, Object> entry ->
            if (entry.value && !filterMeta.contains(entry.key) && !entry.key.contains("filterExpression")) {
                final String entryKey = entry.key as String
                occ++
                /**
                 * We process special filter behaviors that are induced by their key names.
                 */
                if (entryKey.startsWith('_reverse')) {
                    String[] t = entryKey.split('_')
                    final String reverseClassName = t[2].substring(t[2].lastIndexOf(".") + 1)
                    final String reverseFieldName = t[3]
                    final String targetField = t[4]
                    where << ("sc.id IN (select auo.${reverseFieldName}.id from ${reverseClassName} auo where auo.${targetField} like '${escapeHqlParameter(entry.value as String)}')" as String)
                } else if (entryKey.startsWith('_extension_')) {
                    def targetField = entryKey - '_extension_'
                    Field f = getTheField(aClass, targetField)
                    where << filterExtensionMap[f.type]?.queryToWherePart(f.type, "sc." + targetField, entry.value?.toString())
                } else {
                    addJoinEntity(entryKey)
                    JoinEntity joinEntity = getJoinEntity(entryKey)

                    String aliasKey = joinEntity ? "sc${joinEntity.order}${entryKey - joinEntity.joinName}" : "sc.${entryKey}"

                    Field f = getTheField(aClass, entryKey)
                    if (f && f.type == Date) {
                        /**
                         *  For dates we have a simple DSL:
                         *      2022        everything after 2022 included
                         *      10          since october this year
                         *      202210      since october 2022
                         *
                         *  End date is build the same way, so we can create interval:
                         *      2022-2023   every after 2022 but before 2023 excluded
                         */
                        def v = (entry.value as String).replaceAll(' ', '')
                        def p = v.indexOf('-')
                        def v1 = p > 1 ? v.substring(0, p) : p == 0 ? null : v
                        if (v1)
                            if (v1 ==~ /([0-9]{2})/) {
                                where << ("$aliasKey > '${Calendar.instance.get(Calendar.YEAR)}-${v1.toInteger()}-01'" as String)
                            } else if (v1 ==~ /([0-9]{4})/) {
                                where << ("$aliasKey > '${v1.substring(0, 4).toInteger()}-01-01'" as String)
                            } else if (v1 ==~ /([0-9]{6})/) {
                                where << ("$aliasKey > '${v1.substring(0, 4).toInteger()}-${v1.substring(4, 6).toInteger()}-01'" as String)
                            } else {
                                try {
                                    Date d = DateFormat.parse('yyyyMMdd', v1)
                                    where << ("$aliasKey >= '${DateFormat.format(d, 'yyyy-MM-dd')}'" as String)
                                } catch (e) {
                                    log.error "Parse Date Error: ${e.message}"
                                }
                            }
                        def v2 = p >= 0 ? v.substring(p + 1) : null
                        if (v2)
                            if (v2 ==~ /([0-9]{2})/) {
                                where << ("$aliasKey < '${Calendar.instance.get(Calendar.YEAR)}-${v2.toInteger()}-01'" as String)
                            } else if (v2 ==~ /([0-9]{4})/) {
                                where << ("$aliasKey < '${v2.substring(0, 4).toInteger()}-01-01'" as String)
                            } else if (v2 ==~ /([0-9]{6})/) {
                                where << ("$aliasKey < '${v2.substring(0, 4).toInteger()}-${v2.substring(4, 6).toInteger()}-01'" as String)
                            } else {
                                try {
                                    Date d = DateFormat.parse('yyyyMMdd', v2)
                                    where << ("$aliasKey < '${DateFormat.format(d, 'yyyy-MM-dd')}'" as String)
                                } catch (e) {
                                    log.error "Parse Date Error: ${e.message}"
                                }
                            }
                    } else if (f && (f.type == boolean || f.type == Boolean)) {
                        boolean entryValue = entry.value instanceof String ? entry.value == "1" : entry.value as Boolean
                        where << ("$aliasKey = $entryValue" as String)
                    } else if (f && f.type == Set) {
                        if (entry.value instanceof String) {
                            join.append(" inner join ${aliasKey} j${occ}")
                            // TODO: review this part of the code ...
                            if ((entry.value as String).isNumber()) {
                                Long entryValue = entry.value as Long
                                where << ("j${occ} IN ($entryValue)" as String)
                            } else {
                                if (Class.forName((f.genericType as ParameterizedType).actualTypeArguments.first().typeName).isEnum()) {
                                    String entryValue = escapeHqlParameter(entry.value as String)
                                    where << ("j${occ} IN ('$entryValue')" as String)
                                } else {
                                    where << ("j${occ} IN (:np$occ)" as String)
                                    namedParams.put('np' + occ, entry.value)
                                }
                            }
                        } else {
                            if ((entry.value as List<String>).every { it.isNumber() }) {
                                join.append(" inner join ${aliasKey} j${occ}")
                                where << ("j${occ} IN (${(entry.value as List<Long>).join(',')})" as String)
                            } else {
                                join.append(" inner join ${aliasKey} j${occ}")
                                where << ("j${occ} IN (:np$occ)" as String)
                                namedParams.put('np' + occ, entry.value)
                            }
                        }
                    } else if (entry.value instanceof Long || (f && f.type == Integer)) {
                        Long entryValue = entry.value as Long
                        where << ("$aliasKey = $entryValue" as String)
                    } else if (entry.value instanceof String) {
                        String entryValue = entry.value as String
                        if (entryKey.contains('active')) {
                            where << ("$aliasKey = :np$occ" as String)
                            namedParams.put('np' + occ, entry.value)
                        } else if (entryKey.contains('area')) {
                        } else if (entryValue == 'null') {
                            //Useful if the null select option is picked to filter object without object (ex: businessTodo without parent)
                            where << ("${aliasKey} is null" as String)
                        } else if (!entryValue.contains(',')) {
                            if (f && f.type.isEnum()) {
                                where << (" $aliasKey = '$entryValue' " as String)
                            } else if (f) {
                                where << (" upper($aliasKey) like :np$occ " as String)
                                namedParams.put('np' + occ, entry.value.toString().toUpperCase())
                            }
                        } else {
                            StringBuffer tmp = new StringBuffer()
                            tmp.append(' (')
                            Integer index = 0
                            entryValue.tokenize(',').each { String it ->
                                String v = it.trim()
                                if (v) {
                                    if (index++ > 0) tmp.append(' or ')
                                    tmp.append(" upper($aliasKey) like :np$index ")
                                    namedParams.put('np' + index, v.toUpperCase())
                                }
                            }
                            tmp.append(') ')
                            where << tmp.toString()
                        }
                    } else {
                        where << ("$aliasKey = :${'np' + occ}" as String)
                        namedParams.put('np' + occ, entry.value)
                    }
                }
            } else if (entry.key.contains("filterExpression")) {

            }
        }

        if (filterSpecifier) {
            filterSpecifier.visitFilter(new UiFilterVisitorImpl() {
                @Override
                void visitFilterFieldExpressionBool(FilterExpression filterExpression) {
                    String qualifiedName = filterExpression.qualifiedName
                    boolean applyFilter = (theParams[qualifiedName] || theParams[qualifiedName + 'Default']) ? (theParams[qualifiedName] == '1') : true
                    if (!applyFilter) return

                    occ = visitFilterFieldExpressionBool(filterExpression, occ, where, namedParams)
                }

                @Override
                void visitFilterFieldExpressionBool(String i18n, FilterExpression[] filterExpressions, Boolean defaultValue) {
                    String qualifiedName = filterExpressions*.qualifiedName.join('_')
                    boolean applyFilter = (theParams[qualifiedName] || theParams[qualifiedName + 'Default']) ? (theParams[qualifiedName] == '1' || (defaultValue && !theParams[qualifiedName + 'Default'])) : defaultValue
                    if (!applyFilter) return

                    filterExpressions.each {
                        occ = visitFilterFieldExpressionBool(it, occ, where, namedParams)
                    }
                }

                @Override
                void visitFilterFieldExpressionBool(String i18n, FilterExpression filterExpression, Boolean defaultValue) {
                    boolean applyFilter = (theParams[filterExpression.qualifiedName] || theParams[filterExpression.qualifiedName + "Default"]) ? (theParams[filterExpression.qualifiedName] == "1" || (defaultValue && !theParams[filterExpression.qualifiedName + "Default"])) : defaultValue

                    if (!applyFilter) return
                    occ = visitFilterFieldExpressionBool(filterExpression, occ, where, namedParams)
                }
            })
        }

        join = buildJoinClause() ? buildJoinClause().append(join) : join
        String simpleOrder = filter['order'] ?: sortableDirection?.defaultSortingDirection?.toString()
        String simpleSort = filter['sort'] ?: sortableDirection?.field?.fieldName ?: sortableDirection ? RawHtmlFilterDump.getQualifiedName(sortableDirection?.fields) : null
        String sortOrder = simpleSort && simpleOrder ? " order by sc.$simpleSort $simpleOrder" : ""
        String selectOrder = simpleSort && simpleOrder ? " ,sc.$simpleSort " : ""

        String whereClause = where.empty ? " " : " where ${where.join(' and ')} "
        String query = "select distinct sc ${selectOrder}" + from.toString() + join.toString() + removeBrackets(whereClause) + sortOrder
        String count = 'select count(distinct sc) ' + from.toString() + join.toString() + removeBrackets(whereClause)
        List<T> res
        try {
            if (simpleSort && simpleOrder) {
                res = (executeQuery(query, namedParams, (max == -1) ? null : max, theParams["offset"] as Integer) as List<List>)*.first() as List<T>
            } else {
                res = executeQuery(query, namedParams, (max == -1) ? null : max, theParams["offset"] as Integer) as List<T>
            }
        } catch (e) {
            println "ERROR: $e Filter KO: $query"
        }
        return new Pair<List<T>, Long>(res, executeQueryUniqueResult(Long, namedParams, count) as Long)
    }

    /**
     * When a group header is present in the table, list the distinct values for the selected group
     * (without applying the filter)
     *
     * @param aClass class displayed in the table
     * @return list of distinct value
     */
    List listGroup(final Class aClass) {
        final Map<String, ? extends Object> theParams = params as Map<String, ? extends Object>
        if (theParams['grouping']) {
            final String simpleClassName = aClass.name.substring(aClass.name.lastIndexOf('.') + 1)

            final def groupList = (theParams['grouping'] as String).tokenize(' ')
            final def columnsName = groupList.join(', sc.')
            final def whereClause = groupList.join(' is not null and sc.')
            final String query = """
                select sc.${columnsName} from ${simpleClassName} sc
                where sc.${whereClause} is not null
                group by sc.${columnsName}
                order by sc.${columnsName}
            """

            return executeQuery(query, [:])
        } else return null
    }

    /**
     * List objects in the given group
     *
     * @param group the group (from {@link #listGroup(Class)}
     * @param aClass class displayed in the table
     * @param f filter to apply while retrieving the list
     * @param t object instance of type aClass that add filter criteria
     * @return pair that contains list of objects and the number of objects reached by the query
     */
    final <T extends GormEntity> Pair<List<T>, Long> listInGroup(def group, Class<T> aClass, final UiFilterSpecifier f = null, final T t = null) {
        final Map<String, ? extends Object> theParams = params as Map<String, ? extends Object>
        if (theParams['grouping']) {
            if (group.class.isArray()) {
                final def groupList = (theParams['grouping'] as String).tokenize(' ')
                groupList.eachWithIndex { it, i ->
                    theParams.put(it.trim(), (group as List)[i])
                }
                return list(aClass, 20, f, t)
            } else {
                theParams.put((theParams['grouping'] as String).trim(), group)
                return list(aClass, 20, f, t)
            }
        } else return null
    }

    final Map mapFilterDump(UiFilterSpecifier filter) {
        def v = new RawMapFilterDump(params)
        filter.visitFilter(v)
        v.theResults
    }

    final <T extends GormEntity> Pair<List<T>, Long> list(UiTableSpecifier table) {
        list(table.aClass, table.max, table.filterSpecifier, table.tInstance as T, table.sortableDirection, table.idsFilter)
    }
}
