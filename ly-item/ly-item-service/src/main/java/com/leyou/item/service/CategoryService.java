package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;

@Service("categoryService")
public class CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    public List<Category> queryCategoryListByPid(Long pid) {
        Category t = new Category();
        t.setParentId(pid);
        List<Category> list = categoryMapper.select(t);
        if(CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FIND);
        }
        return list;
    }

    public List<Category> findByBid(Long id){
        List<Long> ids = categoryMapper.findByBidFromCneter(id);
        if (CollectionUtils.isEmpty(ids)){
            return null;
        }
        Example e = new Example(Category.class);
        e.createCriteria().andIn("id",ids);
        List<Category> categories = categoryMapper.selectByExample(e);
         return CollectionUtils.isEmpty(categories)?null:categories;

    }

    public List<Category> queryByIds(List<Long> ids) {
         final List<Category> list = categoryMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(list)) {
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FIND);
        }
        return list;
    }
    public Category queryByCid(Long cid){
        return categoryMapper.selectByPrimaryKey(cid);
    }

    public List<Category> queryCategoryByCid3(Long cid3) {
        //通过cid3查询cid3的Category
        Category c3 = categoryMapper.selectByPrimaryKey(cid3);
        //通过c3的parent_id查询cid2的Category
        Category c2 = categoryMapper.selectByPrimaryKey(c3.getParentId());
        //通过c2的parent_id查询cid1的Category
        Category c1 = categoryMapper.selectByPrimaryKey(c2.getParentId());
        return Arrays.asList(c1,c2,c3);
    }
}
