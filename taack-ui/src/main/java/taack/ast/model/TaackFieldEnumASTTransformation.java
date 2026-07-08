package taack.ast.model;

import groovy.lang.Closure;
import groovyjarjarasm.asm.Opcodes;
import org.apache.groovy.ast.tools.ClassNodeUtils;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.ast.tools.GenericsUtils;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.StringGroovyMethods;
import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.syntax.Types;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.stc.StaticTypesMarker;
import taack.ast.annotation.TaackFieldEnum;
import taack.ast.type.FieldConstraint;
import taack.ast.type.FieldInfo;
import taack.ast.type.GetMethodReturn;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Handles generation of code for the @TaackFieldEnum annotation.
 */
public final class TaackFieldEnumASTTransformation extends AbstractASTTransformation {
    private static final void printOut(String outputString) {
        if (trace) DefaultGroovyMethods.println(this, outputString);
    }

    private static final String transformNameIntoMethodName(final String fieldNodeName) {
        return "get" + StringGroovyMethods.capitalize(fieldNodeName) + "_";
    }

    private static final String transformNameIntoMethodName(final MethodNode methodNode) {
        return methodNode.getName() + "_";
    }

    private static final String transformNameIntoMethodName(final FieldNode fieldNode) {
        return transformNameIntoMethodName(fieldNode.getName());
    }

    @Override
    public void visit(ASTNode[] nodes, final SourceUnit source) {

        printOut("source " + source.getName());
        init(nodes, source);

        AnnotationNode node = (AnnotationNode) nodes[0];
        AnnotatedNode parent = (AnnotatedNode) nodes[1];
        if (!TFE_TYPE.equals(node.getClassNode())) return;


        if (parent instanceof ClassNode) {
            ClassNode cNode = (ClassNode) parent;
            if (!checkNotInterface(cNode, TFE_TYPE_NAME)) return;


            final Set<String> avoidDuplicate = (Set<String>) new ArrayList();
            final ClassNode classNode = (ClassNode) nodes[1];
            final Map<String, FieldConstraint.Constraints> constraintsMap = dumpConstraints(classNode);
            final List<MethodNode> methods = new ArrayList<MethodNode>(classNode.getMethods());

            DefaultGroovyMethods.each(methods, new Closure<Void>(this, this) {
                public void doCall(MethodNode it) {
                    if (!DefaultGroovyMethods.contains(DOMAIN_RESERVED, it.getName())) {
                        // Filter pure Getters
                        if (it.getName().startsWith("get") && !it.getName().contains("_") && !it.getName().contains("Solr") && !it.getName().contains("Iterator") && DefaultGroovyMethods.size(it.getParameters()) == 0 && (it.getModifiers() & Opcodes.ACC_PRIVATE) == 0) {

                            if (debugEnum.equals(DebugEnum.NORMAL) || debugEnum.equals(DebugEnum.ONLY_METHOD)) {
                                addGetMethodMethodNode(classNode, it);
                            }

                        }

                    }

                }

                public void doCall() {
                    doCall(null);
                }

            });

            DefaultGroovyMethods.each(classNode.getFields(), new Closure(this, this) {
                public Object doCall(final FieldNode fIt) {
                    // hasMany fields are added after semantic analysis
                    if (fIt.getName().equals("belongsTo")) {
                        if (fIt.getInitialExpression() instanceof MapExpression) {

                            MapExpression me = DefaultGroovyMethods.asType(fIt.getInitialExpression(), MapExpression.class);
                            return DefaultGroovyMethods.each(me.getMapEntryExpressions(), new Closure(TaackFieldEnumASTTransformation.this, TaackFieldEnumASTTransformation.this) {
                                public Object doCall(Object mee) {
                                    final ConstantExpression keyExpression = DefaultGroovyMethods.asType(((MapEntryExpression) mee).getKeyExpression(), ConstantExpression.class);
                                    final ClassExpression valueClassExpression = DefaultGroovyMethods.asType(((MapEntryExpression) mee).getValueExpression(), ClassExpression.class);
                                    FieldConstraint.Constraints constraints = constraintsMap.get(keyExpression.getValue());
                                    final String fieldName = DefaultGroovyMethods.asType(keyExpression.getValue(), String.class);
                                    if (!avoidDuplicate.contains(fieldName)) {
                                        FieldNode fieldNode = new FieldNode(fieldName, 2, valueClassExpression.getType(), classNode, null);
                                        if (debugEnum.equals(DebugEnum.NORMAL) || debugEnum.equals(DebugEnum.ONLY_FIELD)) {
                                            addFieldMethodNode(classNode, fieldNode, null, constraints);
                                            printOut(valueClassExpression.toString());
                                            printOut("Method " + fIt.getName() + " added " + fieldName + ": " + String.valueOf(valueClassExpression.getType()));
                                            return avoidDuplicate.add(fieldName);
                                        }

                                    } else {
                                        printOut("Avoid duplicates for " + String.valueOf(keyExpression.getValue()));
                                    }

                                }

                            });
                        } else if (fIt.getInitialExpression() instanceof ListExpression) {

                            ListExpression le = DefaultGroovyMethods.asType(fIt.getInitialExpression(), ListExpression.class);
                            return DefaultGroovyMethods.each(le.getExpressions(), new Closure<Void>(TaackFieldEnumASTTransformation.this, TaackFieldEnumASTTransformation.this) {
                                public void doCall(Expression it) {
                                    final ClassExpression valueClassExpression = DefaultGroovyMethods.asType(it, ClassExpression.class);
                                    // TODO
                                    printOut("valueClassExpression: " + String.valueOf(valueClassExpression) + " NOT DONE !");
                                }

                                public void doCall() {
                                    doCall(null);
                                }

                            });
                        }

                    } else if (!(fIt.getName().startsWith("get") || fIt.getName().contains("_") || fIt.getName().contains("$") || fIt.getName().contains("hasMany") || DefaultGroovyMethods.contains(DOMAIN_RESERVED, fIt.getName()) || fIt.getType().getName().contains("DetachedCriteria") && (fIt.getModifiers() & Opcodes.ACC_PRIVATE) != 0)) {

                        List<AnnotationNode> annotationNodes = fIt.getAnnotations(TFE_TYPE);
                        String constraintName = null;
                        FieldConstraint.Constraints constraints = constraintsMap.get(fIt.getName());
                        if (!annotationNodes.isEmpty()) {
                            AnnotationNode enumName = DefaultGroovyMethods.first(annotationNodes);
                            ConstantExpression constantExpression = (ConstantExpression) enumName.getMember("name");
                            constraintName = constantExpression.getValue();
                        }


                        if (!avoidDuplicate.contains(fIt.getName())) {
                            if (debugEnum.equals(DebugEnum.NORMAL) || debugEnum.equals(DebugEnum.ONLY_FIELD)) {
                                addFieldMethodNode(classNode, fIt, constraintName, constraints);
                                return avoidDuplicate.add(fIt.getName());
                            }

                        }

                    }

                }

            });
            if (debugEnum.equals(DebugEnum.NORMAL)) addSelfObjectMethod(classNode);
        }

    }

    /**
     * Add self underscore method
     *
     * @param classNode
     * @return
     */
    private static final MethodNode addSelfObjectMethod(final ClassNode classNode) {
        printOut("addSelfObjectMethod: " + classNode.getName());

        final VariableExpression fieldConstraintsVariable = GeneralUtils.varX("fieldConstraints", FC_TYPE);

        final ConstructorCallExpression ctorFC = GeneralUtils.ctorX(FC_TYPE, GeneralUtils.args(GeneralUtils.nullX(), GeneralUtils.nullX(), GeneralUtils.nullX()));

        ctorFC.setNodeMetaData(StaticTypesMarker.DIRECT_METHOD_CALL_TARGET, DefaultGroovyMethods.first(FC_TYPE.getDeclaredConstructors()));
        ctorFC.setNodeMetaData(StaticTypesMarker.INFERRED_TYPE, FC_TYPE);

        final Statement fieldConstraintDeclarationStatement = GeneralUtils.stmt(new DeclarationExpression(fieldConstraintsVariable, Token.newSymbol(Types.ASSIGN, -1, -1), ctorFC));

        final ClassNode fiNode = GenericsUtils.makeClassSafeWithGenerics(FieldInfo.class, classNode);

        final ConstructorCallExpression ctorFi = GeneralUtils.ctorX(fiNode, GeneralUtils.args(fieldConstraintsVariable, GeneralUtils.constX("selfObject"), GeneralUtils.varX("this", classNode)));

        ctorFi.setNodeMetaData(StaticTypesMarker.DIRECT_METHOD_CALL_TARGET, DefaultGroovyMethods.first(fiNode.getDeclaredConstructors()));
        ctorFi.setNodeMetaData(StaticTypesMarker.INFERRED_TYPE, fiNode);

        final Statement fieldMethodReturnExpression = GeneralUtils.returnS(GeneralUtils.castX(fiNode, ctorFi));

        final BlockStatement code = GeneralUtils.block(fieldConstraintDeclarationStatement, fieldMethodReturnExpression);

        return ClassNodeUtils.addGeneratedMethod(classNode, "getSelfObject_", Opcodes.ACC_PUBLIC, fiNode, Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, code);
    }

    public static Map<String, FieldConstraint.Constraints> dumpConstraints(final ClassNode classNode) {
        final FieldNode fn = classNode.getDeclaredField("constraints");

        if (fn == null) return new LinkedHashMap<String, FieldConstraint.Constraints>();

        final ClosureExpression ce = DefaultGroovyMethods.asType(fn.getInitialValueExpression(), ClosureExpression.class);
        final BlockStatement bs = DefaultGroovyMethods.asType(ce.getCode(), BlockStatement.class);
        final Map<String, FieldConstraint.Constraints> ret = new LinkedHashMap<String, FieldConstraint.Constraints>();

        DefaultGroovyMethods.each(bs.getStatements(), new Closure<List<MapEntryExpression>>(null, null) {
            public List<MapEntryExpression> doCall(Statement s) {
                if (s instanceof ExpressionStatement) {
                    Expression e = ((ExpressionStatement) s).getExpression();
                    if (e instanceof MethodCallExpression) {
                        final ConstantExpression cst = (ConstantExpression) ((MethodCallExpression) e).getMethod();
                        TupleExpression te = (TupleExpression) ((MethodCallExpression) e).getArguments();
                        NamedArgumentListExpression nal = (NamedArgumentListExpression) DefaultGroovyMethods.first(te.getExpressions());
                        final Map<String, Object> c = new LinkedHashMap<String, Object>();
                        return DefaultGroovyMethods.each(nal.getMapEntryExpressions(), new Closure<FieldConstraint.Constraints>(null, null) {
                            public FieldConstraint.Constraints doCall(MapEntryExpression mee) {
                                Object key = ((ConstantExpression) mee.getKeyExpression()).getValue();
                                if (mee.getValueExpression() instanceof ConstantExpression) {
                                    Object value = ((ConstantExpression) mee.getValueExpression()).getValue();
                                    if (StringGroovyMethods.isCase("nullable", key)) {
                                        c.nullable = value;
                                    } else if (StringGroovyMethods.isCase("min", key)) {
                                        c.min = (int) value;
                                    } else if (StringGroovyMethods.isCase("max", key)) {
                                        c.max = (int) value;
                                    } else if (StringGroovyMethods.isCase("widget", key)) {
                                        c.widget = value;
                                    } else if (StringGroovyMethods.isCase("email", key)) {
                                        c.email = value;
                                    }
                                }

                                final Object object = c.get("nullable");
                                final Object object1 = c.get("email");
                                FieldConstraint.Constraints constraints = new FieldConstraint.Constraints(DefaultGroovyMethods.asType(c.get("widget"), String.class), DefaultGroovyMethods.asType(DefaultGroovyMethods.asBoolean(object) ? object : false, Boolean.class), DefaultGroovyMethods.asType(DefaultGroovyMethods.asBoolean(object1) ? object1 : false, Boolean.class), DefaultGroovyMethods.asType(c.get("min"), Integer.class), DefaultGroovyMethods.asType(c.get("max"), Integer.class));
                                return ret.put(cst.getValue().toString(), constraints);
                            }

                        });
                    }

                }

            }

        });
        return ret;
    }

    /**
     * Add class fields underscore methods
     *
     * @param classNode
     * @param fieldNode
     * @param constraintName
     * @param constraints
     */
    private static final void addFieldMethodNode(final ClassNode classNode, final FieldNode fieldNode, final String constraintName, final FieldConstraint.Constraints constraints) {
        printOut("addFieldMethodNode: " + classNode.getName() + "::" + fieldNode.getName());

        final VariableExpression constrVar = DefaultGroovyMethods.asBoolean(constraints) ? GeneralUtils.varX("constraints", ClassHelper.make(FieldConstraint.Constraints.class)) : null;

        if (debugEnum.equals(DebugEnum.ONLY_METHOD)) {
            printOut("return " + String.valueOf(debugEnum));
        }


        ClassNode fieldConstraintsCN = ClassHelper.make(FieldConstraint.Constraints.class);

        ConstructorCallExpression ctorFieldConstraints = DefaultGroovyMethods.asBoolean(constraints) ? GeneralUtils.ctorX(fieldConstraintsCN, GeneralUtils.args(GeneralUtils.constX(constraints.getWidget()), GeneralUtils.constX(constraints.getNullable()), GeneralUtils.constX(constraints.getEmail()), GeneralUtils.constX(constraints.getMin()), GeneralUtils.constX(constraints.getMax()))) : null;

        if (DefaultGroovyMethods.asBoolean(ctorFieldConstraints)) {
            ctorFieldConstraints.setNodeMetaData(StaticTypesMarker.DIRECT_METHOD_CALL_TARGET, DefaultGroovyMethods.first(fieldConstraintsCN.getDeclaredConstructors()));
            ctorFieldConstraints.setNodeMetaData(StaticTypesMarker.INFERRED_TYPE, fieldConstraintsCN);
        }


        final Statement constraintsDeclarationExpression = DefaultGroovyMethods.asBoolean(constraints) ? GeneralUtils.stmt(new DeclarationExpression(constrVar, Token.newSymbol(Types.ASSIGN, -1, -1), DefaultGroovyMethods.asBoolean(ctorFieldConstraints) ? ctorFieldConstraints : new EmptyExpression())) : new EmptyStatement();

        final ClassNode fieldConstraintCN = ClassHelper.make(FieldConstraint.class);
        final ClassNode fieldCN = ClassHelper.make(Field.class);


        final VariableExpression fieldConstraintsVariable = GeneralUtils.varX("fieldConstraints", fieldConstraintCN);

        final StaticMethodCallExpression sceGetDeclaredField = GeneralUtils.callX(classNode, "getDeclaredField", GeneralUtils.args(GeneralUtils.constX(fieldNode.getName())));

        sceGetDeclaredField.setNodeMetaData(StaticTypesMarker.DIRECT_METHOD_CALL_TARGET, "getDeclaredField");
        sceGetDeclaredField.setNodeMetaData(StaticTypesMarker.INFERRED_TYPE, fieldCN);

        final ConstructorCallExpression cceFieldConstraint = GeneralUtils.ctorX(fieldConstraintCN, GeneralUtils.args(DefaultGroovyMethods.asBoolean(constrVar) ? constrVar : GeneralUtils.nullX(), sceGetDeclaredField, new ConstantExpression(constraintName)));

        cceFieldConstraint.setNodeMetaData(StaticTypesMarker.DIRECT_METHOD_CALL_TARGET, DefaultGroovyMethods.first(fieldConstraintCN.getDeclaredConstructors()));
        cceFieldConstraint.setNodeMetaData(StaticTypesMarker.INFERRED_TYPE, fieldConstraintCN);

        final Statement fieldConstraintDeclarationStatement = GeneralUtils.stmt(new DeclarationExpression(fieldConstraintsVariable, Token.newSymbol(Types.ASSIGN, -1, -1), cceFieldConstraint));

        final ClassNode filedInfoCN = ClassHelper.make(FieldInfo.class);

        final ConstructorCallExpression ctorFieldInfo = GeneralUtils.ctorX(filedInfoCN, GeneralUtils.args(fieldConstraintsVariable, GeneralUtils.constX(fieldNode.getName()), GeneralUtils.varX(fieldNode.getName(), fieldNode.getType())));

        ctorFieldInfo.setNodeMetaData(StaticTypesMarker.DIRECT_METHOD_CALL_TARGET, DefaultGroovyMethods.first(filedInfoCN.getDeclaredConstructors()));
        ctorFieldInfo.setNodeMetaData(StaticTypesMarker.INFERRED_TYPE, filedInfoCN);

        final Statement fieldMethodReturnExpression = GeneralUtils.returnS(GeneralUtils.castX(filedInfoCN, ctorFieldInfo));

        BlockStatement body = GeneralUtils.block(constraintsDeclarationExpression, fieldConstraintDeclarationStatement, fieldMethodReturnExpression);

        ClassNodeUtils.addGeneratedMethod(classNode, transformNameIntoMethodName(fieldNode), Opcodes.ACC_PUBLIC, GenericsUtils.makeClassSafeWithGenerics(FieldInfo.class, castTypeToClass(fieldNode.getType())), Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, body);
    }

    /**
     * Add class fields underscore methods
     *
     * @param classNode
     * @param fieldNode
     * @param constraintName
     * @param constraints
     */
    private static final void addFieldMethodNode(final ClassNode classNode, final FieldNode fieldNode, final String constraintName) {
        TaackFieldEnumASTTransformation.addFieldMethodNode(classNode, fieldNode, constraintName, null);
    }

    /**
     * Add class fields underscore methods
     *
     * @param classNode
     * @param fieldNode
     * @param constraintName
     * @param constraints
     */
    private static final void addFieldMethodNode(final ClassNode classNode, final FieldNode fieldNode) {
        TaackFieldEnumASTTransformation.addFieldMethodNode(classNode, fieldNode, null, null);
    }

    private static ClassNode castTypeToClass(final ClassNode classNode) {
        if (!classNode.isPrimaryClassNode()) {
            if (Boolean.class.isAssignableFrom(classNode.getTypeClass())) {
                return ClassHelper.make(Boolean.class);
            } else if (Integer.class.isAssignableFrom(classNode.getTypeClass())) {
                return ClassHelper.make(Integer.class);
            }

        }

        return classNode;
    }

    /**
     * add getter underscore methods
     *
     * @param classNode
     * @param methodNode
     */
    private static final void addGetMethodMethodNode(final ClassNode classNode, final MethodNode methodNode) {
        String genMethodName = transformNameIntoMethodName(methodNode);
        printOut("addGetMethodMethodNode: " + classNode.getName() + "::" + genMethodName + " " + String.valueOf(methodNode.getReturnType()));

        if (debugEnum.equals(DebugEnum.ONLY_FIELD)) {
            printOut("return " + String.valueOf(debugEnum));
        }


        final BlockStatement body = new BlockStatement();

        StaticMethodCallExpression callGetMethod = GeneralUtils.callX(classNode, "getMethod", GeneralUtils.args(GeneralUtils.constX(methodNode.getName())));

//        callGetMethod.setNodeMetaData(StaticTypesMarker.DIRECT_METHOD_CALL_TARGET, methodNode)
//        callGetMethod.setNodeMetaData(StaticTypesMarker.INFERRED_TYPE, make(Method))

        MethodCallExpression callMethod = GeneralUtils.callThisX(methodNode.getName());

        callMethod.putNodeMetaData(StaticTypesMarker.DIRECT_METHOD_CALL_TARGET, methodNode);
        callMethod.putNodeMetaData(StaticTypesMarker.INFERRED_TYPE, methodNode.getReturnType());

        ConstructorCallExpression ctorGetMethodReturn = GeneralUtils.ctorX(GRM_TYPE, GeneralUtils.args(callGetMethod, callMethod));

        ctorGetMethodReturn.setNodeMetaData(StaticTypesMarker.DIRECT_METHOD_CALL_TARGET, DefaultGroovyMethods.first(GRM_TYPE.getDeclaredConstructors()));
        ctorGetMethodReturn.setNodeMetaData(StaticTypesMarker.INFERRED_TYPE, GRM_TYPE);

        body.addStatement(GeneralUtils.returnS(ctorGetMethodReturn));

        ClassNodeUtils.addGeneratedMethod(classNode, genMethodName, Opcodes.ACC_PUBLIC, GRM_TYPE, Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, body);
    }

    private static final DebugEnum debugEnum = DebugEnum.NORMAL;
    private static final boolean trace = true;
    private static final ClassNode GRM_TYPE = ClassHelper.make(GetMethodReturn.class, false);
    private static final ClassNode FC_TYPE = ClassHelper.make(FieldConstraint.class);
    private static final ClassNode TFE_TYPE = ClassHelper.make(TaackFieldEnum.class);
    private static final String TFE_TYPE_NAME = "@" + TFE_TYPE.getNameWithoutPackage();
    private static final String[] DOMAIN_RESERVED = new ArrayList<String>(Arrays.asList("serialVersionUID", "self", "mapping", "constraints", "ui", "uiMap", "metaClass", "transients", "mappedBy", "log", "instanceControllersDomainBindingApi", "instanceConvertersApi", "getConstrainedProperties", "getProperties", "getDirtyPropertyNames", "getGormPersistentEntity", "getGormDynamicFinders", "getCount", "getAll", "getErrors"));

    public static enum DebugEnum {
        NORMAL, ONLY_FIELD, ONLY_METHOD;
    }
}
