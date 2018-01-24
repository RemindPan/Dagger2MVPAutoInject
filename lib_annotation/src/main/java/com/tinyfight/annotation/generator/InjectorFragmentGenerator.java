package com.tinyfight.annotation.generator;

import com.google.auto.common.MoreElements;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import static com.tinyfight.annotation.Configuration.AUTO_INJECT_METHOD;
import static com.tinyfight.annotation.Configuration.BASE_GENERATOR_PACKAGE;
import static com.tinyfight.annotation.Configuration.COMPONENT_NAME;
import static com.tinyfight.annotation.Configuration.INJECTOR_FRAGMENT_NAME;
import static com.tinyfight.annotation.Configuration.MODULE_NAME;
import static com.tinyfight.annotation.Configuration.PACKAGE_NAME;

/**
 * Created by tinyfight on 2018/1/23.
 */

public class InjectorFragmentGenerator {
    private TypeSpec mTypeSpec;
    private TypeSpec.Builder mTypeSpecBuilder;
    private MethodSpec mMethodSpec;
    private MethodSpec.Builder mMethodSpecBuilder;
    private Filer mFiler;

    public InjectorFragmentGenerator(Filer filer){
        mFiler = filer;
        mTypeSpecBuilder = TypeSpec.classBuilder(INJECTOR_FRAGMENT_NAME);
    }

    private void generateModifier(){
        mTypeSpecBuilder.addModifiers(Modifier.PUBLIC,Modifier.ABSTRACT)
                        .superclass(ClassName.get(BASE_GENERATOR_PACKAGE,"BaseRxFragment"));
    }

    private void generateMethodWrapper(){
        mMethodSpecBuilder = MethodSpec.methodBuilder(AUTO_INJECT_METHOD)
                                        .addAnnotation(Override.class)
                                        .addModifiers(Modifier.PROTECTED)
                                        .returns(TypeName.VOID);
    }

    private void generateCodeBlock(Element element){
        TypeElement enclosingElement;

        if(element instanceof TypeElement){
            enclosingElement = (TypeElement) element;
        }else if(element.getEnclosingElement() instanceof TypeElement){
            enclosingElement = (TypeElement) element.getEnclosingElement();
        }else {
            return;
        }

        String elementPackage = MoreElements.getPackage(enclosingElement).getQualifiedName().toString();
        String className = enclosingElement.getQualifiedName().toString().substring(elementPackage.length() + 1).replace('.','$');

        ClassName bindingClassName = ClassName.get(elementPackage,className);
        String moduleMethodName = String.format("%s%s",MODULE_NAME.substring(0,1).toLowerCase(),MODULE_NAME.substring(1));
        String code = String.format("%s.builder().%s(new $L(getActivity().getApplication(),this)).build().inject(($L)this)","Dagger" + COMPONENT_NAME,moduleMethodName);

        CodeBlock codeBlock = CodeBlock.builder()
                            .beginControlFlow("if(this instanceof $L)",bindingClassName)
                            .addStatement(code,ClassName.get(PACKAGE_NAME,MODULE_NAME), bindingClassName)
                            .endControlFlow()
                            .build();
        mMethodSpecBuilder.addCode(codeBlock);
    }

    private void generateFile(TypeSpec typeSpec){
        try {
            JavaFile.builder(PACKAGE_NAME,typeSpec).build().writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean generate(Set<? extends Element> elements){
        generateModifier();
        generateMethodWrapper();

        if(elements == null || elements.isEmpty()){
            return false;
        }

        for(Element element : elements){
            if(element.getKind() != ElementKind.CLASS){
                return false;
            }
            generateCodeBlock(element);
        }
        mTypeSpecBuilder.addMethod(mMethodSpecBuilder.build());
        mTypeSpec = mTypeSpecBuilder.build();
        generateFile(mTypeSpec);
        return true;
    }
}
