package com.byyw.demo.toBeanUtils.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Inherited
public @interface ByBytes {
}

/** learning:
    1. @Retention，元注解，保留策略，
        - RetentionPolicy.SOURCE：注解只在源码中存在，编译时会被忽略
        - RetentionPolicy.CLASS：注解只在源码和字节码中存在，编译时会被忽略
        - RetentionPolicy.RUNTIME：注解在源码、字节码和运行时都存在，常用
    2. @Target，元注解，作用域
        - ElementType.TYPE：类、接口、枚举、注解
        - ElementType.FIELD：字段、枚举的常量
        - ElementType.METHOD：方法
        - ElementType.PARAMETER：方法参数
        - ElementType.CONSTRUCTOR：构造函数
        - ElementType.LOCAL_VARIABLE：局部变量
        - ElementType.ANNOTATION_TYPE：注解
        - ElementType.PACKAGE：包
        - ElementType.TYPE_PARAMETER：类型参数
        - ElementType.TYPE_USE：类型使用
    3. @Documented，元注解，是否生成文档
    4. @Inherited，元注解，是否允许子类继承
    5. @Repeatable，元注解，是否允许重复

 
 
 */