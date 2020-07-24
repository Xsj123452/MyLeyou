package com.leyou.item.mapper;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Author: cuzz
 * @Date: 2018/11/5 13:53
 * @Description:
 */
public interface SpecGroupMapper extends Mapper<SpecGroup>{
    @Delete("delete from tb_spec_param where group_id = #{id}")
    public void deleteGroupParamByGid(Long id);
    @Select("select * from tb_spec_param where group_id = #{gid}")
    public List<SpecParam> querySpecParamByGid(Long gid);
    @Select("select * from tb_spec_param where cid = #{cid}")
    public List<SpecParam> querySpecParamByCid(Long cid);
    @Select("select * from tb_spec_group where cid = #{cid}")
    public List<SpecGroup> queryGroupByCid(Long cid);
}
