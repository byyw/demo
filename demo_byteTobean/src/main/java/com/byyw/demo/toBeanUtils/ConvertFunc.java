package com.byyw.demo.toBeanUtils;

public interface ConvertFunc<T>{
    public T decode(ByteBuf o);
    public ByteBuf encode(T o);
}