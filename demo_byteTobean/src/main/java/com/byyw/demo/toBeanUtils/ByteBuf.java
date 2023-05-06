package com.byyw.demo.toBeanUtils;

public class ByteBuf{
    private String bs;
    private int index;
    public ByteBuf(byte[] bs){
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<bs.length;i++){
            String s = Integer.toBinaryString(bs[i]&0x00ff);
            sb.append("00000000".substring(s.length())).append(s);
        }
        this.bs = sb.toString();
        this.index = 0;
    }

    public Integer readBit(){
        if(index+1 >= bs.length())
            return null;
        return bs.charAt(index++)-'0';
    }

    public Byte readByte(){
        if(index+8 >= bs.length()){
            
        }
        Byte b = (byte)Integer.parseInt(bs.substring(index,index+8), 2);
        index += 8;
        return b;
    }


}