package com.tinyfight.annotation.generator;

import com.google.auto.common.MoreElements;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import static com.tinyfight.annotation.Configuration.COMPONENT_NAME;
import static com.tinyfight.annotation.Configuration.MODULE_NAME;
import static com.tinyfight.annotation.Configuration.PACKAGE_NAME;

/**
 * Created by tinyfight on 2018/1/23.
 */

public class ComponentGenerator {
    private TypeSpec mTypeSpec;
    private TypeSpec.Builder mTypeSpecBuilder;
    private Filer mFiler;

    public ComponentGenerator(Filer filer){
        mFiler = filer;
        mTypeSpecBuilder = TypeSpec.interfaceBuilder(COMPONENT_NAME).addModifiers(Modifier.PUBLIC);
    }

    //为类生成该类的注解
    private void generateAnnotation(){
        AnnotationSpec annotationSpec
                = AnnotationSpec.builder(ClassName.get("dagger","Component"))
                                .addMember("modules", "$L.class",ClassName.get(PACKAGE_NAME,MODULE_NAME))
                                .build();
        mTypeSpecBuilder.addAnnotation(annotationSpec);
    }

    private void generateMethod(Element element){
        TypeElement enclosingElement;

        if(element instanceof TypeElement) {
            enclosingElement = (TypeElement) element;
        } else if(element.getEnclosingElement() instanceof TypeElement) {
            enclosingElement = (TypeElement) element.getEnclosingElement();
        } else {
            return;
        }

        String elementPackage = MoreElements.getPackage(element).getQualifiedName().toString();
        String className = enclosingElement.getQualifiedName().toString().substring(elementPackage.length() + 1).replace('.','$');

        ClassName bindingClassName = ClassName.get(elementPackage,className);
        ParameterSpec parameterSpec = ParameterSpec.builder(bindingClassName,"activity").build();

        MethodSpec methodSpec = MethodSpec.methodBuilder("inject")
                                .addModifiers(Modifier.PUBLIC,Modifier.ABSTRACT)
                                .addParameter(parameterSpec)
                                .build();
        mTypeSpecBuilder.addMethod(methodSpec);
    }

    private void generateFile(TypeSpec typeSpec){
        try {
            JavaFile.builder(PACKAGE_NAME,typeSpec).build().writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean generate(Set<? extends Element> elements){
        if(elements == null || elements.isEmpty()){
            return false;
        }

        generateAnnotation();

        for(Element element : elements){
            if(element.getKind() != ElementKind.CLASS){
                return false;
            }
            generateMethod(element);
        }

        mTypeSpec = mTypeSpecBuilder.build();
        generateFile(mTypeSpec);
        return true;
    }
}
