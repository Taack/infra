package taack.domain

import grails.compiler.GrailsCompileStatic
import org.grails.datastore.gorm.GormEntity
import taack.ast.type.FieldConstraint
import taack.ast.type.FieldInfo
import taack.ast.type.GetMethodReturn

import java.lang.reflect.Modifier
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
        Class aClassWithId = aClass.superclass != Object && !Modifier.isAbstract(aClass.superclass.getModifiers()) ? aClass.superclass : aClass
        fieldInfoWithId.add 0, new FieldInfo(new FieldConstraint(new FieldConstraint.Constraints("", false, false, null, null), aClassWithId.getDeclaredField('id'), null), 'id', (Long) 0)
        fieldInfoMap.put(aClass, fieldInfoWithId as FieldInfo[])
        gormClassesMap.put(aClass.simpleName, aClass)
        gormRealClassesMap.put(aClass.name, aClass)
    }

    final static void registerJdbcClass(Class<? extends GormEntity> aClass, GetMethodReturn... methodReturns) {
        methodMap.put(aClass, methodReturns)
        gormClassesMap.put(aClass.simpleName, aClass)
        gormRealClassesMap.put(aClass.name, aClass)
    }
}
