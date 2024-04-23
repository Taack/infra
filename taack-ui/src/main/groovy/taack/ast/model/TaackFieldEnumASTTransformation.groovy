package taack.ast.model

import groovy.transform.CompileStatic
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
import taack.ast.annotation.TaackEnumName
import taack.ast.annotation.TaackFieldEnum
import taack.ast.type.FieldConstraint
import taack.ast.type.FieldInfo
import taack.ast.type.GetMethodReturn

import static org.codehaus.groovy.ast.ClassHelper.make
import static org.codehaus.groovy.ast.tools.GeneralUtils.*

/**
 * Handles generation of code for the @TaackFieldEnum annotation.
 */
// see http://docs.groovy-lang.org/latest/html/documentation/index.html#developing-ast-xforms
@CompileStatic
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
final class TaackFieldEnumASTTransformation extends AbstractASTTransformation {

    @CompileStatic
    enum DebugEnum {
        NORMAL, ONLY_FIELD, ONLY_METHOD
    }

    private static final DebugEnum debugEnum = DebugEnum.NORMAL
    private static final boolean trace = true

    private static final ClassNode GRM_TYPE = make(GetMethodReturn.class, false)
    private static final ClassNode FC_TYPE = make(FieldConstraint)
    private static final ClassNode TFE_TYPE = make(TaackFieldEnum)
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

    @Override
    void visit(ASTNode[] nodes, SourceUnit source) {

        printOut "source ${source.name}"
        init(nodes, source)

        AnnotationNode node = (AnnotationNode) nodes[0]
        AnnotatedNode parent = (AnnotatedNode) nodes[1]
        if (!TFE_TYPE.equals(node.getClassNode()))
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
                    if (it.name.startsWith("get") && !it.name.contains('_') && !it.name.contains('Solr') &&
                            !it.name.contains('Iterator') && it.parameters.size() == 0 &&
                            (it.modifiers & ACC_PRIVATE) == 0) {

                        if (debugEnum == DebugEnum.NORMAL || debugEnum == DebugEnum.ONLY_METHOD) {
                            MethodNode methodNode = addGetMethodMethodNode(classNode, it)
                            classNode.addMethod(methodNode)
                        }
                    }
                }
            }

            classNode.fields.each {

                // hasMany fields are added after semantic analysis
                if (it.name == 'belongsTo' /*|| it.name == "hasMany"*/) {
                    if (it.initialExpression instanceof MapExpression) {

                        MapExpression me = it.initialExpression as MapExpression
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
                                    MethodNode methodNode = addFieldMethodNode(classNode,
                                            //classNode.addField(fieldName, 2, valueClassExpression.type, null),
                                            fieldNode,
                                            null,
                                            constraints)
                                    printOut valueClassExpression.toString()
                                    printOut "Method ${it.name} added ${fieldName}: ${valueClassExpression.type}"
                                    classNode.addMethod(methodNode)
                                    avoidDuplicate.add(fieldName)
                                }
                            } else {
                                printOut "Avoid duplicates for ${keyExpression.value}"
                            }
                        }
                    } else if (it.initialExpression instanceof ListExpression) {

                        ListExpression le = it.initialExpression as ListExpression
                        le.expressions.each {
                            ClassExpression valueClassExpression = it as ClassExpression
                            // TODO
                            printOut "valueClassExpression: ${valueClassExpression} NOT DONE !"
                        }
                    }
                } else if (!(it.name.contains('_') || it.name.contains('$') || it.name.contains('hasMany') ||
                        DOMAIN_RESERVED.contains(it.name) ||
                        it.type.name.contains('DetachedCriteria') && (it.modifiers & ACC_PRIVATE) != 0)) {

                    List<AnnotationNode> annotationNodes = it.getAnnotations(TFE_TYPE)
                    String constraintName = null
                    FieldConstraint.Constraints constraints = constraintsMap.get(it.name)
                    if (!annotationNodes.empty) {
                        AnnotationNode enumName = annotationNodes.first()
                        ConstantExpression constantExpression = (ConstantExpression) enumName.getMember("name")
                        constraintName = constantExpression.value
                    }

                    if (!avoidDuplicate.contains(it.name)) {
                        if (debugEnum == DebugEnum.NORMAL || debugEnum == DebugEnum.ONLY_FIELD) {
                            MethodNode methodNode = addFieldMethodNode(classNode, it, constraintName, constraints)
                            classNode.addMethod(methodNode)
                            avoidDuplicate.add(it.name)
                        }
                    }
                }
            }
            if (debugEnum == DebugEnum.NORMAL)
                classNode.addMethod addSelfObjectMethod(classNode)
        }
    }

    private static final MethodNode addSelfObjectMethod(final ClassNode classNode) {
        final VariableExpression fieldConstraintsVariable = varX("fieldConstraints", FC_TYPE)
        final Statement fieldConstraintDeclarationStatement = stmt(new DeclarationExpression(
                fieldConstraintsVariable,
                Token.newSymbol(Types.ASSIGN, -1, -1),
                ctorX(
                        FC_TYPE,
                        args(
                                nullX(),
                                nullX(),
                                nullX(),
                        )
                )
        ))

        final ClassNode fiNode = GenericsUtils.makeClassSafeWithGenerics(FieldInfo, classNode)

        final Statement fieldMethodReturnExpression = returnS(
                castX(
                        fiNode,
                        ctorX(
                                fiNode,
                                args(
                                        fieldConstraintsVariable,
                                        varX("this", classNode)
                                )
                        )
                )
        )

        final BlockStatement code = block(
                fieldConstraintDeclarationStatement,
                fieldMethodReturnExpression
        )

        new MethodNode(
                "getSelfObject_",
                ACC_PUBLIC,
                fiNode,
                Parameter.EMPTY_ARRAY,
                ClassNode.EMPTY_ARRAY,
                code
        )
    }

    static Map<String, FieldConstraint.Constraints> dumpConstraints(final ClassNode classNode) {
        final FieldNode fn = classNode.getDeclaredField("constraints")

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
                                case "nullable":
                                    c.nullable = value
                                    break
                                case "min":
                                    c.min = (int) value
                                    break
                                case "max":
                                    c.max = (int) value
                                    break
                                case "widget":
                                    c.widget = value
                                    break
                                case "email":
                                    c.email = value
                            }
                        }
                        FieldConstraint.Constraints constraints = new FieldConstraint.Constraints(
                                c['widget'] as String,
                                (c['nullable'] ?: false) as boolean,
                                (c['email'] ?: false) as boolean,
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

    private static ClassNode transformNativeTypeIntoClass(ClassNode nativeType) {
        switch (nativeType.toString()) {
            case 'boolean':
                return make(Boolean)
            case 'int':
                return make(Integer)
            case 'float':
                return make(Float)
            case 'double':
                return make(Double)
            case 'char':
                return make(Character)
            case 'short':
                return make(Short)
            default:
                return nativeType
        }
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

    private static MethodNode addFieldMethodNode(final ClassNode classNode, final FieldNode fieldNode,
                                                 final String constraintName = null,
                                                 final FieldConstraint.Constraints constraints = null) {
        final VariableExpression constrVar = constraints ? varX("constraints", make(FieldConstraint.Constraints)) : null
        printOut "addFieldMethodNode: ${classNode.name}::${fieldNode.name}"

        if (debugEnum == DebugEnum.ONLY_METHOD) {
            printOut "return $debugEnum"
        }

        final Statement constraintsDeclarationExpression = constraints ? stmt(
                new DeclarationExpression(
                        constrVar,
                        Token.newSymbol(Types.ASSIGN, -1, -1),
                        ctorX(
                                make(FieldConstraint.Constraints),
                                args(
                                        constX(constraints.widget),
                                        constX(constraints.nullable),
                                        constX(constraints.email),
                                        constX(constraints.min),
                                        constX(constraints.max),
                                )
                        )
                )) : new EmptyStatement()

        final VariableExpression fieldConstraintsVariable = varX("fieldConstraints", make(FieldConstraint))

        final StaticMethodCallExpression sceGetDeclaredField = callX(
                classNode,
                "getDeclaredField",
                args(constX(fieldNode.name))
        )

        final ConstructorCallExpression cceFieldConstraint = ctorX(
                make(FieldConstraint),
                args(
                        constrVar ?: constX(null),
                        sceGetDeclaredField,
                        new ConstantExpression(constraintName),
                )
        )


        final Statement fieldConstraintDeclarationStatement = stmt(
                new DeclarationExpression(
                        fieldConstraintsVariable,
                        Token.newSymbol(Types.ASSIGN, -1, -1),
                        cceFieldConstraint
                ))

        final Statement fieldMethodReturnExpression = returnS(
                new CastExpression(
                        GenericsUtils.makeClassSafeWithGenerics(FieldInfo, castTypeToClass(fieldNode.type)),
                        new ConstructorCallExpression(
                                GenericsUtils.makeClassSafeWithGenerics(FieldInfo, castTypeToClass(fieldNode.type)),
                                new ArgumentListExpression([
                                        fieldConstraintsVariable,
                                        new ConstantExpression(fieldNode.name),
                                        new VariableExpression(fieldNode.name, fieldNode.type)
                                ] as List<Expression>)
                        )
                )
        )

        BlockStatement code = block(
                constraintsDeclarationExpression,
                fieldConstraintDeclarationStatement,
                fieldMethodReturnExpression
        )


        new MethodNode(
                transformNameIntoMethodName(fieldNode),
                ACC_PUBLIC,
                GenericsUtils.makeClassSafeWithGenerics(FieldInfo, castTypeToClass(fieldNode.type)),
                Parameter.EMPTY_ARRAY,
                ClassNode.EMPTY_ARRAY,
                code
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

    private static MethodNode addGetMethodMethodNode(final ClassNode classNode, final MethodNode methodNode) {
        String genMethodName = transformNameIntoMethodName methodNode
        printOut "addGetMethodMethodNode: ${classNode.name}::$genMethodName"

        if (debugEnum == DebugEnum.ONLY_FIELD) {
            printOut "return $debugEnum"
        }

        final BlockStatement body = new BlockStatement()

        StaticMethodCallExpression callGetMethod = callX(
                methodNode.returnType,
                "getMethod",
                args(methodNode.name)
        )

        callGetMethod.setNodeMetaData(StaticTypesMarker.DIRECT_METHOD_CALL_TARGET, true)
        callGetMethod.setNodeMetaData(StaticTypesMarker.INFERRED_TYPE, true)

        MethodCallExpression callMethod = callThisX(methodNode.name)

        callMethod.setNodeMetaData(StaticTypesMarker.DIRECT_METHOD_CALL_TARGET, true)
        callMethod.setNodeMetaData(StaticTypesMarker.INFERRED_TYPE, true)

        ConstructorCallExpression ctorGetMethodReturn = ctorX(
                GRM_TYPE,
                args(
                        callGetMethod,
                        callMethod
                )
        )

        ctorGetMethodReturn.setNodeMetaData(StaticTypesMarker.DIRECT_METHOD_CALL_TARGET, true)
        ctorGetMethodReturn.setNodeMetaData(StaticTypesMarker.INFERRED_TYPE, true)

        body.addStatement(
                returnS(
                        ctorGetMethodReturn
                )
        )

        new MethodNode(
                genMethodName,
                ACC_PUBLIC,
                GRM_TYPE,
                Parameter.EMPTY_ARRAY,
                ClassNode.EMPTY_ARRAY,
                body
        )

    }
}
