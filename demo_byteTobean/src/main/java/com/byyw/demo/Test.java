package com.byyw.demo;

import java.io.Serializable;
import java.util.Map;

import com.byyw.demo.toBeanUtils.ConvertUtils;
import com.byyw.demo.toBeanUtils.Func;
import com.byyw.demo.toBeanUtils.annotation.ByBytes;
import com.byyw.demo.toBeanUtils.annotation.ByteField;
import com.byyw.demo.toBeanUtils.exception.ConvertException;

import lombok.Data;

@ByBytes
public class Test {

    @ByteField(value = 4, desc = "报警标志")
    private int warnBit;

    @ByteField(value = 4, desc = "状态")
    private int statusBit;

    @ByteField(value = 4, desc = "纬度")
    private int latitude;

    @ByteField(value = 4, desc = "经度")
    private int longitude;

    @ByteField(value = 2, desc = "高程(米)")
    private int altitude;

    @ByteField(value = 2, desc = "速度(1/10公里每小时)")
    private int speed;

    @ByteField(value = 2, desc = "方向")
    private int direction;

    @ByteField(value = 6, func = Func.BCD, desc = "时间(YYMMDDHHMMSS)")
    private String dateTime;

    @ByteField(func = Func.MAP, bean = AttributeConverter.class, desc = "位置附加信息")
    private Map<Integer, Object> attributes;

    @Data
    @ByBytes
    public static class AttributeConverter{

        @ByteField(value = 1)
        private Integer id;

        @ByteField(value = 1)
        private Integer length;

        @ByteField(lengthName = "length", func = Func.BYTES)
        private byte[] value;
    }

    public static void main(String[] args) {
        String s = "0200003d010027324508000e000000000008000301c51265073d0a17000d000000002304050032080104001af00e1001632a02000030011831010b56020a0057080000000000000000";
        // try {
        //     // Test test = (Test) ConvertUtils.fromByHexStr(s, Test.class);
        // } catch (ConvertException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        
    }
}
