package com.leyou.item.pojo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @Author: cuzz
 * @Date: 2018/11/6 19:40
 * @Description: Spu详情
 */
@Data
@Table(name="tb_spu_detail")
public class SpuDetail {
    @Id
    private Long spuId;// 对应的SPU的id
    private String description;// 商品描述
    @Column(name = "spec_template")
    private String specialSpec;// 商品特殊规格的名称及可选值模板
    @Column(name = "specifications")
    private String genericSpec;// 商品的全局规格属性
    @Column(name = "packing_list")
    private String packingList;// 包装清单
    @Column(name = "after_service")
    private String afterService;// 售后服务
}
