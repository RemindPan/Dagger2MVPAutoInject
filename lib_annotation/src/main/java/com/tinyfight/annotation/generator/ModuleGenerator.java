package com.tinyfight.annotation.generator;

import com.google.auto.common.MoreElements;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import com.tinyfight.annotation.AutoInject;
import com.tinyfight.annotation.GeneratorUtils;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import static com.tinyfight.annotation.Configuration.MODULE_NAME;
import static com.tinyfight.annotation.Configuration.PACKAGE_NAME;

/**
 * Created by tinyfight on 2018/1/23.
 *
 * Note: AnnotationDIModule对于activity的处理只需提供mActivity, 之后在各个provider中强行转换.
 *
 * 原理: 每个provide方法都会生成一个类(AnnotationDiModule_ProvideDetailPresenterFactory),
 *      之后在DaggerAnnotationDIComponent中对每个provider进行调用引用。
 *      但由于在注入时， BaseAutoInjectActivity的autoInject会根据当前instance做针对性的注入,
 *      因此DaggerAnnotationDIComponent中某些类型不匹配的provider不会被调用到.
 *
 *      (如注入Detail2Activity时， 对于detailActivityMembersInjector的inject()方法不会被调用，
 *          因此在AnnotationDIModule中每个provider中， mActivity被强行转化为不同的类不会出错)
 */

public class ModuleGenerator {
    private TypeSpec mTypeSpec;
    private TypeSpec.Builder mTypeSpecBuilder;
    private Filer mFiler;
    private Types mTypes;


    public ModuleGenerator(Filer filer, Types types){
        mFiler = filer;
        mTypes = types;
        mTypeSpecBuilder = TypeSpec.classBuilder(MODULE_NAME).addModifiers(Modifier.PUBLIC);
    }

    private void generateAnnotation() {
        AnnotationSpec annotationSpec = AnnotationSpec.builder(ClassName.get("dagger", "Module")).build();
        mTypeSpecBuilder.addAnnotation(annotationSpec);
    }

    private void generateProviders(){
        mTypeSpecBuilder.addField(ClassName.get("android.app","Application"),"mApplication",Modifier.PRIVATE);
        mTypeSpecBuilder.addField(ClassName.get("android.app","Activity"),"mActivity",Modifier.PRIVATE);

        ParameterSpec appparameterSpec = ParameterSpec.builder(ClassName.get("android.app", "Application"), "application").build();
        ParameterSpec activityparameterSpec = ParameterSpec.builder(ClassName.get("android.app", "Activity"), "activity").build();

        //module constructor
        MethodSpec constructorMethod = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(appparameterSpec)
                .addParameter(activityparameterSpec)
                .addStatement("this.mApplication = application")
                .addStatement("this.mActivity = activity")
                .build();

        mTypeSpecBuilder.addMethod(constructorMethod);
    }

    private void generatePresenterProvider(Element element){
        TypeElement enclosingElement;

        if(element instanceof TypeElement) {
            enclosingElement = (TypeElement) element;
        } else if(element.getEnclosingElement() instanceof TypeElement) {
            enclosingElement = (TypeElement) element.getEnclosingElement();
        } else {
            return;
        }

        // 1. prepare annotation
        AnnotationSpec annotationSpec = AnnotationSpec.builder(ClassName.get("dagger", "Provides")).build();

        // 2. generate presenter instance
        String elementPackage = MoreElements.getPackage(enclosingElement).getQualifiedName().toString();
        String className = enclosingElement.getQualifiedName().toString().substring(elementPackage.length() + 1).replace('.', '$');
        ClassName bindingClassName = ClassName.get(elementPackage, className);

        AutoInject annotation = enclosingElement.getAnnotation(AutoInject.class);
        TypeMirror presenterMirror = GeneratorUtils.getPresenterTypeMirror(annotation);
        TypeElement presenter = (TypeElement) mTypes.asElement(presenterMirror);

        TypeMirror contractMirror = GeneratorUtils.getContractTypeMirror(annotation);
        TypeElement contract = (TypeElement) mTypes.asElement(contractMirror);

        MethodSpec presenterMethodSpec = MethodSpec.methodBuilder("provide" + presenter.getSimpleName())
                .addAnnotation(annotationSpec)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return new $L(($L)mActivity)", presenter, ClassName.get(MoreElements.getPackage(contract).getQualifiedName().toString() + '.' + contract.getSimpleName(), "View"))
                .returns(ClassName.get(MoreElements.getPackage(contract).getQualifiedName().toString() + '.' + contract.getSimpleName(), "Presenter"))
                .build();

        mTypeSpecBuilder.addMethod(presenterMethodSpec);
    }

    private void generateFile(TypeSpec typeSpec) {
        try {
            JavaFile.builder(PACKAGE_NAME, typeSpec).build().writeTo(mFiler);
        } catch (IOException e) {
           e.printStackTrace();
        }
    }

    public boolean generate(Set<? extends Element> elements) {
        if (elements == null || elements.isEmpty()) {
            return false;
        }

        //1: generate annotation
        generateAnnotation();


        //2. generate methods

        //2.1 generate unique activity provider method
        generateProviders();

        //2.2 generate each presenter provider method
        for(Element element : elements) {
            if (element.getKind() != ElementKind.CLASS) {
                return false;
            }

            generatePresenterProvider(element);
        }

        //3. generate whole file
        mTypeSpec = mTypeSpecBuilder.build();
        generateFile(mTypeSpec);

        return true;
    }
}
