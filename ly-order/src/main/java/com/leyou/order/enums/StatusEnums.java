package com.leyou.order.enums;

import org.omg.CORBA.PUBLIC_MEMBER;

public enum StatusEnums {
    UN_PAY(1,"未付款"),
    PAYED(2,"已付款，未发货"),
    DELIVERED(3,"已发货，未确认"),
    SUCCESS(4,"已确认，交易成功"),
    CLOSED(5,"已关闭"),
    RATED(6,"已评价，交易结束")
    ;
    private int code;
    private String desc;

    StatusEnums(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public int getCode(){
        return this.code;
    }
}
