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
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;

import static com.tinyfight.annotation.Configuration.BASE_GENERATOR_PACKAGE;
import static com.tinyfight.annotation.Configuration.COMPONENT_NAME;
import static com.tinyfight.annotation.Configuration.INJECTOR_ACTIVITY_NAME;
import static com.tinyfight.annotation.Configuration.MODULE_NAME;
import static com.tinyfight.annotation.Configuration.PACKAGE_NAME;

/**
 * Created by tinyfight on 2018/1/23.
 * 生成基类的Activity。自动实现好了注入代码，不需要在各个Activity里再在重写autoInject方法，把自身注入进去
 */

public class InjectorActivityGenerator {
    private Filer mFiler;
    private Types mTypeUtil;
    private TypeSpec mTypeSpec;
    private TypeSpec.Builder mTypeSpecBuilder;
    private MethodSpec.Builder mMethodSpecBuilder;

    public InjectorActivityGenerator(Filer filer, Types typeUtil){
        mFiler = filer;
        mTypeUtil = typeUtil;
        mTypeSpecBuilder = TypeSpec.classBuilder(INJECTOR_ACTIVITY_NAME);
    }

    //生成类名以及extend implement,以及类名前段修饰符等信息
    private void generateModifier(){
        mTypeSpecBuilder.addModifiers(Modifier.PUBLIC,Modifier.ABSTRACT)
                .superclass(ClassName.get(BASE_GENERATOR_PACKAGE,"BaseRxActivity"));

    }

    /**
     * 生成方法
     */
    private void generatorMethodWrapper() {
        mMethodSpecBuilder = MethodSpec.methodBuilder("autoInject")
                                        .addAnnotation(Override.class)
                                        .addModifiers(Modifier.PROTECTED)
                                        .returns(TypeName.VOID);
    }

    /**
     * 生成代码块
     */
    private void generatorCodeBlock(Element element) {
        TypeElement enclosingElement;

        if(element instanceof TypeElement){
            enclosingElement = (TypeElement) element;
        }else if(element.getEnclosingElement() instanceof TypeElement){
            enclosingElement = (TypeElement) element.getEnclosingElement();
        }else {
            return;
        }

        //获取到类所在的包名
        String elementPackage = MoreElements.getPackage(enclosingElement).getQualifiedName().toString();
        //获取到全路径名，剪切掉包名，然后替换.为$,形成相应的编译后的class类名
        String className = enclosingElement.getQualifiedName().toString().substring(elementPackage.length() + 1).replace('.','$');

        ClassName bindingClassName = ClassName.get(elementPackage,className);

        //获取到dagger生成Component中的传入module生成Builder的方法
        //此时dagger还没有跑他自己的annotationProcessor,需要我们手动按照Dagger的规则去获取
        String moduleMethodName = String.format("%s%s",MODULE_NAME.substring(0,1).toLowerCase(),MODULE_NAME.substring(1));
        //此处根据Dagger的注入建造方式，统一生成每个类里面inject()方法的代码。
        //根据JavaPoet $L可以代替常量占据位置
        String code = String.format("%s.builder().%s(new $L(getApplication(),this)).build().inject(($L)this)","Dagger" + COMPONENT_NAME, moduleMethodName);
        CodeBlock codeBlock = CodeBlock.builder()
                                .beginControlFlow("if(this instanceof $L)", bindingClassName)
                                .addStatement(code,ClassName.get(PACKAGE_NAME,MODULE_NAME),bindingClassName)
                                .endControlFlow()
                                .build();
        mMethodSpecBuilder.addCode(codeBlock);
    }

    //可以看成在编译期生成对应的.java文件
    private void generatorFile(TypeSpec typeSpec){
        try {
            JavaFile.builder(PACKAGE_NAME, typeSpec)
                    .build().writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean generator(Set<? extends Element> elements){
        if(elements == null || elements.isEmpty()){
            return false;
        }
        //首先生成类修饰
        generateModifier();
        //再生成相应的方法修饰
        generatorMethodWrapper();

        for (Element element : elements){
            if(element.getKind() != ElementKind.CLASS){
                //此处认为有一个不是 类，就不会继续往下走，后面的均不会生成相应的方法体
                //还有个解决方案是跳过本次，进行下一次
                return false;
            }
            generatorCodeBlock(element);
        }

        //类中添加所有相应的方法
        mTypeSpecBuilder.addMethod(mMethodSpecBuilder.build());
        //生成已经添加所有方法的类
        mTypeSpec = mTypeSpecBuilder.build();
        //将类写入文件中，生成.java文件
        generatorFile(mTypeSpec);
        return true;
    }
}
