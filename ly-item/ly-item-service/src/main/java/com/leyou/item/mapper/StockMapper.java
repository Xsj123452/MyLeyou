package com.leyou.item.mapper;

import com.leyou.common.mapper.MyMapper;
import com.leyou.item.pojo.Stock;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

public interface StockMapper extends MyMapper<Stock,Long> {
    @Update("update tb_stock set stock = stock - #{num} where sku_id = #{id} and stock >= #{num}")
    public int decreaseStock(Long id,Integer num);
}
