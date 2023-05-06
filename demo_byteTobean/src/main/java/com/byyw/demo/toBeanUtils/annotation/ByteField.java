package com.byyw.demo.toBeanUtils.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.byyw.demo.toBeanUtils.Func;
import com.byyw.demo.toBeanUtils.Unit;

@Repeatable(Fs.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ByteField {
    /**
     * 长度
     * @return
     */
    int value() default -1;
    /**
     * 指定长度为某个字段的值，优先级高于value
     * @return
     */
    String lengthName() default "";
    /**
     * 长度单位
     * @return
     */
    Unit unit() default Unit.BYTE;

    /**
     * 格式转换函数
     * @return
     */
    String func() default Func.INT;

    /**
     * 针对map，list，obj转换函数时所指定的class
     * @return
     */
    Class bean() default Object.class;
    /**
     * 采用Aviator,变量为字段名
     * @return
     */
    String judge() default "";
    /**
     * 字段描述
     * @return
     */
    String desc() default "";
}