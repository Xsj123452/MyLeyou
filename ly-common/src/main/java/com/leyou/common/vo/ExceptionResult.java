package com.leyou.common.vo;

import com.leyou.common.enums.ExceptionEnum;
import lombok.Data;

@Data
public class ExceptionResult {
    private int status;
    private String msg;
    private Long timestap;
    public ExceptionResult(ExceptionEnum ee){
        status = ee.getCode();
        msg = ee.getMsg();
        timestap = System.currentTimeMillis();
    }
}
