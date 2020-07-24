package com.leyou.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionEnum {
    BRAND_NOT_FIND(404,"品牌未查到"),
    CATEGORY_NOT_FIND(404,"该分类为空"),
    BRAND_SAVE_ERROR(500,"品牌新增失败"),
    CATEGORY_BRAND_SAVE_ERROR(500,"品牌新增失败"),
    UPLOAD_FILE_FAIL(500,"上传文件失败"),
    INVALID_FILE_TYPE(400,"无效的文件类型"),
    UPDATE_BRAND_ERROR(400,"品牌不存在或以被删除"),
    GROUP_SPEC_NOT_FOUND(404,"该分类未编写规格组"),
    SPEC_PARAM_NOT_FOUND(404,"规格参数为空"),
    GOODS_SAVE_ERROR(500,"商品新增失败"),
    THE_CATEGORY_NOT_HAVE_BRAND(404,"该分类下暂无品牌"),
    SPU_NOT_FOUND(404,"商品未找到"),
    STOCK_NULL(404,"库存为空"),
    SPU_UPDATE_ERROR(400,"修改商品失败"),
    GOODS_DELETE_ERROR(400,"商品删除失败"),
    INVAILD_USER_PARAM_TYPE(400,"用户数据类型无效"),
    INVALID_CODE(400,"验证码错误"),
    CREATE_ORDER_ERROR(500,"新增订单失败"),
    STOCK_NOT_ENOUGH(500,"库存不足")
    ;

    private int code;//异常代码
    private String msg;//异常信息
}
