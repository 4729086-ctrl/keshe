package com.shanxi.shopping.common;

/**
 * 商品状态枚举
 */
public enum ProductStatus {
    OFF_SHELF(0, "下架"),
    ON_SHELF(1, "上架");

    private final int code;
    private final String desc;

    ProductStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() { return code; }
    public String getDesc() { return desc; }
}
