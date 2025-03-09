package taack.domain

import grails.compiler.GrailsCompileStatic
import grails.util.Pair
import jakarta.persistence.metamodel.*
import org.grails.datastore.gorm.GormEntity
import org.hibernate.SessionFactory
import org.hibernate.query.Query
import org.springframework.beans.factory.annotation.Value

import java.nio.file.Files

/**
 * Service used to display domain class graphs. Depends on dot executable.
 */
@GrailsCompileStatic
final class TaackMetaModelService {
    SessionFactory sessionFactory

    @Value('${exe.dot.path}')
    String exeDotPath

    static Map<Class<? extends GormEntity>, List<Pair<ManagedType<?>, Set<Attribute<?, ?>>>>> cachedData = [:]

    private List<Pair<ManagedType<?>, Set<Attribute<?, ?>>>> listClassPointingTo(final Class<? extends GormEntity> toClass, final boolean includeSelf = false) {
        if (cachedData.containsKey(toClass)) return cachedData[toClass]
        Metamodel metamodel = sessionFactory.metamodel
        def types = metamodel.managedTypes.find { it.javaType == toClass }
        def allButSelfType = metamodel.managedTypes.findAll {
            includeSelf ? true : it != types
        }
        def allButSelfTypeAtt = allButSelfType.collect { mit ->
            def l = mit.attributes.findAll {
                if (it instanceof SingularAttribute) {
                    it.javaType == toClass
                } else if (it instanceof CollectionAttribute)
                    it.elementType.javaType == toClass
                else if (it instanceof SetAttribute)
                    it.elementType.javaType == toClass
                else if (it instanceof ListAttribute)
                    it.elementType.javaType == toClass
                else if (it instanceof MapAttribute) // ATM, should never be an MapAttribute ..
                    it.keyJavaType == toClass || it.elementType.javaType == toClass
                else println "TaackMetaModelService::listClassPointingTo Unknown relationship kind $it"
            }
            new Pair<ManagedType<?>, Set<Attribute<?, ?>>>(mit, l)
        }.findAll {
            (it.bValue as List).size() > 0
        }

        cachedData.put(toClass, allButSelfTypeAtt)
        allButSelfTypeAtt
    }

    private List listObjectPointing(final ManagedType<?> mt, final Attribute<?, ?> attribute, final Serializable id) {
        def q = "from ${mt} as c where ${attribute.collection ? "${id} = some elements(c.${attribute.name})" : "c.${attribute.name}=${id}"}"
        Query query = sessionFactory.currentSession.createQuery(q, mt.getJavaType())
        query.list()
    }

    private Integer countObjectPointing(final ManagedType<?> mt, final Attribute<?, ?> attribute, final Serializable id) {
        def q = "select count(c) from ${mt} as c where ${attribute.collection ? "${id} = some elements(c.${attribute.name})" : "c.${attribute.name}=${id}"}"
        Query<Integer> query = sessionFactory.currentSession.createQuery(q, Integer)
        query.uniqueResult()
    }

    private Integer updateObjectPointing(final ManagedType<?> mt, final Attribute<?, ?> attribute, final Serializable idFrom, final Serializable idTo) {
        if (!attribute.collection) {
            def qSingle = "update ${mt} c set c.${attribute.name}=${idTo} where ${"c.${attribute.name}=${idFrom}"}"
            def q = qSingle
            sessionFactory.currentSession.createQuery(q).executeUpdate()
        } else {
            0
        }
    }

    /**
     * List objects pointing to this entity
     *
     * @param entity
     * @return Type name, attribute name, list of objects
     */
    Map<Pair<String, String>, List<GormEntity>> listObjectsPointingTo(GormEntity entity) {
        Map<Pair<String, String>, List<GormEntity>> res = [:]
        listClassPointingTo(entity.class).each {
            it.bValue.each { it2 ->
                ManagedType mt = it.aValue
                Attribute<?, ?> attribute = it2
                def objects = listObjectPointing(mt, attribute, entity.ident()) as List<GormEntity>
                if (!objects.empty) {
                    res.put(new Pair<>(mt.javaType.name, attribute.name), objects)
                }
            }
        }
        res
    }

    /**
     * Helper method to replace an entity by another one.
     *
     * @param entityFrom
     * @param entityTo
     */
    void replaceEntity(GormEntity entityFrom, GormEntity entityTo) {
        listClassPointingTo(entityFrom.class).each {
            it.bValue.each { it2 ->
                ManagedType mt = it.aValue
                Attribute<?, ?> attribute = it2
                updateObjectPointing(mt, attribute, entityFrom.ident(), entityTo.ident())
            }
        }
    }

    /**
     * Create a string representing a graph for dot executable, of classes pointing to the parameter class.
     *
     * @param toClass
     * @return
     */
    String modelGraph(Class toClass = null) {
        Metamodel metamodel = sessionFactory.getMetamodel()
        final Set<EntityType<?>> types = metamodel.entities
        StringBuffer res = new StringBuffer()
        res.append("digraph mygraph {\n" +
                "  overlap = false;\n${toClass?"layout=circo;\n":""}" +
                "  node [shape=box];\n")
        types.each {
            it.attributes.each { ait ->
                if (ait instanceof SingularAttribute && types*.javaType.contains(ait.javaType)) {
                    if (!toClass || (ait.javaType.simpleName == toClass.simpleName))
                        res.append("${it.javaType.simpleName} -> ${ait.javaType.simpleName} [label = \"(Single) ${ait.name}\"]\n")
                } else if (ait instanceof CollectionAttribute && types*.javaType.contains(ait.elementType.javaType)) {
                    if (!toClass || (ait.elementType.javaType.simpleName == toClass.simpleName))
                        res.append("${it.javaType.simpleName} -> ${ait.elementType.javaType.simpleName} [label = \"(Coll) ${ait.name}\"]\n")
                } else if (ait instanceof SetAttribute && types*.javaType.contains(ait.elementType.javaType)) {
                    if (!toClass || (ait.elementType.javaType.simpleName == toClass.simpleName))
                        res.append("${it.javaType.simpleName} -> ${ait.elementType.javaType.simpleName} [label = \"(Set) ${ait.name}\"]\n")
                } else if (ait instanceof ListAttribute && types*.javaType.contains(ait.elementType.javaType)) {
                    if (!toClass || (ait.elementType.javaType.simpleName == toClass.simpleName))
                        res.append("${it.javaType.simpleName} -> ${ait.elementType.javaType.simpleName} [label = \"(List) ${ait.name}\"]\n")
                } else if (ait instanceof MapAttribute && types*.javaType.contains(ait.elementType.javaType)) {
                    // ATM, should never be an MapAttribute ..
                    if (!toClass || (ait.elementType.javaType.simpleName == toClass.simpleName))
                        res.append("${it.javaType.simpleName} -> ${ait.elementType.javaType.simpleName} [label = \"(Map) ${ait.name}\"]\n")
                }
            }
        }
        res.append("}")
        res.toString()
    }

    private String enumTransitionGraph(IEnumTransition2 enumTransition2, Set<IEnumTransition2> visited) {
        StringBuffer ret = new StringBuffer()
        for (def et : enumTransition2.transitionsTo(null)) {
            ret << enumTransition2.toString() + " -> " + et.toString() + "\n"
        }
        visited.add enumTransition2
        for (def et : enumTransition2.transitionsTo(null)) {
            if (!visited.contains(et)) ret << enumTransitionGraph(et, visited)
        }
        ret.toString()
    }

    /**
     * Create a string representing a transition graph for dot executable of an {@link IEnumTransition2}.
     *
     * @param enumTransition2
     * @return
     */
    String buildEnumTransitionGraph(IEnumTransition2 enumTransition2) {
        """digraph mygraph {
             overlap = false;
             node [shape=box];
             ${enumTransitionGraph(enumTransition2, [] as Set)}
             }
        """
    }

    /**
     * Call the dot executable to convert graph to SVG.
     *
     * @param graph Dot executable input string
     * @return SVG String
     */
    String svg(String graph) {
        def f = Files.createTempFile("graph_", ".dot").toFile()
        f << graph
        def p = "${exeDotPath} -Tsvg ${f.path}".execute()
        p.text
    }
}
