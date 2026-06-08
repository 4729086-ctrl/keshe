package com.shanxi.shopping.common;

/**
 * 订单状态枚举
 */
public enum OrderStatus {
    PENDING_PAY(1, "待付款"),
    PENDING_SHIP(2, "待发货"),
    PENDING_RECEIVE(3, "待收货"),
    COMPLETED(4, "已完成"),
    CANCELLED(5, "已取消"),
    RETURNING(6, "退货中"),
    RETURNED(7, "已退货");

    private final int code;
    private final String desc;

    OrderStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() { return code; }
    public String getDesc() { return desc; }

    public static boolean canCancel(int status) {
        return status == PENDING_PAY.code;
    }

    public static boolean canShip(int status) {
        return status == PENDING_SHIP.code;
    }

    public static boolean canReceive(int status) {
        return status == PENDING_RECEIVE.code;
    }

    public static boolean canReturn(int status) {
        return status == COMPLETED.code || status == PENDING_RECEIVE.code;
    }
}
