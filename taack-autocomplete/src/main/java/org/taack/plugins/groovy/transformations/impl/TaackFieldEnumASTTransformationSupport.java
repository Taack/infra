package org.taack.plugins.groovy.transformations.impl;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.impl.light.LightFieldBuilder;
import com.intellij.psi.impl.light.LightMethodBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.groovy.transformations.AstTransformationSupport;
import org.jetbrains.plugins.groovy.transformations.TransformationContext;

import java.util.ArrayList;

public class TaackFieldEnumASTTransformationSupport implements AstTransformationSupport {
    static final String[] reservedNames = {"serialVersionUID", "self", "mapping", "constraints", "ui", "uiMap", "metaClass", "hasMany", "belongsTo"};
    static final String ORIGIN_INFO = "via @TaackEnumField";

    @Override
    public void applyTransformation(@NotNull TransformationContext context) {
        if (context.getCodeClass().getAnnotation("taack.ast.annotation.TaackFieldEnum") != null) {
//            for (var a : context.getCodeClass().getMethods()) {
//                final String name = a.getName();
//                if (name.startsWith("get") && !name.contains("_") && !name.contains("Solr") &&
//                        a.getParameterList().getParametersCount() == 0) {
//                    context.addMethod(createGetterAccessorMethod(context.getCodeClass(), a));
//                }
//            }
            var methods =  new ArrayList<>(context.getMethods());
            for (var a : methods) {
                final String name = a.getName();
                if (name.startsWith("get") && !name.contains("_") && !name.contains("Solr") &&
                        a.getParameterList().getParametersCount() == 0) {
                    context.addMethod(createGetterAccessorMethod(context.getCodeClass(), a));
                }
            }

            for (var a : context.getFields()) {
                final String name = a.getName();
                boolean addMethod = true;
                for (var b : reservedNames) {
                    if (b.equals(name)) {
                        if (name.equals("belongsTo")) {
                            for (var c : a.getInitializerGroovy().getChildren()) {
                                LightFieldBuilder fieldBuilder = new LightFieldBuilder(c.getFirstChild().getText(), c.getLastChild().getText(), c);
                                context.addMethod(createFieldInfoAccessorMethod(context.getCodeClass(), fieldBuilder));
                            }
                        }
                        addMethod = false;
                        break;
                    }
                }
                if (addMethod && !(name.contains("_") || name.contains("$") || a.getType().getCanonicalText().contains("DetachedCriteria"))) {
                    context.addMethod(createFieldInfoAccessorMethod(context.getCodeClass(), a));
                }
            }
            context.addMethod(createSelfObjectMethod(context.getCodeClass()));
        }
    }

    @NotNull
    private LightMethodBuilder createSelfObjectMethod(@NotNull PsiClass enclosingClass) {
        final LightMethodBuilder builderMethod = new LightMethodBuilder(enclosingClass.getManager(), "getSelfObject_");
        builderMethod.addModifier(PsiModifier.PUBLIC);
        builderMethod.setOriginInfo(ORIGIN_INFO);
        builderMethod.setMethodReturnType("taack.ast.type.FieldInfo");
        return builderMethod;

    }

    @NotNull
    private LightMethodBuilder createFieldInfoAccessorMethod(@NotNull PsiClass enclosingClass, @NotNull PsiField grField) {
        final String name = "get" + grField.getName().substring(0, 1).toUpperCase() + grField.getName().substring(1) + '_';
        final LightMethodBuilder builderMethod = new LightMethodBuilder(enclosingClass.getManager(), name);
        builderMethod.addModifier(PsiModifier.PUBLIC);
        builderMethod.setOriginInfo(ORIGIN_INFO);

//        builderMethod.setMethodReturnType("taack.ast.type.FieldInfo<"+ grField.getType().getCanonicalText() +">");
        builderMethod.setMethodReturnType("taack.ast.type.FieldInfo");
        return builderMethod;
    }

    @NotNull
    private LightMethodBuilder createGetterAccessorMethod(@NotNull PsiClass builderClass, @NotNull PsiMethod psiMethod) {
        final String name = psiMethod.getName() + '_';
        final LightMethodBuilder builderMethod = new LightMethodBuilder(builderClass.getManager(), name);
        builderMethod.addModifier(PsiModifier.PUBLIC);
        builderMethod.setOriginInfo(ORIGIN_INFO);
        builderMethod.setMethodReturnType("taack.ast.type.GetMethodReturn");
        return builderMethod;

    }
}
