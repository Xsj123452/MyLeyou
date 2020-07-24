package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Collections;
import java.util.List;

@Service("brandService")
public class BrandService {
    @Autowired
    private BrandMapper brandMapper;

    public PageResult<Brand> queryBrandBypage(Integer page, Integer rows, String sortBy, Boolean desc, String key) {
        Example e = new Example(Brand.class);
        PageHelper.startPage(page,rows);
        //模糊查询
        if(StringUtils.isNoneBlank(key)){
            e.createCriteria().orLike("name","%"+key+"%").orEqualTo("letter",key);
        }
        //排序
        if(StringUtils.isNotBlank(sortBy)){
            String orderClause = sortBy + (desc ? " DESC":" ASC");
            e.setOrderByClause(orderClause);
        }
        //查询
        List<Brand> brands = brandMapper.selectByExample(e);
        if(CollectionUtils.isEmpty(brands)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FIND);
        }
        PageInfo<Brand> pageInfo = new PageInfo<>(brands);
        return new PageResult<>(pageInfo.getTotal(),pageInfo.getPages(),brands);
    }
    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {
        int flag = brandMapper.insert(brand);
        if(flag != 1){
            throw new LyException(ExceptionEnum.BRAND_SAVE_ERROR);
        }
        for (Long cid : cids) {
            int i = brandMapper.insertCategoryBrand(cid, brand.getId());
            if(i != 1){
                throw new LyException(ExceptionEnum.CATEGORY_BRAND_SAVE_ERROR);
            }
        }

    }

    /**
     * 修改
     * @param brand
     */
    @Transactional
    public void  update(Brand brand){
        brandMapper.updateByPrimaryKey(brand);
    }
    public Brand findById(Long id){
        Brand brand = brandMapper.selectByPrimaryKey(id);
        if(brand == null){
            throw new LyException(ExceptionEnum.UPDATE_BRAND_ERROR);
        }
        return brand;
    }

    /**
     * 删除
     */
    @Transactional
    public void  delete(Long id){
        //使用id查询相关分类中间表，并删除中间表数据
        brandMapper.deleteCategoryByBrand(id);
        brandMapper.deleteByPrimaryKey(id);
    }

    /**
     * 通过分类id查询品牌
     */
    public List<Brand> queryBrandByCid(Long cid){
        List<Long> bids = brandMapper.queryBidByCid(cid);
        Example e = new Example(Brand.class);
        e.createCriteria().andIn("id",bids);
        List<Brand> brands = brandMapper.selectByExample(e);
        if(CollectionUtils.isEmpty(brands)){
            throw new LyException(ExceptionEnum.THE_CATEGORY_NOT_HAVE_BRAND);
        }
        return  brands;
    }

    public Brand queryById(Long brandId) {
        return brandMapper.selectByPrimaryKey(brandId);
    }
    /**
     * 通过id列表查询
     */
    public List<Brand> queryBrandByIds(List<Long> ids){
        List<Brand> brands = brandMapper.selectByIdList(ids);
        if(CollectionUtils.isEmpty(brands)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FIND);
        }
        return brands;
    }
}
