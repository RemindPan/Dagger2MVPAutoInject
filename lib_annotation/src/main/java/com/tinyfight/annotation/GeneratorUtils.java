package com.tinyfight.annotation;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import static com.tinyfight.annotation.Configuration.ACTIVITY_TYPE;
import static com.tinyfight.annotation.Configuration.ACTIVITY_TYPES;
import static com.tinyfight.annotation.Configuration.INJECTOR_FRAGMENT_NAME;

/**
 * Created by tinyfight on 2018/1/23.
 */

public class GeneratorUtils {

    public static TypeMirror getPresenterTypeMirror(AutoInject annotation) {
        try {
            annotation.presenter();
        } catch(MirroredTypeException mte ) {
            return mte.getTypeMirror();
        }
        return null;
    }

    public static TypeMirror getContractTypeMirror(AutoInject annotation) {
        try {
            annotation.contract();
        } catch(MirroredTypeException mte ) {
            return mte.getTypeMirror();
        }
        return null;
    }


    public static boolean isSubOfActivity(TypeMirror typeMirror){
        boolean isActivity = isSubOfType(typeMirror,ACTIVITY_TYPE);
        return isActivity;
    }

    public static boolean isSubOfActivity1(TypeMirror typeMirror){
        boolean isActivity = isSubOfType1(typeMirror,ACTIVITY_TYPES);
        return isActivity;
    }

    public static boolean isSubOfFragment(TypeMirror typeMirror){
        boolean isFrag = isSubOfType(typeMirror, INJECTOR_FRAGMENT_NAME);
        return isFrag;
    }

    public static boolean isSubOfType1(TypeMirror typeMirror,String[] type){
        if(isTypeEqual1(typeMirror,type)){
            return true;
        }

        //判断是不是类
        if(typeMirror.getKind() != TypeKind.DECLARED){
            return false;
        }

        DeclaredType declaredType = (DeclaredType)typeMirror;

        //Returns the actual type arguments of this type.
        // For a type nested within a parameterized type (such as Outer<String>.Inner<Number>),
        // only the type arguments of the innermost type are included.
        //即获取最里面的内部类。看是不是相应的类型
//        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
//        if(typeArguments.size() > 0){
//            StringBuilder typeString = new StringBuilder(declaredType.asElement().toString());
//            typeString.append('<');
//            for(int i = 0 ; i < typeArguments.size(); i++){
//                if(i > 0){
//                    typeString.append(',');
//                }
//                typeString.append('?');
//            }
//            typeString.append('>');
//            if(typeString.toString().equals(type)){
//                return true;
//            }
//        }

        Element element = declaredType.asElement();
        if(!(element instanceof TypeElement)){
            return false;
        }

        //判断父类，是不是相应的类型
        TypeElement typeElement = (TypeElement) element;
        TypeMirror superType = typeElement.getSuperclass();
        if(isSubOfType1(superType,type)){    //递归调用直到最顶级父类
            return true;
        }


        //接口类是不是相应的类型
        for(TypeMirror interfaceType : typeElement.getInterfaces()){
            if(isSubOfType1(interfaceType,type)){
                return true;
            }
        }
        return false;
    }


    public static boolean isSubOfType(TypeMirror typeMirror,String type){
        if(isTypeEqual(typeMirror,type)){
            return true;
        }

        //判断是不是类
        if(typeMirror.getKind() != TypeKind.DECLARED){
            return false;
        }

        DeclaredType declaredType = (DeclaredType)typeMirror;

        //Returns the actual type arguments of this type.
        // For a type nested within a parameterized type (such as Outer<String>.Inner<Number>),
        // only the type arguments of the innermost type are included.
        //即获取最里面的内部类。看是不是相应的类型
//        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
//        if(typeArguments.size() > 0){
//            StringBuilder typeString = new StringBuilder(declaredType.asElement().toString());
//            typeString.append('<');
//            for(int i = 0 ; i < typeArguments.size(); i++){
//                if(i > 0){
//                    typeString.append(',');
//                }
//                typeString.append('?');
//            }
//            typeString.append('>');
//            if(typeString.toString().equals(type)){
//                return true;
//            }
//        }

        Element element = declaredType.asElement();
        if(!(element instanceof TypeElement)){
            return false;
        }

        //判断父类，是不是相应的类型
        TypeElement typeElement = (TypeElement) element;
        TypeMirror superType = typeElement.getSuperclass();
        if(isSubOfType(superType,type)){    //递归调用直到最顶级父类
            return true;
        }


        //接口类是不是相应的类型
        for(TypeMirror interfaceType : typeElement.getInterfaces()){
            if(isSubOfType(interfaceType,type)){
                return true;
            }
        }
        return false;
    }

    private static boolean isTypeEqual(TypeMirror typeMirror, String type){
        return typeMirror.toString().contains(type);
//        return type.equals(typeMirror.toString());
    }

    private static boolean isTypeEqual1(TypeMirror typeMirror, String[] types){
        for(String type : types){
            if(!(typeMirror.toString().contains(type))){
                return false;
            }
        }
        return true;
    }

}
