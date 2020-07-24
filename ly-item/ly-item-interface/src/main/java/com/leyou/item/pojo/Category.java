package com.leyou.item.pojo;


import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_category")
@Data
public class Category {
  @Id
  @KeySql(useGeneratedKeys = true)
  private Long id;
  private String name;
  @Column(name = "parent_id")
  private Long parentId;
  @Column(name = "is_parent")
  private Long isParent;
  private Long sort;

}
