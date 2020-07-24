package com.leyou.item.mapper;

import com.leyou.common.mapper.MyMapper;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CategoryMapper extends MyMapper<Category,Long> {
    @Select("select category_id from tb_category_brand where brand_id =#{bid}")
    public List<Long> findByBidFromCneter(Long bid);
}
