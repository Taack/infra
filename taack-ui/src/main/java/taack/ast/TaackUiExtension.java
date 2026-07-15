package taack.ast;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassCodeVisitorSupport;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.AttributeExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCall;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.transform.stc.AbstractTypeCheckingExtension;
import org.codehaus.groovy.transform.stc.StaticTypeCheckingVisitor;
import org.grails.datastore.gorm.GormEntity;

import taack.ast.type.FieldInfo;

public class TaackUiExtension extends AbstractTypeCheckingExtension {

	static Set<ClassNode> mustAddGormInterface = new HashSet<>();
	
	private static boolean addGormInterface(ClassNode classNode, boolean force) {
		
		ModuleNode m = classNode.getModule();
		
		if (force || (m != null && m.getContext().getName().contains("domain")) || (m == null && mustAddGormInterface.contains(classNode))) {
			classNode.addInterface(ClassHelper.make(GormEntity.class));
			//handled = true
			mustAddGormInterface.add(classNode);
			return true;
		} else if (m == null)
			System.out.println("classNode.module is null for $classNode");
		return false;
	}

	static void extensionLog(String trace) {
		if (true)
			System.out.println("TaackUiExtension::" + trace);
	}
	
    public TaackUiExtension(final StaticTypeCheckingVisitor typeCheckingVisitor) {
        super(typeCheckingVisitor);
    }

    @Override
    public boolean handleUnresolvedVariableExpression(final VariableExpression vexp) {
    	extensionLog("handleUnresolvedVariableExpression " + vexp);
//        if ("robot".equals(vexp.getName())) {
//            storeType(vexp, ClassHelper.make(Robot.class));
//            setHandled(true);
//            return true;
//        }
        return false;
    }

	@Override
	public void setup() {
		// TODO Auto-generated method stub
		super.setup();
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
	}

	@Override
	public boolean handleUnresolvedProperty(PropertyExpression pexp) {
		// TODO Auto-generated method stub
		String propAsString = pexp.getPropertyAsString();
    	extensionLog("handleUnresolvedProperty propAsString: " + propAsString);
		if (propAsString.endsWith("_")) {
			storeType(pexp,classNodeFor(FieldInfo.class));
			setHandled(true);
			return true;
		} else if (propAsString.equals("id")) {
			storeType(pexp,classNodeFor(Long.class));
			setHandled(true);
			return true;
		}    	
		return false;
	}

	@Override
	public boolean handleUnresolvedAttribute(AttributeExpression aexp) {
		// TODO Auto-generated method stub
    	extensionLog("handleUnresolvedAttribute " + aexp.getText());
		return super.handleUnresolvedAttribute(aexp);
	}

	@Override
	public List<MethodNode> handleMissingMethod(ClassNode receiver, String name, ArgumentListExpression argumentList,
			ClassNode[] argumentTypes, MethodCall call) {
		// TODO Auto-generated method stub
    	extensionLog("handleMissingMethod receiver: " + receiver.getName()  + " name: " + name);
		return super.handleMissingMethod(receiver, name, argumentList, argumentTypes, call);
	}

	@Override
	public boolean handleIncompatibleAssignment(ClassNode lhsType, ClassNode rhsType, Expression assignmentExpression) {
		// TODO Auto-generated method stub
    	extensionLog("handleIncompatibleAssignment lhsType: " + lhsType.getName()  + " rhsType: " + rhsType.getName());
		return super.handleIncompatibleAssignment(lhsType, rhsType, assignmentExpression);
	}

	@Override
	public List<MethodNode> handleAmbiguousMethods(List<MethodNode> nodes, Expression origin) {
		// TODO Auto-generated method stub
    	extensionLog("handleAmbiguousMethods ");
		return super.handleAmbiguousMethods(nodes, origin);
	}

	@Override
	public boolean beforeVisitMethod(MethodNode node) {
		// TODO Auto-generated method stub
//    	extensionLog("beforeVisitMethod node: " + node.getName());
		return super.beforeVisitMethod(node);
	}

	@Override
	public void afterVisitMethod(MethodNode node) {
		// TODO Auto-generated method stub
//    	extensionLog("afterVisitMethod node: " + node);
		super.afterVisitMethod(node);
	}

	@Override
	public boolean beforeVisitClass(ClassNode node) {
		// TODO Auto-generated method stub
    	extensionLog("beforeVisitClass node: " + node.getName());
    	extensionLog("AUOAUAAUOAOUOUOA mustAddGormInterface " + mustAddGormInterface);
		return addGormInterface(node, false);
	}

	@Override
	public void afterVisitClass(ClassNode node) {
		// TODO Auto-generated method stub
    	extensionLog("afterVisitClass node: " + node.getName());
		super.afterVisitClass(node);
	}

	@Override
	public boolean beforeMethodCall(MethodCall call) {
		// TODO Auto-generated method stub
//    	extensionLog("beforeMethodCall call: " + call.getText());
		return super.beforeMethodCall(call);
	}

	@Override
	public void afterMethodCall(MethodCall call) {
		// TODO Auto-generated method stub
//    	extensionLog("afterMethodCall call: " + call.getText());
		super.afterMethodCall(call);
	}

	@Override
	public void onMethodSelection(Expression expression, MethodNode target) {
		// TODO Auto-generated method stub
    	extensionLog("onMethodSelection expression: " + expression.getText()  + " target: " + target.getText());
    	if (target.getText().contains("GormEntity")) {
    		extensionLog("AUOUOAUOAUOAUOAUOAUO " + target.getParameters());
    		for (Parameter p : target.getParameters()) {
    			extensionLog("Aauoaouauoauoauoauoauoauoauoaouauoauoauo " + p.getType());
    			if (p.getType().equals(ClassHelper.make(Class.class))) {
        			extensionLog("Aauoaouauoauoauoauoauoauoauoaouauoauoauo2 " + expression);
        			addGormInterface(p.getType(), true);
    			}
    		}
    	}
		super.onMethodSelection(expression, target);
	}

	@Override
	public boolean handleIncompatibleReturnType(ReturnStatement returnStatement, ClassNode inferredReturnType) {
		// TODO Auto-generated method stub
    	extensionLog("handleIncompatibleReturnType returnStatement: " + returnStatement.getText()  + " inferredReturnType: " + inferredReturnType.getText());
		return super.handleIncompatibleReturnType(returnStatement, inferredReturnType);
	}

	@Override
	public ClassNode getType(ASTNode exp) {
		// TODO Auto-generated method stub
    	extensionLog("getType exp: " + exp);
		return super.getType(exp);
	}

	@Override
	public void addStaticTypeError(String msg, ASTNode expr) {
		// TODO Auto-generated method stub
		super.addStaticTypeError(msg, expr);
	}

	@Override
	public void storeType(Expression exp, ClassNode cn) {
		// TODO Auto-generated method stub
		super.storeType(exp, cn);
	}

	@Override
	public boolean existsProperty(PropertyExpression pexp, boolean checkForReadOnly) {
		// TODO Auto-generated method stub
		return super.existsProperty(pexp, checkForReadOnly);
	}

	@Override
	public boolean existsProperty(PropertyExpression pexp, boolean checkForReadOnly, ClassCodeVisitorSupport visitor) {
		// TODO Auto-generated method stub
		return super.existsProperty(pexp, checkForReadOnly, visitor);
	}

	@Override
	public ClassNode[] getArgumentTypes(ArgumentListExpression args) {
		// TODO Auto-generated method stub
		return super.getArgumentTypes(args);
	}

	@Override
	public MethodNode getTargetMethod(Expression expression) {
		// TODO Auto-generated method stub
		return super.getTargetMethod(expression);
	}

	@Override
	public ClassNode classNodeFor(Class type) {
		// TODO Auto-generated method stub
		return super.classNodeFor(type);
	}

	@Override
	public ClassNode classNodeFor(String type) {
		// TODO Auto-generated method stub
		return super.classNodeFor(type);
	}

	@Override
	public ClassNode lookupClassNodeFor(String type) {
		// TODO Auto-generated method stub
		return super.lookupClassNodeFor(type);
	}

	@Override
	public ClassNode parameterizedType(ClassNode baseType, ClassNode... genericsTypeArguments) {
		// TODO Auto-generated method stub
		return super.parameterizedType(baseType, genericsTypeArguments);
	}

	@Override
	public ClassNode buildListType(ClassNode componentType) {
		// TODO Auto-generated method stub
		return super.buildListType(componentType);
	}

	@Override
	public ClassNode buildMapType(ClassNode keyType, ClassNode valueType) {
		// TODO Auto-generated method stub
		return super.buildMapType(keyType, valueType);
	}

	@Override
	public ClassNode extractStaticReceiver(MethodCall call) {
		// TODO Auto-generated method stub
		return super.extractStaticReceiver(call);
	}

	@Override
	public boolean isStaticMethodCallOnClass(MethodCall call, ClassNode receiver) {
		// TODO Auto-generated method stub
		return super.isStaticMethodCallOnClass(call, receiver);
	}

    
}
