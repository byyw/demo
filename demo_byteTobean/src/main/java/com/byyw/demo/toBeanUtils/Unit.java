package com.byyw.demo.toBeanUtils;

public enum Unit {
    Bit(1), BYTE(8), SHORT(16), INT(32), LONG(64), FLOAT(32), DOUBLE(64);
    private int size;

    private Unit(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
