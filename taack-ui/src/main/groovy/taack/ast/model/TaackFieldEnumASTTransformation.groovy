package taack.ast.model

import groovy.transform.CompileStatic
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.ast.tools.GenericsUtils
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.syntax.Token
import org.codehaus.groovy.syntax.Types
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import taack.ast.annotation.TaackEnumName
import taack.ast.type.FieldConstraint
import taack.ast.type.FieldInfo
import taack.ast.type.GetMethodReturn

// see http://docs.groovy-lang.org/latest/html/documentation/index.html#developing-ast-xforms
@CompileStatic
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
final class TaackFieldEnumASTTransformation implements ASTTransformation {

    @CompileStatic
    enum DebugEnum {
        NORMAL, ONLY_FIELD, ONLY_METHOD
    }

    static DebugEnum debugEnum = DebugEnum.NORMAL
    static boolean trace = true

    static printOut(String outputString) {
        if (trace)
            println(outputString)
    }

    static boolean checkNodes(final ASTNode[] astNodes) {
        astNodes &&
                astNodes[0] &&
                astNodes[1] &&
                astNodes[0] instanceof AnnotationNode
    }

    @Override
    void visit(ASTNode[] nodes, SourceUnit source) {
        Set<String> avoidDuplicate = []
        if (!checkNodes(nodes)) return
        ClassNode classNode = (ClassNode) nodes[1]
        final Map<String, FieldConstraint.Constraints> constraintsMap = dumpConstraints(classNode)
        List<MethodNode> methods = new ArrayList<>(classNode.methods)
        methods.each {
            if (it.name.startsWith("get") && !it.name.contains('_') && !it.name.contains('Solr') && !it.name.contains('Iterator') &&
                    it.parameters.size() == 0 && (it.modifiers & MethodNode.ACC_PRIVATE) == 0) {
                if (debugEnum == DebugEnum.NORMAL || debugEnum == DebugEnum.ONLY_METHOD) {
                    MethodNode methodNode = addGetMethodMethodNode(classNode, it)
                    classNode.addMethod(methodNode)
                }
            } else {
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
//                            FieldNode fieldNode = classNode.getField(fieldName)
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
                                printOut "Add method for ${it.name} variable ${fieldName}: ${valueClassExpression.type}"
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
                    }
                }
            } else if (!(it.name.contains('_') || it.name.contains('$') || it.name.contains('hasMany') ||
                    ['serialVersionUID', 'self', 'mapping', 'constraints', 'ui', 'uiMap', 'metaClass', 'transients', 'mappedBy'].contains(it.name) ||
                    it.type.name.contains('DetachedCriteria') && (it.modifiers & MethodNode.ACC_PRIVATE) != 0
            )) {
                List<AnnotationNode> annotationNodes = it.getAnnotations(new ClassNode(TaackEnumName))
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
                } else {
                }

            } else {
            }
        }
        if (debugEnum == DebugEnum.NORMAL)
            classNode.addMethod addSelfObjectMethod(classNode)
    }

    private static MethodNode addSelfObjectMethod(final ClassNode classNode) {
        final VariableExpression fieldConstraintsVariable = new VariableExpression("fieldConstraints", ClassHelper.make(FieldConstraint))
        final ExpressionStatement fieldConstraintDeclarationStatement = new ExpressionStatement(new DeclarationExpression(
                fieldConstraintsVariable,
                Token.newSymbol(Types.ASSIGN, -1, -1),
                new ConstructorCallExpression(
                        ClassHelper.make(FieldConstraint),
                        new ArgumentListExpression([
                                new ConstantExpression(null),
//                                new StaticMethodCallExpression(
//                                        classNode,
//                                        "getDeclaredField",
//                                        new ArgumentListExpression([new ConstantExpression("this")] as List<Expression>)
//                                ),
                                new ConstantExpression(null),
                                new ConstantExpression(null),

                        ] as List<Expression>)
                )
        ))

        final ReturnStatement fieldMethodReturnExpression = new ReturnStatement(
                new CastExpression(
                        GenericsUtils.makeClassSafeWithGenerics(FieldInfo, classNode),
                        new ConstructorCallExpression(
                                GenericsUtils.makeClassSafeWithGenerics(FieldInfo, classNode),
                                new ArgumentListExpression([
                                        fieldConstraintsVariable,
                                        new ConstantExpression("selfObject"),
                                        new VariableExpression("this", classNode)
                                ] as List<Expression>)
                        )
                )
        )
        List<Statement> statements = []
        statements.addAll([fieldConstraintDeclarationStatement, fieldMethodReturnExpression])
        BlockStatement code = new BlockStatement(statements, new VariableScope())

        new MethodNode(
                "getSelfObject_",
                MethodNode.ACC_PUBLIC,
                GenericsUtils.makeClassSafeWithGenerics(FieldInfo, classNode),
                [] as Parameter[],
                [] as ClassNode[],
                code
        )

    }

    static Map<String, FieldConstraint.Constraints> dumpConstraints(final ClassNode classNode) {
        final FieldNode fn = classNode.getDeclaredField("constraints")
        if (fn == null) return [:]
        final ClosureExpression ce = fn.initialValueExpression as ClosureExpression
        final BlockStatement bs = ce.code as BlockStatement

        Map<String, FieldConstraint.Constraints> ret = [:]
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
                return ClassHelper.make(Boolean)
            case 'int':
                return ClassHelper.make(Integer)
            case 'float':
                return ClassHelper.make(Float)
            case 'double':
                return ClassHelper.make(Double)
            case 'char':
                return ClassHelper.make(Character)
            case 'short':
                return ClassHelper.make(Short)
            default:
                return nativeType
        }
    }

    private static String transformNameIntoMethodName(final String fieldNodeName) {
        'get' + fieldNodeName.capitalize() + '_'
    }

    private static String transformNameIntoMethodName(FieldNode fieldNode) {
//        'get' + (fieldNode.name != "name" ? fieldNode.name : "${fieldNode.type.nameWithoutPackage}Name").capitalize() + '_'
        transformNameIntoMethodName fieldNode.name
    }

    private static MethodNode addFieldMethodNode(final ClassNode classNode, final FieldNode fieldNode, final String constraintName = null, final FieldConstraint.Constraints constraints = null) {
        final VariableExpression constraintsVariable = constraints ? new VariableExpression("constraints", ClassHelper.make(FieldConstraint.Constraints)) : null
        printOut "addFieldMethodNode: ${classNode.name}::${fieldNode.name}"

        if (debugEnum == DebugEnum.ONLY_METHOD) {
            printOut "return $debugEnum"
        }

        final ExpressionStatement constraintsDeclarationExpression = constraints ? new ExpressionStatement(new DeclarationExpression(
                constraintsVariable,
                Token.newSymbol(Types.ASSIGN, -1, -1),
                new ConstructorCallExpression(
                        ClassHelper.make(FieldConstraint.Constraints),
                        new ArgumentListExpression([
                                new ConstantExpression(constraints.widget),
                                new ConstantExpression(constraints.nullable),
                                new ConstantExpression(constraints.email),
                                new ConstantExpression(constraints.min),
                                new ConstantExpression(constraints.max),
                        ] as List<Expression>)
                )
        )) : null

        final VariableExpression fieldConstraintsVariable = new VariableExpression("fieldConstraints", ClassHelper.make(FieldConstraint))
        final ExpressionStatement fieldConstraintDeclarationStatement = new ExpressionStatement(new DeclarationExpression(
                fieldConstraintsVariable,
                Token.newSymbol(Types.ASSIGN, -1, -1),
                new ConstructorCallExpression(
                        ClassHelper.make(FieldConstraint),
                        new ArgumentListExpression([
                                constraintsVariable ?: new ConstantExpression(null),
                                new StaticMethodCallExpression(
                                        classNode,
                                        "getDeclaredField",
                                        new ArgumentListExpression([new ConstantExpression(fieldNode.name)] as List<Expression>)
                                ),
                                new ConstantExpression(constraintName),

                        ] as List<Expression>)
                )
        ))

        final ReturnStatement fieldMethodReturnExpression = new ReturnStatement(
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
        List<Statement> statements = []
        if (constraintsDeclarationExpression) statements.add(constraintsDeclarationExpression)
        statements.addAll([fieldConstraintDeclarationStatement, fieldMethodReturnExpression])
        BlockStatement code = new BlockStatement(statements, new VariableScope())


        new MethodNode(
                transformNameIntoMethodName(fieldNode),
                MethodNode.ACC_PUBLIC,
                GenericsUtils.makeClassSafeWithGenerics(FieldInfo, castTypeToClass(fieldNode.type)),
                [] as Parameter[],
                [] as ClassNode[],
                code
        )
    }

    private static ClassNode castTypeToClass(final ClassNode classNode) {
        if (!classNode.isPrimaryClassNode()) {
            if (boolean.isAssignableFrom(classNode.typeClass)) {
                return new ClassNode(Boolean)
            } else if (int.isAssignableFrom(classNode.typeClass)) {
                return new ClassNode(Integer)
            }
        }
        return classNode
    }

    private static MethodNode addGetMethodMethodNode(final ClassNode classNode, final MethodNode methodNode) {
        String methodName = methodNode.name + '_'
        printOut "addGetMethodMethodNode: ${classNode.name}::$methodName"

        if (debugEnum == DebugEnum.ONLY_FIELD) {
            printOut "return $debugEnum"
        }

        BlockStatement code = new BlockStatement([
                new ReturnStatement(
                        new CastExpression(
                                GenericsUtils.makeClassSafeWithGenerics(GetMethodReturn, transformNativeTypeIntoClass(methodNode.returnType)),
                                new ConstructorCallExpression(
                                        GenericsUtils.makeClassSafeWithGenerics(GetMethodReturn, transformNativeTypeIntoClass(methodNode.returnType)),
                                        new ArgumentListExpression([
                                                new StaticMethodCallExpression(
                                                        classNode,
                                                        "getMethod",
                                                        new ArgumentListExpression(
                                                                [new ConstantExpression(methodNode.name)] as List<Expression>
                                                        )),
                                                new MethodCallExpression(
                                                        new VariableExpression('this'),
                                                        methodNode.name,
                                                        [] as ArgumentListExpression),
                                        ] as List<Expression>)
                                )
                        )
                )
        ] as List<Statement>, new VariableScope())
// cast something
        new MethodNode(
                methodName,
                MethodNode.ACC_PUBLIC,
                GenericsUtils.makeClassSafeWithGenerics(GetMethodReturn, transformNativeTypeIntoClass(methodNode.returnType)),
                [] as Parameter[],
                [] as ClassNode[],
                code
        )
    }
}
