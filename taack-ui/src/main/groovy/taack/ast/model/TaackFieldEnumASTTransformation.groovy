package taack.ast.model

import groovy.transform.CompileStatic
import groovyjarjarasm.asm.Opcodes
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.EmptyStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.ast.tools.GenericsUtils
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.syntax.Token
import org.codehaus.groovy.syntax.Types
import org.codehaus.groovy.transform.AbstractASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.codehaus.groovy.transform.stc.StaticTypesMarker
import taack.ast.annotation.TaackFieldEnum
import taack.ast.type.FieldConstraint
import taack.ast.type.FieldInfo
import taack.ast.type.GetMethodReturn

import java.lang.reflect.Field

import static org.apache.groovy.ast.tools.ClassNodeUtils.addGeneratedMethod
import static org.codehaus.groovy.ast.ClassHelper.make
import static org.codehaus.groovy.ast.tools.GeneralUtils.*
/**
 * Handles generation of code for the @TaackFieldEnum annotation.
 */
// see http://docs.groovy-lang.org/latest/html/documentation/index.html#developing-ast-xforms
@CompileStatic
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
final class TaackFieldEnumASTTransformation extends AbstractASTTransformation {

    @CompileStatic
    enum DebugEnum {
        NORMAL, ONLY_FIELD, ONLY_METHOD
    }

    private static final DebugEnum debugEnum = DebugEnum.NORMAL
    private static final boolean trace = true

    private static final ClassNode GRM_TYPE = make(GetMethodReturn.class, false)
    private static final ClassNode FC_TYPE = make(FieldConstraint.class)
    private static final ClassNode TFE_TYPE = make(TaackFieldEnum.class)
    private static final String TFE_TYPE_NAME = '@' + TFE_TYPE.nameWithoutPackage
    private static final String[] DOMAIN_RESERVED =
            [
                    'serialVersionUID', 'self', 'mapping', 'constraints', 'ui',
                    'uiMap', 'metaClass', 'transients', 'mappedBy',
                    'log', 'instanceControllersDomainBindingApi',           // >6.2.0
                    'instanceConvertersApi', 'getConstrainedProperties',    // >6.2.0
                    'getProperties', 'getDirtyPropertyNames',               // >6.2.0
                    'getGormPersistentEntity', 'getGormDynamicFinders',     // >6.2.0
                    'getCount', 'getAll', 'getErrors'                       // >6.2.0
            ]

    private static final printOut(String outputString) {
        if (trace)
            println(outputString)
    }

    private static final String transformNameIntoMethodName(final String fieldNodeName) {
        'get' + fieldNodeName.capitalize() + '_'
    }

    private static final String transformNameIntoMethodName(final MethodNode methodNode) {
        methodNode.name + '_'
    }

    private static final String transformNameIntoMethodName(final FieldNode fieldNode) {
        transformNameIntoMethodName fieldNode.name
    }

    @Override
    void visit(ASTNode[] nodes, SourceUnit source) {

        printOut "source ${source.name}"
        init(nodes, source)

        AnnotationNode node = (AnnotationNode) nodes[0]
        AnnotatedNode parent = (AnnotatedNode) nodes[1]
        if (TFE_TYPE != node.getClassNode())
            return

        if (parent instanceof ClassNode) {
            ClassNode cNode = (ClassNode) parent
            if (!checkNotInterface(cNode, TFE_TYPE_NAME)) return

            final Set<String> avoidDuplicate = []
            final ClassNode classNode = (ClassNode) nodes[1]
            final Map<String, FieldConstraint.Constraints> constraintsMap = dumpConstraints(classNode)
            final List<MethodNode> methods = new ArrayList<>(classNode.methods)

            methods.each {
                if (!DOMAIN_RESERVED.contains(it.name)) {
                    // Filter pure Getters
                    if (it.name.startsWith('get') && !it.name.contains('_') && !it.name.contains('Solr') &&
                            !it.name.contains('Iterator') && it.parameters.size() == 0 &&
                            (it.modifiers & Opcodes.ACC_PRIVATE) == 0) {

                        if (debugEnum == DebugEnum.NORMAL || debugEnum == DebugEnum.ONLY_METHOD) {
                            addGetMethodMethodNode(classNode, it)
                        }
                    }
                }
            }

            classNode.fields.each { FieldNode fIt ->
                // hasMany fields are added after semantic analysis
                if (fIt.name == 'belongsTo' /*|| fIt.name == 'hasMany'*/) {
                    if (fIt.initialExpression instanceof MapExpression) {

                        MapExpression me = fIt.initialExpression as MapExpression
                        me.mapEntryExpressions.each { mee ->
                            ConstantExpression keyExpression = mee.keyExpression as ConstantExpression
                            ClassExpression valueClassExpression = mee.valueExpression as ClassExpression
                            FieldConstraint.Constraints constraints = constraintsMap.get(keyExpression.value)
                            final String fieldName = keyExpression.value as String
                            if (!avoidDuplicate.contains(fieldName)) {
                                FieldNode fieldNode = new FieldNode(fieldName,
                                        2,
                                        valueClassExpression.type,
                                        classNode,
                                        null)
                                if (debugEnum == DebugEnum.NORMAL || debugEnum == DebugEnum.ONLY_FIELD) {
                                    addFieldMethodNode(classNode,
                                            fieldNode,
                                            null,
                                            constraints)
                                    printOut valueClassExpression.toString()
                                    printOut "Method ${fIt.name} added ${fieldName}: ${valueClassExpression.type}"
                                    avoidDuplicate.add(fieldName)
                                }
                            } else {
                                printOut "Avoid duplicates for ${keyExpression.value}"
                            }
                        }
                    } else if (fIt.initialExpression instanceof ListExpression) {

                        ListExpression le = fIt.initialExpression as ListExpression
                        le.expressions.each {
                            ClassExpression valueClassExpression = it as ClassExpression
                            // TODO
                            printOut "valueClassExpression: ${valueClassExpression} NOT DONE !"
                        }
                    }
                } else if (!(fIt.name.startsWith('get') || fIt.name.contains('_') || fIt.name.contains('$')
                        || fIt.name.contains('hasMany') || DOMAIN_RESERVED.contains(fIt.name) ||
                        fIt.type.name.contains('DetachedCriteria') && (fIt.modifiers & Opcodes.ACC_PRIVATE) != 0)) {

                    List<AnnotationNode> annotationNodes = fIt.getAnnotations(TFE_TYPE)
                    String constraintName = null
                    FieldConstraint.Constraints constraints = constraintsMap.get(fIt.name)
                    if (!annotationNodes.empty) {
                        AnnotationNode enumName = annotationNodes.first()
                        ConstantExpression constantExpression = (ConstantExpression) enumName.getMember('name')
                        constraintName = constantExpression.value
                    }

                    if (!avoidDuplicate.contains(fIt.name)) {
                        if (debugEnum == DebugEnum.NORMAL || debugEnum == DebugEnum.ONLY_FIELD) {
                            addFieldMethodNode(classNode, fIt, constraintName, constraints)
                            avoidDuplicate.add(fIt.name)
                        }
                    }
                }
            }
            if (debugEnum == DebugEnum.NORMAL)
                addSelfObjectMethod(classNode)
        }
    }

    /**
     * Add self underscore method
     *
     * @param classNode
     * @return
     */
    private static final MethodNode addSelfObjectMethod(final ClassNode classNode) {
        printOut "addSelfObjectMethod: ${classNode.name}"

        final VariableExpression fieldConstraintsVariable = varX('fieldConstraints', FC_TYPE)

        final ConstructorCallExpression ctorFC = ctorX(
                FC_TYPE,
                args(
                        nullX(),
                        nullX(),
                        nullX(),
                )
        )

        ctorFC.setNodeMetaData(StaticTypesMarker.DIRECT_METHOD_CALL_TARGET, FC_TYPE.getDeclaredConstructors().first())
        ctorFC.setNodeMetaData(StaticTypesMarker.INFERRED_TYPE, FC_TYPE)

        final Statement fieldConstraintDeclarationStatement = stmt(new DeclarationExpression(
                fieldConstraintsVariable,
                Token.newSymbol(Types.ASSIGN, -1, -1),
                ctorFC
        ))

        final ClassNode fiNode = GenericsUtils.makeClassSafeWithGenerics(FieldInfo, classNode)

        final ConstructorCallExpression ctorFi = ctorX(
                fiNode,
                args(
                        fieldConstraintsVariable,
                        constX('selfObject'),
                        varX('this', classNode)
                )
        )

        ctorFi.setNodeMetaData(StaticTypesMarker.DIRECT_METHOD_CALL_TARGET, fiNode.getDeclaredConstructors().first())
        ctorFi.setNodeMetaData(StaticTypesMarker.INFERRED_TYPE, fiNode)

        final Statement fieldMethodReturnExpression = returnS(
                castX(
                        fiNode,
                        ctorFi
                )
        )

        final BlockStatement code = block(
                fieldConstraintDeclarationStatement,
                fieldMethodReturnExpression
        )

        addGeneratedMethod(
                classNode,
                'getSelfObject_',
                Opcodes.ACC_PUBLIC,
                fiNode,
                Parameter.EMPTY_ARRAY,
                ClassNode.EMPTY_ARRAY,
                code
        )
    }

    static Map<String, FieldConstraint.Constraints> dumpConstraints(final ClassNode classNode) {
        final FieldNode fn = classNode.getDeclaredField('constraints')

        if (fn == null) return [:]

        final ClosureExpression ce = fn.initialValueExpression as ClosureExpression
        final BlockStatement bs = ce.code as BlockStatement
        final Map<String, FieldConstraint.Constraints> ret = [:]

        bs.statements.each { Statement s ->
            if (s instanceof ExpressionStatement) {
                Expression e = s.expression
                if (e instanceof MethodCallExpression) {
                    ConstantExpression cst = (ConstantExpression) ((MethodCallExpression) e).method
                    TupleExpression te = (TupleExpression) ((MethodCallExpression) e).arguments
                    NamedArgumentListExpression nal = (NamedArgumentListExpression) te.expressions.first()
                    Map<String, Object> c = [:]
                    nal.mapEntryExpressions.each { MapEntryExpression mee ->
                        Object key = ((ConstantExpression) mee.keyExpression).value
                        if (mee.valueExpression instanceof ConstantExpression) {
                            Object value = ((ConstantExpression) mee.valueExpression).value
                            switch (key) {
                                case 'nullable':
                                    c.nullable = value
                                    break
                                case 'min':
                                    c.min = (int) value
                                    break
                                case 'max':
                                    c.max = (int) value
                                    break
                                case 'widget':
                                    c.widget = value
                                    break
                                case 'email':
                                    c.email = value
                            }
                        }
                        FieldConstraint.Constraints constraints = new FieldConstraint.Constraints(
                                c['widget'] as String,
                                (c['nullable'] ?: false) as Boolean,
                                (c['email'] ?: false) as Boolean,
                                c['min'] as Integer,
                                c['max'] as Integer
                        )
                        ret.put(cst.value.toString(), constraints)
                    }
                }
            }
        }
        ret
    }

    /**
     * Add class fields underscore methods
     *
     * @param classNode
     * @param fieldNode
     * @param constraintName
     * @param constraints
     */
    private static final void addFieldMethodNode(final ClassNode classNode, final FieldNode fieldNode,
                                                 final String constraintName = null,
                                                 final FieldConstraint.Constraints constraints = null) {
        printOut "addFieldMethodNode: ${classNode.name}::${fieldNode.name}"

        final VariableExpression constrVar = constraints ? varX('constraints', make(FieldConstraint.Constraints)) : null

        if (debugEnum == DebugEnum.ONLY_METHOD) {
            printOut "return $debugEnum"
        }

        ClassNode fieldConstraintsCN = make(FieldConstraint.Constraints)

        ConstructorCallExpression ctorFieldConstraints = constraints ? ctorX(
                fieldConstraintsCN,
                args(
                        constX(constraints.widget),
                        constX(constraints.nullable),
                        constX(constraints.email),
                        constX(constraints.min),
                        constX(constraints.max),
                )
        ) : null

        if (ctorFieldConstraints) {
            ctorFieldConstraints.setNodeMetaData(StaticTypesMarker.DIRECT_METHOD_CALL_TARGET, fieldConstraintsCN.getDeclaredConstructors().first())
            ctorFieldConstraints.setNodeMetaData(StaticTypesMarker.INFERRED_TYPE, fieldConstraintsCN)
        }

        final Statement constraintsDeclarationExpression = constraints ? stmt(
                new DeclarationExpression(
                        constrVar,
                        Token.newSymbol(Types.ASSIGN, -1, -1),
                        ctorFieldConstraints ?: new EmptyExpression()
                )) : new EmptyStatement()

        final ClassNode fieldConstraintCN = make(FieldConstraint)
        final ClassNode fieldCN = make(Field)


        final VariableExpression fieldConstraintsVariable = varX('fieldConstraints', fieldConstraintCN)

        final StaticMethodCallExpression sceGetDeclaredField = callX(
                classNode,
                'getDeclaredField',
                args(constX(fieldNode.name))
        )

        sceGetDeclaredField.setNodeMetaData(StaticTypesMarker.DIRECT_METHOD_CALL_TARGET, 'getDeclaredField')
        sceGetDeclaredField.setNodeMetaData(StaticTypesMarker.INFERRED_TYPE, fieldCN)

        final ConstructorCallExpression cceFieldConstraint = ctorX(
                fieldConstraintCN,
                args(
                        constrVar ?: nullX(),
                        sceGetDeclaredField,
                        new ConstantExpression(constraintName),
                )
        )

        cceFieldConstraint.setNodeMetaData(StaticTypesMarker.DIRECT_METHOD_CALL_TARGET, fieldConstraintCN.getDeclaredConstructors().first())
        cceFieldConstraint.setNodeMetaData(StaticTypesMarker.INFERRED_TYPE, fieldConstraintCN)

        final Statement fieldConstraintDeclarationStatement = stmt(
                new DeclarationExpression(
                        fieldConstraintsVariable,
                        Token.newSymbol(Types.ASSIGN, -1, -1),
                        cceFieldConstraint
                ))

        final ClassNode filedInfoCN = make(FieldInfo)

        final ConstructorCallExpression ctorFieldInfo = ctorX(
                filedInfoCN,
                args(
                        fieldConstraintsVariable,
                        constX(fieldNode.name),
                        varX(fieldNode.name, fieldNode.type)
                )
        )

        ctorFieldInfo.setNodeMetaData(StaticTypesMarker.DIRECT_METHOD_CALL_TARGET, filedInfoCN.getDeclaredConstructors().first())
        ctorFieldInfo.setNodeMetaData(StaticTypesMarker.INFERRED_TYPE, filedInfoCN)

        final Statement fieldMethodReturnExpression = returnS(
                castX(
                        filedInfoCN,
                        ctorFieldInfo
                )
        )

        BlockStatement body = block(
                constraintsDeclarationExpression,
                fieldConstraintDeclarationStatement,
                fieldMethodReturnExpression
        )

        addGeneratedMethod(
                classNode,
                transformNameIntoMethodName(fieldNode),
                Opcodes.ACC_PUBLIC,
                GenericsUtils.makeClassSafeWithGenerics(FieldInfo, castTypeToClass(fieldNode.type)),
                Parameter.EMPTY_ARRAY,
                ClassNode.EMPTY_ARRAY,
                body
        )
    }

    private static ClassNode castTypeToClass(final ClassNode classNode) {
        if (!classNode.isPrimaryClassNode()) {
            if (boolean.isAssignableFrom(classNode.typeClass)) {
                return make(Boolean)
            } else if (int.isAssignableFrom(classNode.typeClass)) {
                return make(Integer)
            }
        }
        return classNode
    }

    /**
     * add getter underscore methods
     *
     * @param classNode
     * @param methodNode
     */
    private static final void addGetMethodMethodNode(final ClassNode classNode, final MethodNode methodNode) {
        String genMethodName = transformNameIntoMethodName methodNode
        printOut "addGetMethodMethodNode: ${classNode.name}::$genMethodName ${methodNode.returnType}"

        if (debugEnum == DebugEnum.ONLY_FIELD) {
            printOut "return $debugEnum"
        }

        final BlockStatement body = new BlockStatement()

        StaticMethodCallExpression callGetMethod = callX(
                classNode,
                'getMethod',
                args(constX(methodNode.name))
        )

//        callGetMethod.setNodeMetaData(StaticTypesMarker.DIRECT_METHOD_CALL_TARGET, methodNode)
//        callGetMethod.setNodeMetaData(StaticTypesMarker.INFERRED_TYPE, make(Method))

        MethodCallExpression callMethod = callThisX(methodNode.name)

        callMethod.putNodeMetaData(StaticTypesMarker.DIRECT_METHOD_CALL_TARGET, methodNode)
        callMethod.putNodeMetaData(StaticTypesMarker.INFERRED_TYPE, methodNode.returnType)

        ConstructorCallExpression ctorGetMethodReturn = ctorX(
                GRM_TYPE,
                args(
                        callGetMethod,
                        callMethod
                )
        )

        ctorGetMethodReturn.setNodeMetaData(StaticTypesMarker.DIRECT_METHOD_CALL_TARGET, GRM_TYPE.getDeclaredConstructors().first())
        ctorGetMethodReturn.setNodeMetaData(StaticTypesMarker.INFERRED_TYPE, GRM_TYPE)

        body.addStatement(
                returnS(
                        ctorGetMethodReturn
                )
        )

        addGeneratedMethod(
                classNode,
                genMethodName,
                Opcodes.ACC_PUBLIC,
                GRM_TYPE,
                Parameter.EMPTY_ARRAY,
                ClassNode.EMPTY_ARRAY,
                body
        )
    }
}
