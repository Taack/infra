package taack.ast.controller

import grails.web.Action
import groovy.transform.CompileStatic
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.ClassExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.ListExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

// see http://docs.groovy-lang.org/latest/html/documentation/index.html#developing-ast-xforms
@CompileStatic
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
final class TaackDomainAutoASTTransformation implements ASTTransformation {
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
        def classNode = (ClassNode) nodes[1]
        def annotationNode = (AnnotationNode) nodes[0]
        def expression = (ListExpression) annotationNode.getMember('autoClass')
        def autoClasses = expression.expressions as List<ClassExpression>
        println "****** ADD TEST ACTION IN ${classNode.name}"
        autoClasses.each {
            def type = (ClassNode) it.type
            def name = type.name
            println "****** NAME: $name"
            addActionsForType(classNode, type)
        }
        classNode.addMethod(addTestAction())
    }

    private static String className(ClassNode typeToProcess) {
        def name = typeToProcess.name
        name.substring(name.lastIndexOf('.') + 1)
    }

    private static String simpleName(ClassNode typeToProcess) {
        def name = className(typeToProcess)
        name[0].toLowerCase() + name.substring(1)
    }

    private static String saveMethodName(ClassNode typeToProcess) {
        "save${className(typeToProcess)}"
    }

    private static String selectManyToManyMethodName(ClassNode typeToProcess) {
        "select${className(typeToProcess)}"
    }

    private static String selectManyToManyCloseModalMethodName(ClassNode typeToProcess) {
        "select${className(typeToProcess)}CloseModal"
    }

    private static void addActionsForType(ClassNode controller, ClassNode typeToProcess) {
//        if (!controller.methods*.name.contains(saveMethodName(typeToProcess))) {
//            controller.addMethod(methodSaveForType(typeToProcess))
//        }
//        if (!controller.methods*.name.contains(selectManyToManyMethodName(typeToProcess))) {
//            controller.addMethod(methodSelectManyToManyForType(typeToProcess))
//        }
        if (!controller.methods*.name.contains(selectManyToManyCloseModalMethodName(typeToProcess))) {
            controller.addMethod(methodSelectCloseModalForType(typeToProcess))
        }
    }

    private static MethodNode methodSaveForType(ClassNode typeToProcess) {
        println "********* Adding Method ${saveMethodName(typeToProcess)}"

    }

    private static MethodNode methodSelectManyToManyForType(ClassNode typeToProcess) {
        println "********* Adding Method ${selectManyToManyMethodName(typeToProcess)}"

    }

    private static MethodNode methodSelectCloseModalForType(ClassNode typeToProcess) {
        println "********* Adding Method ${selectManyToManyCloseModalMethodName(typeToProcess)}"
        BlockStatement code = new BlockStatement([
                new ExpressionStatement(
                        new MethodCallExpression(
                                new VariableExpression('taackUiSimpleService'),
                                'closeModal',
                                new ArgumentListExpression(
                                        new MethodCallExpression(
                                                new VariableExpression(simpleName(typeToProcess)),
                                                "ident",
                                                [] as ArgumentListExpression
                                        ),
                                        new MethodCallExpression(
                                                new VariableExpression(simpleName(typeToProcess)),
                                                "toString",
                                                [] as ArgumentListExpression
                                        )
                                )
                        )
                )
        ] as List<Statement>, new VariableScope())

        def mn = new MethodNode(
                selectManyToManyCloseModalMethodName(typeToProcess),
                MethodNode.ACC_PUBLIC,
                ClassHelper.VOID_TYPE,
                [new Parameter(
                        typeToProcess, simpleName(typeToProcess)
                )] as Parameter[],
                [] as ClassNode[],
                code
        )
        mn.addAnnotation(new AnnotationNode(
                new ClassNode(Action)
        ))
        mn

    }

    private static MethodNode addTestAction() {

        BlockStatement code = new BlockStatement([
                new ExpressionStatement(
                        new MethodCallExpression(
                                new VariableExpression('this'),
                                'render',
                                new ConstantExpression('Totototo')
                        )
                )
        ] as List<Statement>, new VariableScope())

        def mn = new MethodNode(
                "test",
                MethodNode.ACC_PUBLIC,
                ClassHelper.VOID_TYPE,
                [] as Parameter[],
                [] as ClassNode[],
                code
        )
        mn.addAnnotation(new AnnotationNode(
                new ClassNode(Action)
        ))
        mn
    }

}
