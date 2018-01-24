package com.tinyfight.annotation;

import com.google.auto.service.AutoService;
import com.tinyfight.annotation.generator.ComponentGenerator;
import com.tinyfight.annotation.generator.InjectorActivityGenerator;
import com.tinyfight.annotation.generator.InjectorFragmentGenerator;
import com.tinyfight.annotation.generator.ModuleGenerator;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Created by tinyfight on 2018/1/23.
 * Java API已经提供了扫描源码并解析注解的框架，
 * 我们可以继承AbstractProcessor类来提供实现自己的解析注解逻辑
 */

@AutoService(Processor.class)
public class AutoInjectProcessor extends AbstractProcessor{
    //实现注解逻辑需要的一些工具类
    private Elements mElements;     //元素相关的辅助类
    private Types mTypes;       //类型相关辅助类，比如类，接口，Enum
    private Filer mFiler;   //文件相关的辅助类
    private Messager mMessager;     //日志相关辅助类

    private InjectorActivityGenerator mActivityGenerator;
    private InjectorFragmentGenerator mFragmentGenerator;
    private ComponentGenerator mComponentGenerator;
    private ModuleGenerator mModuleGenerator;


    /**
     * init方法它会被注解处理工具调用，并输入ProcessingEnvironment参数
     * {@link ProcessingEnvironment} 会提供很多有用的工具类:
     *
     * {@link Elements},
     *      一个用来处理Element的工具类，
     *      源代码的每一个部分都是一个特定类型的Element，
     *      例如package对应PackageElement
     *      类本身对应对应TypeElement
     *      类成员变量对应VariableElement
     *      类方法对应ExecuteableElement
     *
     * {@link Types},我们使用TypeElement获取不到类本身信息，例如父类。
     *                  只能获得类名等信息。如果要获取类本身信息，这种信息需要通过TypeMirror获取。
     *                  可以通过调用elements.asType()获取元素的TypeMirror。
     *                  而Types是一个用来处理TypeMirror的工具。
     *
     * {@link Filer},创建文件使用。
     *
     * {@link Messager}
     * @param env
     */
    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        mElements = env.getElementUtils();
        mTypes = env.getTypeUtils();
        mFiler = env.getFiler();
        mMessager = env.getMessager();
        mComponentGenerator = new ComponentGenerator(mFiler);
        mModuleGenerator = new ModuleGenerator(mFiler,mTypes);
        mActivityGenerator = new InjectorActivityGenerator(mFiler,mTypes);
        mFragmentGenerator = new InjectorFragmentGenerator(mFiler);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if(set == null || set.isEmpty()){
            return true;
        }
        //获取到被AutoInject注解的Element，在本框架中，获取到的都为TypeElement
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(AutoInject.class);
        Set<Element> activityElements = new HashSet<>();
        Set<Element> fragElements = new HashSet<>();

        for(Element element : elements){
            if(GeneratorUtils.isSubOfActivity(element.asType())){
                activityElements.add(element);
            }else if(GeneratorUtils.isSubOfFragment(element.asType())){
                fragElements.add(element);
            }
        }
        mComponentGenerator.generate(elements);
        mModuleGenerator.generate(elements);
        mActivityGenerator.generator(activityElements);
        mFragmentGenerator.generate(fragElements);
        return true;
    }

    /**
     * 指定使用的java版本
     * @return
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * 这里必须指定，这个注解处理器是注册给哪个注解的。
     * 注意，它的返回值是一个字符串的集合，包含本处理器想要处理的注解类型的合法全称。
     * 换句话说，在这里定义你的注解处理器注册到哪些注解上。
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(AutoInject.class.getCanonicalName());
        return annotations;
    }

}
