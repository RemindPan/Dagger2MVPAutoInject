package com.tinyfight.annotation;

/**
 * Created by tinyfight on 2018/1/23.
 */

public class Configuration {
    //这里实在是太尬了。本来使用"android.app.Activity"，
    // 但是明显走不通，因为需要先生成BaseAutoInjectActivity，继承BaseRxActivity。
    //但是做判断的时候，还没有生成BaseAutoInjectActivity，
    // 所以从BaseBusinessActivity -> BaseAutoInjectActivity -> BaseRxActivity ->...->Activity
    //这段走不通。所以永远不可能生成BaseAutoInjectActivity。
    // 不得已只能破坏解耦性，强行指定业务逻辑使用的基类Activity
    //后期可能考虑把业务的基类Activity传进来。或者找个别的方法。
    public static final String ACTIVITY_TYPE="BaseBusinessActivity";
    public static final String INJECTOR_FRAGMENT_NAME = "BaseAutoInjectFragment";
    public static final String INJECTOR_ACTIVITY_NAME = "BaseAutoInjectActivity";

    public static final String BASE_GENERATOR_PACKAGE = "com.tinyfight.core.base";
    public static final String AUTO_INJECT_METHOD = "autoInject";

    public static final String MODULE_NAME = "AnnotationDIModule";
    public static final String COMPONENT_NAME = "AnnotationDIComponent";
    public static final String PACKAGE_NAME = "com.tinyfight.android.di";

    public static final String[] ACTIVITY_TYPES = {"android","app","Activity"};
}
