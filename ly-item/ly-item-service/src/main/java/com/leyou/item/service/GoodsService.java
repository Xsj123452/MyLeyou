package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.dto.CartDto;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.SkuMapper;
import com.leyou.item.mapper.SpuDetailMapper;
import com.leyou.item.mapper.SpuMapper;
import com.leyou.item.mapper.StockMapper;
import com.leyou.item.pojo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GoodsService {
    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private SpuDetailMapper spuDetailMapper;
    @Autowired
    private StockMapper stockMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 修改商品·
     * @param spu
     */

    @Transactional
    public void  updateSpu(Spu spu){
        spu.setLastUpdateTime(new Date());
        int count = spuMapper.updateByPrimaryKey(spu);
        if(count!=1){
            throw new LyException(ExceptionEnum.SPU_NOT_FOUND);
        }
//        修改detail
        SpuDetail spuDetail = spu.getSpuDetail();
        count = spuDetailMapper.updateByPrimaryKey(spuDetail);
        if(count!=1){
            throw new LyException(ExceptionEnum.SPU_UPDATE_ERROR);
        }
        //修改sku
        //先删除再新增
        Sku s = new Sku();
        s.setSpuId(spu.getId());
        Stock st = new Stock();
        //获取skuid删除stock
        List<Sku> skus = skuMapper.select(s);
        for (Sku sku : skus) {
            st.setSkuId(sku.getId());
            stockMapper.delete(st);
        }
        skuMapper.delete(s);
        List<Stock> all =new ArrayList<>();
        for (Sku sku : spu.getSkus()) {
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            sku.setSpuId(spu.getId());
            count = skuMapper.insert(sku);
            if(count != 1){
                throw new LyException(ExceptionEnum.SPU_UPDATE_ERROR);
            }
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            all.add(stock);
        }
        //修改库存
        count = stockMapper.insertList(all);
        if(count < 0){
            throw new LyException(ExceptionEnum.SPU_UPDATE_ERROR);
        }
        // 消息通知
        amqpTemplate.convertAndSend("item.update",spu.getId());
    }
    /**
     *新增商品
     * @param spu
     */
    @Transactional
    public void saveGood(Spu spu){
        //新增spu
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(spu.getCreateTime());
        spu.setId(null);
        spu.setSaleable(true);
        spu.setValid(false);
        int count = spuMapper.insert(spu);
        if (count!=1){
            throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
        }
        //新增detail
        SpuDetail spuDetail = spu.getSpuDetail();
        spuDetail.setSpuId(spu.getId());
        count = spuDetailMapper.insert(spu.getSpuDetail());
        if(count!=1){
            throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
        }
        //新增sku
        List<Stock> all =new ArrayList<>();
        for (Sku sku : spu.getSkus()) {
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            sku.setSpuId(spu.getId());
             count = skuMapper.insert(sku);
             if(count != 1){
                 throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
             }
             Stock stock = new Stock();
             stock.setSkuId(sku.getId());
             stock.setStock(sku.getStock());
             all.add(stock);
        }
        //新增库存
        count = stockMapper.insertList(all);
        if(count != 1){
            throw new LyException(ExceptionEnum.GOODS_SAVE_ERROR);
        }
        //amqp消息发送
        amqpTemplate.convertAndSend("item.insert",spu.getId());
    }

    /**
     * 分页查询记录
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    public PageResult<Spu> queryByTerm(String key,Boolean saleable,Integer page,Integer rows){
        //查询spu
        Example e = new Example(Spu.class);
        Example.Criteria criteria = e.createCriteria();
        //是否有关键字搜索
        if(!StringUtils.isBlank(key)){
            criteria.andLike("title","%"+key.trim()+"%");
        }
        //是否过滤上下架
        if(saleable!=null){
            criteria.andEqualTo("saleable",saleable);
        }
        //默认以最新修改时间为准排序
        e.setOrderByClause("last_update_time DESC");
        //控制查询条数
        PageHelper.startPage(page,Math.min(rows,200));
        List<Spu> spus = spuMapper.selectByExample(e);
        Sku s = new Sku();
        for (Spu spu : spus) {
            spu.setBname(brandService.queryById(spu.getBrandId()).getName());
            spu.setCname(categoryService.queryByCid(spu.getCid1()).getName()+"/"+
                    categoryService.queryByCid(spu.getCid2()).getName()+"/"+
                    categoryService.queryByCid(spu.getCid3()).getName()
            );
            s.setSpuId(spu.getId());
            spu.setSkus(skuMapper.select(s));
        }
        //判断是否查询到数据
        if(CollectionUtils.isEmpty(spus)){
            throw new LyException(ExceptionEnum.SPU_NOT_FOUND);
        }

        PageInfo<Spu> pageInfo = new PageInfo<>(spus);
        return new PageResult<Spu>(pageInfo.getTotal(),pageInfo.getPages(),spus);
    }
    private void loadCategoryAndBrandName(List<Spu> spus) {
        for (Spu spu : spus) {
            // 处理分类名称
            List<String> names = categoryService.queryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()))
                    .stream().map(Category::getName).collect(Collectors.toList());
            spu.setCname(StringUtils.join(names, "/"));
            // 处理品牌名称
            spu.setBname(brandService.queryById(spu.getBrandId()).getName());
        }
    }
    /**
     * 通过spu_detail id查询
     * @param id
     * @return
     */
    public SpuDetail querySpudetailBySid(Long id){
        SpuDetail s = spuDetailMapper.selectByPrimaryKey(id);
        if(s==null ){
            throw new LyException(ExceptionEnum.SPU_NOT_FOUND);
        }
        return s;
    }
    public List<Sku> querySkusBySpuId(Long id) {
        Sku sku = new Sku();
        sku.setSpuId(id);
        List<Sku> skus = skuMapper.select(sku);
        //查询库存
        List<Long> ids = skus.stream().map(Sku::getId).collect(Collectors.toList());
        List<Stock> stocks = stockMapper.selectByIdList(ids);
        Map<Long, Integer> stockmap = stocks.stream().collect(Collectors.toMap(Stock::getSkuId, Stock::getStock));
        skus.forEach(sku1 -> sku1.setStock(stockmap.get(sku1.getId())));
//        for (Sku sku1 : skus) {
//            Stock stock = stockMapper.selectByPrimaryKey(sku1.getId());
//            if(stock == null){
//                throw new LyException(ExceptionEnum.STOCK_NULL);
//            }
//            sku1.setStock(stock.getStock());
//        }
//
//        return skus;
        return skus;
    }

    /**
     * 删除商品
     * @param id
     */
    @Transactional
    public void deleteSpu(@PathVariable(name = "id") Long id) {
        Sku sku = new Sku();
        Stock stock = new Stock();
            //获得spu对象
            Spu spu = spuMapper.selectByPrimaryKey(id);

            //获得spu下的sku对象
            sku.setSpuId(spu.getId());
            List<Sku> skus = skuMapper.select(sku);
            if(!CollectionUtils.isEmpty(skus)) {
                for (Sku s : skus) {
                    stock.setSkuId(s.getId());
                    stockMapper.delete(stock);
                    skuMapper.deleteByPrimaryKey(s.getId());
                }
            }
        spuMapper.delete(spu);
            //amqp消息发送
        amqpTemplate.convertAndSend("item.delete",spu.getId());
    }

    public Spu querySpuByid(Long id) {
        return spuMapper.selectByPrimaryKey(id);
    }

    public Spu querySpuById(Long id) {
        //查询spu
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if(spu!=null){
            throw new LyException(ExceptionEnum.SPU_NOT_FOUND);
        }
        //查询sku
        List<Sku> skus = querySkusBySpuId(id);
            spu.setSkus(skus);
        //查询detail
        spu.setSpuDetail(querySpudetailBySid(id));
        return spu;
    }

    public Sku querySkuBuSkuid(Long skuid) {
        return skuMapper.selectByPrimaryKey(skuid);
    }

    public List<Sku> querySkuByIds(List<Long> ids) {
        List<Sku> lists = new ArrayList<>();
        ids.forEach(id -> lists.add(skuMapper.selectByPrimaryKey(id)));
        return lists;
    }

    @Transactional
    public void decreaseStock(List<CartDto> carts) {
        carts.forEach(cartDto -> {
            int i = stockMapper.decreaseStock(cartDto.getSkuId(), cartDto.getNum());
            if(i != 1){
                throw new LyException(ExceptionEnum.STOCK_NOT_ENOUGH);
            }
        });
    }
}
