package com.byyw.demo.toBeanUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cglib.beans.BeanMap;

import com.byyw.demo.Test;
import com.byyw.demo.toBeanUtils.annotation.ByBytes;
import com.byyw.demo.toBeanUtils.annotation.ByteField;
import com.byyw.demo.toBeanUtils.annotation.Fs;
import com.byyw.demo.toBeanUtils.exception.ConvertException;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;

public class ConvertUtils {
    // todo，可以用于class的记忆化
    private Map<Class<?>,List<Field>> fieldMap = new HashMap<>();
    private static AviatorEvaluatorInstance aviatorEvaluator = AviatorEvaluator.getInstance();
    

    public static Object fromByByte(byte bs[], Class<?> targetType) throws ConvertException {
        return from(new ByteBuf(bs), targetType);
    }
    private static Object from(ByteBuf bb, Class<?> targetType) throws ConvertException {
        // bb不为空，targetType不为空，targetType存在@ByBytes注解
        if(bb == null){
            throw new ConvertException("bs is null");
        }
        if(targetType == null){
            throw new ConvertException("targetType is null");
        }
        if(!targetType.isAnnotationPresent(ByBytes.class)){
            throw new ConvertException("targetType is not @ByBytes");
        }
        try {
            // 通过反射创建对象
            Object o = targetType.getDeclaredConstructor().newInstance();
            // 获取包含@ByBytes的父类的所有@ByteField字段
            List<Field> fields = getField(targetType);
            for(Field f:fields){
                // 判断judge逻辑条件
                ByteField bf = judgeField(o,f);
                if(bf == null){
                    continue;
                }
                // 设置可访问
                f.setAccessible(true);
                // 获取长度，以lengthName优先
                Integer length = null;
                if(!bf.lengthName().equals("")){
                    Field v = targetType.getField(bf.lengthName());
                    v.setAccessible(true);
                    length = (Integer)v.get(o);
                } else {
                    length = bf.value();
                }
                if(length == -1){
                    throw new ConvertException("length is -1");
                }

                // ByteBuf bs = bb.getBytes(length);
                if(Func.containsKey(bf.func())){
                    // f.set(o, Func.getFunc(bf.func()).decode(bs));
                }
            }
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException | NoSuchFieldException e) {
            throw new ConvertException(e);
        }
        return null;
    }




    public static String to(Object bs) {
        if (bs == null) {
            return null;
        }
        return bs.toString();
    }

    private static List<Field> getField(Class<?> c){
        List<Field> fields = new ArrayList<>();
        // 父类也存在注释
        if(c.getSuperclass().isAnnotationPresent(ByBytes.class)){
            fields.addAll(getField(c.getSuperclass()));
        }
        Field[] fs = c.getDeclaredFields();
        for(Field f:fs){
            if(f.isAnnotationPresent(ByteField.class) || f.isAnnotationPresent(Fs.class)){
                fields.add(f);
            }
        }
        Collections.addAll(fields, c.getDeclaredFields());
        return fields;
    }
    private static ByteField judgeField(Object o, Field f){
        ByteField[] fs = f.getAnnotationsByType(ByteField.class);
        // 多注解实现或操作
        for (ByteField bf : fs) {
            // 判断注释是否存在judge
            if (bf.judge().equals("") || (boolean)aviatorEvaluator.compile(bf.judge()).execute(BeanMap.create(o))) {
                return bf;
            }
        }
        return null;
    }
}


 /*
对以byte[]存储的二进制数据，
取第i位到第j位的值，并倒叙以byte[]返回




  */