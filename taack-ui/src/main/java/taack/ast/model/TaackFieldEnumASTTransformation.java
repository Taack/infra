package taack.ast.model;

import groovy.transform.CompileStatic;
import groovyjarjarasm.asm.Opcodes;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.tools.GenericsUtils;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.syntax.Types;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.codehaus.groovy.transform.stc.StaticTypesMarker;
import taack.ast.annotation.TaackFieldEnum;
import taack.ast.type.FieldConstraint;
import taack.ast.type.FieldInfo;
import taack.ast.type.GetMethodReturn;

import java.lang.reflect.Field;
import java.util.*;

import static org.apache.groovy.ast.tools.ClassNodeUtils.addGeneratedMethod;
import static org.codehaus.groovy.ast.ClassHelper.make;
import static org.codehaus.groovy.ast.tools.GeneralUtils.*;

/**
 * Handles generation of code for the @TaackFieldEnum annotation.
 */
// see http://docs.groovy-lang.org/latest/html/documentation/index.html#developing-ast-xforms
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public final class TaackFieldEnumASTTransformation extends AbstractASTTransformation {

    @CompileStatic
    enum DebugEnum {
        NORMAL, ONLY_FIELD, ONLY_METHOD
    }

    private static final DebugEnum debugEnum = DebugEnum.NORMAL;
    private static final boolean trace = true;

    private static final ClassNode GRM_TYPE = make(GetMethodReturn.class, false);
    private static final ClassNode FC_TYPE = make(FieldConstraint.class);
    private static final ClassNode TFE_TYPE = make(TaackFieldEnum.class);
    private static final String TFE_TYPE_NAME = '@' + TFE_TYPE.getNameWithoutPackage();
    private static final List<String> DOMAIN_RESERVED =
            Arrays.asList(
                    "serialVersionUID", "self", "mapping", "constraints", "ui",
                    "uiMap", "metaClass", "transients", "mappedBy",
                    "log", "instanceControllersDomainBindingApi",           // >6.2.0
                    "instanceConvertersApi", "getConstrainedProperties",    // >6.2.0
                    "getProperties", "getDirtyPropertyNames",               // >6.2.0
                    "getGormPersistentEntity", "getGormDynamicFinders",     // >6.2.0
                    "getCount", "getAll", "getErrors"                       // >6.2.0
            );

    private static void printOut(String outputString) {
        if (trace) {
            System.out.println(outputString);
        }
    }

    private static String transformNameIntoMethodName(final String fieldNodeName) {
        return "get" + fieldNodeName.substring(0, 1).toUpperCase() + fieldNodeName.substring(1) + '_';
    }

    private static String transformNameIntoMethodName(final MethodNode methodNode) {
        return methodNode.getName() + '_';
    }

    private static String transformNameIntoMethodName(final FieldNode fieldNode) {
        return transformNameIntoMethodName(fieldNode.getName());
    }

    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        printOut("TaackFieldEnumASTTransformation::visit " + source.getName());
        init(nodes, source);
        AnnotationNode node = (AnnotationNode) nodes[0];
        AnnotatedNode parent = (AnnotatedNode) nodes[1];
        if (!TFE_TYPE.equals(node.getClassNode())) {
            printOut("TFE_TYPE != node.getClassNode() => " + TFE_TYPE + " != " + node.getClassNode());
            return;
        }
        if (parent instanceof ClassNode classNode) {
            if (!checkNotInterface(classNode, TFE_TYPE_NAME)) return;

            final Set<String> avoidDuplicate = new HashSet<>();
            //final ClassNode classNode = (ClassNode) nodes[1];
            final Map<String, FieldConstraint.Constraints> constraintsMap = dumpConstraints(classNode);
            final List<MethodNode> methods = new ArrayList<>(classNode.getMethods());

            for (MethodNode it : methods) {
                if (!DOMAIN_RESERVED.contains(it.getName())) {
                    // Filter pure Getters
                    if (it.getName().startsWith("get") && !it.getName().contains("_") && !it.getName().contains("Solr") &&
                            !it.getName().contains("Iterator") && it.getParameters().length == 0 &&
                            (it.getModifiers() & Opcodes.ACC_PRIVATE) == 0) {

                        if (debugEnum == DebugEnum.NORMAL || debugEnum == DebugEnum.ONLY_METHOD) {
                            addGetMethodMethodNode(classNode, it);
                        }
                    }
                }
            }

            for (FieldNode fIt : classNode.getFields()) {
                // hasMany fields are added after semantic analysis
                if (Objects.equals(fIt.getName(), "belongsTo") /*|| fIt.name == 'hasMany'*/) {
                    if (fIt.getInitialExpression() instanceof MapExpression me) {

                        for (MapEntryExpression mee : me.getMapEntryExpressions()) {
                            ConstantExpression keyExpression = (ConstantExpression) mee.getKeyExpression();
                            ClassExpression valueClassExpression = (ClassExpression) mee.getValueExpression();
                            FieldConstraint.Constraints constraints = constraintsMap.get((String) keyExpression.getValue());
                            final String fieldName = (String) keyExpression.getValue();
                            if (!avoidDuplicate.contains(fieldName)) {
                                FieldNode fieldNode = new FieldNode(fieldName,
                                        2,
                                        valueClassExpression.getType(),
                                        classNode,
                                        null);
                                if (debugEnum == DebugEnum.NORMAL || debugEnum == DebugEnum.ONLY_FIELD) {
                                    addFieldMethodNode(classNode,
                                            fieldNode,
                                            null,
                                            constraints);
                                    printOut(valueClassExpression.toString());
                                    printOut("Method " + fIt.getName() + " added " + fieldName + ": " + valueClassExpression.getType());
                                    avoidDuplicate.add(fieldName);
                                }
                            } else {
                                printOut("Avoid duplicates for " + keyExpression.getValue());
                            }
                        }
                    } else if (fIt.getInitialExpression() instanceof ListExpression le) {

                        for (Expression it : le.getExpressions()) {
                            ClassExpression valueClassExpression = (ClassExpression) it;
                            // TODO
                            printOut("valueClassExpression: " + valueClassExpression + " NOT DONE !");
                        }
                    }
                } else if (!(fIt.getName().startsWith("get") || fIt.getName().contains("_") || fIt.getName().contains("$")
                        || fIt.getName().contains("hasMany") || DOMAIN_RESERVED.contains(fIt.getName()) ||
                        fIt.getType().getName().contains("DetachedCriteria") && (fIt.getModifiers() & Opcodes.ACC_PRIVATE) != 0)) {

                    List<AnnotationNode> annotationNodes = fIt.getAnnotations(TFE_TYPE);
                    String constraintName = null;
                    FieldConstraint.Constraints constraints = constraintsMap.get(fIt.getName());
                    if (!annotationNodes.isEmpty()) {
                        AnnotationNode enumName = annotationNodes.get(0);
                        ConstantExpression constantExpression = (ConstantExpression) enumName.getMember("name");
                        constraintName = (String) constantExpression.getValue();
                    }

                    if (!avoidDuplicate.contains(fIt.getName())) {
                        if (debugEnum == DebugEnum.NORMAL || debugEnum == DebugEnum.ONLY_FIELD) {
                            addFieldMethodNode(classNode, fIt, constraintName, constraints);
                            avoidDuplicate.add(fIt.getName());
                        }
                    }
                }
            }
            if (debugEnum == DebugEnum.NORMAL)
                addSelfObjectMethod(classNode);
        }
    }

    /**
     * Add self underscore method
     *
     */
    private static void addSelfObjectMethod(final ClassNode classNode) {
        printOut("addSelfObjectMethod: " + classNode.getName());

        final VariableExpression fieldConstraintsVariable = varX("fieldConstraints", FC_TYPE);

        final ConstructorCallExpression ctorFC = ctorX(
                FC_TYPE,
                args(
                        nullX(),
                        nullX(),
                        nullX()
                )
        );

        ctorFC.setNodeMetaData(StaticTypesMarker.DIRECT_METHOD_CALL_TARGET, FC_TYPE.getDeclaredConstructors().get(0));
        ctorFC.setNodeMetaData(StaticTypesMarker.INFERRED_TYPE, FC_TYPE);

        final Statement fieldConstraintDeclarationStatement = stmt(new DeclarationExpression(
                fieldConstraintsVariable,
                Token.newSymbol(Types.ASSIGN, -1, -1),
                ctorFC
        ));

        final ClassNode fiNode = GenericsUtils.makeClassSafeWithGenerics(FieldInfo.class, classNode);

        final ConstructorCallExpression ctorFi = ctorX(
                fiNode,
                args(
                        fieldConstraintsVariable,
                        constX("selfObject"),
                        varX("this", classNode)
                )
        );

        ctorFi.setNodeMetaData(StaticTypesMarker.DIRECT_METHOD_CALL_TARGET, fiNode.getDeclaredConstructors().get(0));
        ctorFi.setNodeMetaData(StaticTypesMarker.INFERRED_TYPE, fiNode);

        final Statement fieldMethodReturnExpression = returnS(
                castX(
                        fiNode,
                        ctorFi
                )
        );

        final BlockStatement code = block(
                fieldConstraintDeclarationStatement,
                fieldMethodReturnExpression
        );

        addGeneratedMethod(
                classNode,
                "getSelfObject_",
                Opcodes.ACC_PUBLIC,
                fiNode,
                Parameter.EMPTY_ARRAY,
                ClassNode.EMPTY_ARRAY,
                code
        );
    }

    static Map<String, FieldConstraint.Constraints> dumpConstraints(final ClassNode classNode) {
        final FieldNode fn = classNode.getDeclaredField("constraints");

        if (fn == null) return new HashMap<>();
        final ClosureExpression ce = (ClosureExpression) fn.getInitialValueExpression();
        final BlockStatement bs = (BlockStatement) ce.getCode();
        final Map<String, FieldConstraint.Constraints> ret = new HashMap<>();

        for (Statement s : bs.getStatements()) {
            if (s instanceof ExpressionStatement) {
                Expression e = ((ExpressionStatement) s).getExpression();
                if (e instanceof MethodCallExpression) {
                    ConstantExpression cst = (ConstantExpression) ((MethodCallExpression) e).getMethod();
                    TupleExpression te = (TupleExpression) ((MethodCallExpression) e).getArguments();
                    NamedArgumentListExpression nal = (NamedArgumentListExpression) te.getExpression(0);
                    Map<String, Object> c = new HashMap<>();
                    for (MapEntryExpression mee : nal.getMapEntryExpressions()) {
                        Object key = ((ConstantExpression) mee.getKeyExpression()).getValue();
                        if (mee.getValueExpression() instanceof ConstantExpression) {
                            Object value = ((ConstantExpression) mee.getValueExpression()).getValue();
                            if (key.equals("nullable")) {
                                c.put("nullable", value);
                            } else if (key.equals("min")) {
                                c.put("min", value);
                            } else if (key.equals("max")) {
                                c.put("max", value);
                            } else if (key.equals("widget")) {
                                c.put("widget", value);
                            } else if (key.equals("email")) {
                                c.put("email", value);
                            }
                        }
                        FieldConstraint.Constraints constraints = new FieldConstraint.Constraints(
                                (String) c.get("widget"),
                                (Boolean) (c.get("nullable") != null ? c.get("nullable") : false),
                                (Boolean) (c.get("email") != null ? c.get("email") : false),
                                (Number) c.get("min"),
                                (Number) c.get("max")
                        );
                        ret.put(cst.getValue().toString(), constraints);
                    }
                }
            }
        }
        return ret;
    }

    /**
     * Add class fields underscore methods
     *
     */
    private static void addFieldMethodNode(final ClassNode classNode, final FieldNode fieldNode,
                                           final String constraintName,
                                           final FieldConstraint.Constraints constraints) {
        printOut("addFieldMethodNode: " + classNode.getName() + "::" + fieldNode.getName());

        final VariableExpression constrVar = constraints != null ? varX("constraints", make(FieldConstraint.Constraints.class)) : null;

        if (debugEnum == DebugEnum.ONLY_METHOD) {
            printOut("return " + debugEnum);
        }

        ClassNode fieldConstraintsCN = make(FieldConstraint.Constraints.class);

        ConstructorCallExpression ctorFieldConstraints = constraints != null ? ctorX(
                fieldConstraintsCN,
                args(
                        constX(constraints.getWidget()),
                        constX(constraints.getNullable()),
                        constX(constraints.getEmail()),
                        constX(constraints.getMin()),
                        constX(constraints.getMax())
                )
        ) : null;

        if (ctorFieldConstraints != null) {
            ctorFieldConstraints.setNodeMetaData(StaticTypesMarker.DIRECT_METHOD_CALL_TARGET, fieldConstraintsCN.getDeclaredConstructors().get(0));
            ctorFieldConstraints.setNodeMetaData(StaticTypesMarker.INFERRED_TYPE, fieldConstraintsCN);
        }

        final Statement constraintsDeclarationExpression = constraints != null ? stmt(
                new DeclarationExpression(
                        constrVar,
                        Token.newSymbol(Types.ASSIGN, -1, -1),
                        ctorFieldConstraints
                )) : new EmptyStatement();

        final ClassNode fieldConstraintCN = make(FieldConstraint.class);
        final ClassNode fieldCN = make(Field.class);

        final VariableExpression fieldConstraintsVariable = varX("fieldConstraints", fieldConstraintCN);

        final StaticMethodCallExpression sceGetDeclaredField = callX(
                classNode,
                "getDeclaredField",
                args(constX(fieldNode.getName()))
        );

        sceGetDeclaredField.setNodeMetaData(StaticTypesMarker.DIRECT_METHOD_CALL_TARGET, "getDeclaredField");
        sceGetDeclaredField.setNodeMetaData(StaticTypesMarker.INFERRED_TYPE, fieldCN);

        final ConstructorCallExpression cceFieldConstraint = ctorX(
                fieldConstraintCN,
                args(
                        constrVar != null ? constrVar : nullX(),
                        sceGetDeclaredField,
                        new ConstantExpression(constraintName))
        );

        cceFieldConstraint.setNodeMetaData(StaticTypesMarker.DIRECT_METHOD_CALL_TARGET, fieldConstraintCN.getDeclaredConstructors().get(0));
        cceFieldConstraint.setNodeMetaData(StaticTypesMarker.INFERRED_TYPE, fieldConstraintCN);

        final Statement fieldConstraintDeclarationStatement = stmt(
                new DeclarationExpression(
                        fieldConstraintsVariable,
                        Token.newSymbol(Types.ASSIGN, -1, -1),
                        cceFieldConstraint)
        );

        final ClassNode filedInfoCN = make(FieldInfo.class);

        final ConstructorCallExpression ctorFieldInfo = ctorX(
                filedInfoCN,
                args(
                        fieldConstraintsVariable,
                        constX(fieldNode.getName()),
                        varX(fieldNode.getName(), fieldNode.getType())
                )
        );

        ctorFieldInfo.setNodeMetaData(StaticTypesMarker.DIRECT_METHOD_CALL_TARGET, filedInfoCN.getDeclaredConstructors().get(0));
        ctorFieldInfo.setNodeMetaData(StaticTypesMarker.INFERRED_TYPE, filedInfoCN);

        final Statement fieldMethodReturnExpression = returnS(
                castX(
                        filedInfoCN,
                        ctorFieldInfo
                )
        );

        BlockStatement body = block(
                constraintsDeclarationExpression,
                fieldConstraintDeclarationStatement,
                fieldMethodReturnExpression
        );

        addGeneratedMethod(
                classNode,
                transformNameIntoMethodName(fieldNode),
                Opcodes.ACC_PUBLIC,
                GenericsUtils.makeClassSafeWithGenerics(FieldInfo.class, castTypeToClass(fieldNode.getType())),
                Parameter.EMPTY_ARRAY,
                ClassNode.EMPTY_ARRAY,
                body
        );
    }

    private static ClassNode castTypeToClass(final ClassNode classNode) {
        if (!classNode.isPrimaryClassNode())
            if (ClassHelper.boolean_TYPE.equals(classNode)) {
                printOut("castTypeToClass: Boolean_TYPE: " + classNode.getName());
                return ClassHelper.Boolean_TYPE;
            } else if (ClassHelper.int_TYPE.equals(classNode)) {
                printOut("castTypeToClass: Integer_TYPE: " + classNode.getName());
                return ClassHelper.Integer_TYPE;
            }
        return classNode;
    }

    /**
     * add getter underscore methods
     *
     */
    private static void addGetMethodMethodNode(final ClassNode classNode, final MethodNode methodNode) {
        String genMethodName = transformNameIntoMethodName(methodNode);
        printOut("addGetMethodMethodNode: " + classNode.getName() + "::" + genMethodName + " " + methodNode.getReturnType());

        if (debugEnum == DebugEnum.ONLY_FIELD) {
            printOut("return " + debugEnum);
        }

        final BlockStatement body = new BlockStatement();

        StaticMethodCallExpression callGetMethod = callX(
                classNode,
                "getMethod",
                args(constX(methodNode.getName()))
        );

//        callGetMethod.setNodeMetaData(StaticTypesMarker.DIRECT_METHOD_CALL_TARGET, methodNode)
//        callGetMethod.setNodeMetaData(StaticTypesMarker.INFERRED_TYPE, make(Method))

        MethodCallExpression callMethod = callThisX(methodNode.getName());

        callMethod.putNodeMetaData(StaticTypesMarker.DIRECT_METHOD_CALL_TARGET, methodNode);
        callMethod.putNodeMetaData(StaticTypesMarker.INFERRED_TYPE, methodNode.getReturnType());

        ConstructorCallExpression ctorGetMethodReturn = ctorX(
                GRM_TYPE,
                args(
                        callGetMethod,
                        callMethod
                )
        );

        ctorGetMethodReturn.setNodeMetaData(StaticTypesMarker.DIRECT_METHOD_CALL_TARGET, GRM_TYPE.getDeclaredConstructors().get(0));
        ctorGetMethodReturn.setNodeMetaData(StaticTypesMarker.INFERRED_TYPE, GRM_TYPE);

        body.addStatement(
                returnS(
                        ctorGetMethodReturn
                )
        );

        addGeneratedMethod(
                classNode,
                genMethodName,
                Opcodes.ACC_PUBLIC,
                GRM_TYPE,
                Parameter.EMPTY_ARRAY,
                ClassNode.EMPTY_ARRAY,
                body
        );
    }
}