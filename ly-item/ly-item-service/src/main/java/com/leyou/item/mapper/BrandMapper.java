package com.leyou.item.mapper;


import com.leyou.common.mapper.MyMapper;
import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends MyMapper<Brand,Long> {
    @Insert("insert into tb_category_brand values (#{cid},#{bid})")
        public int insertCategoryBrand(Long cid,Long bid);
    @Delete("delete from tb_category_brand where brand_id = #{id}")
        public void deleteCategoryByBrand(Long id);
    @Select("select brand_id from tb_category_brand where category_id = #{cid}")
        public List<Long> queryBidByCid(Long Cid);
}
