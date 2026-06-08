package com.shanxi.shopping.common;

/**
 * 退货状态枚举
 */
public enum ReturnStatus {
    PENDING(1, "待处理"),
    APPROVED(2, "已同意"),
    REJECTED(3, "已拒绝"),
    COMPLETED(4, "已完成");

    private final int code;
    private final String desc;

    ReturnStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() { return code; }
    public String getDesc() { return desc; }
}
