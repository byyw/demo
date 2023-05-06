package com.byyw.demo.toBeanUtils;

import java.util.HashMap;
import java.util.Map;

import com.byyw.demo.toBeanUtils.convertFunc.*;

public class Func {

    private static Map<String,ConvertFunc> funcMap = new HashMap<String,ConvertFunc>();

    public static final String BYTE = "byte";
    public static final String BYTES = "bytes";
    public static final String INT = "int";
    public static final String BCD = "bcd";
    public static final String ASCII = "ascii";
    public static final String GBK = "gbk";
    public static final String MAP = "map";
    public static final String LIST = "list";
    public static final String OBJ = "obj";

    {
        funcMap.put(BYTE, new ByteFunc());
        funcMap.put(INT, new IntFunc());
    }
    
    public static void addFunc(String name,ConvertFunc func){
        if(funcMap.containsKey(name)){
            throw new RuntimeException("func name is exist");
        }
        funcMap.put(name, func);
    }
    
    public static boolean containsKey(String name){
        return funcMap.containsKey(name);
    }

    public static ConvertFunc getFunc(String name){
        return funcMap.get(name);
    }
}