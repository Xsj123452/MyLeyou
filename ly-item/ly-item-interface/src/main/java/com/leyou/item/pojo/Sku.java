package com.leyou.item.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.*;
import java.util.Date;

/**
 * @Author: cuzz
 * @Date: 2018/11/7 19:09
 * @Description:
 */
@Data
@Table(name = "tb_sku")
public class Sku {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    @Column(name = "spu_id")
    private Long spuId;
    private String title;
    private String images;
    private Long price;
    @Column(name = "own_spec")
    private String ownSpec;// 商品特殊规格的键值对
    private String indexes;// 商品特殊规格的下标
    private Boolean enable;// 是否有效，逻辑删除用
    @Column(name = "create_time")
    private Date createTime;// 创建时间
    @Column(name = "last_update_time")
    private Date lastUpdateTime;// 最后修改时间

    @Transient
    private Integer stock;// 库存
}
