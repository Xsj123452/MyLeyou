package com.leyou.search.repository;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Spu;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GoodsRepositoryTest {
    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private ElasticsearchTemplate template;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SearchService searchService;
    @Test
    public void  testCreateIndex(){
        //创建索引
        template.createIndex(Goods.class);
        //创建映射
        template.putMapping(Goods.class);
    }
    @Test
    public void saveToElasticsearch(){
        int page = 1;
        int row = 100;
        int flag;
        do {
            //获取单页数据
            PageResult<Spu> pageResult = goodsClient.queryAllGoods(null, true, page, row);
            //获取spu
            List<Spu> items = pageResult.getItems();
            if (CollectionUtils.isEmpty(items)) {
                break;
            }
            //处理成goods
            List<Goods> collect = items.stream().map(searchService::buildGoods).collect(Collectors.toList());
            goodsRepository.saveAll(collect);
            flag = pageResult.getTotalPage();
            page++;

        }while(page <= flag);
    }
}