package com.leyou.common.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class PageResult<T> {
    private Long total;   //总记录数
    private Integer totalPage; //总页数
    private List<T> items;//当前页数据
    public PageResult(Long total,List<T> items){
        this.total =total;
        this.items = items;
    }
    public PageResult(Long total,Integer totalPage,List<T> items){
        this.total =total;
        this.items = items;
        this.totalPage = totalPage;
    }
}
