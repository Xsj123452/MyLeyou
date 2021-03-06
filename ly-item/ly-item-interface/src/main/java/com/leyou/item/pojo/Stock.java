package com.leyou.item.pojo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @Author: cuzz
 * @Date: 2018/11/7 19:11
 * @Description:
 */
@Data
@Table(name = "tb_stock")
public class Stock {
    @Id
    @Column(name = "sku_id")
    private Long skuId;
    @Column(name = "seckill_stock")
    private Integer seckillStock;// 秒杀可用库存
    @Column(name = "seckill_total")
    private Integer seckillTotal;// 已秒杀数量
    private Integer stock;// 正常库存
}
