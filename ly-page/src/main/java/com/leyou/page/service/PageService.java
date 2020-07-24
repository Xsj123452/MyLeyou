package com.leyou.page.service;

import com.leyou.item.pojo.*;
import com.leyou.page.client.BrandClient;
import com.leyou.page.client.CategoryClient;
import com.leyou.page.client.GoodsClient;
import com.leyou.page.client.SpecificationClent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PageService {
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecificationClent specificationClent;
    @Autowired
    private TemplateEngine templateEngine;

    public Map<String, Object> loadModel(Long id) {
        Map<String,Object> model = new HashMap<>();
        Spu spu = goodsClient.querySpuById(id);
        List<Sku> skus = goodsClient.querySkuListBySpuid(spu.getId());
        SpuDetail detail = goodsClient.queryDetailById(spu.getId());
        //查询品牌
        Brand brand = brandClient.findById(spu.getBrandId());
        //查询商品分类
        List<Category> categories = categoryClient.queryCategoryByIds(
                Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        //查询规格参数
        List<SpecGroup> specs = specificationClent.queryListByCid(spu.getCid3());
        model.put("title", spu.getTitle());
        model.put("subTitle", spu.getSubTitle());
        model.put("skus", skus);
        model.put("detail", detail);
        model.put("brand", brand);
        model.put("categories", categories);
        model.put("specs", specs);
        System.out.println("ok");
        return model;
    }
    public void createHtml(Long spuId){
        //获取上下文
        Context context = new Context();
        context.setVariables(loadModel(spuId));
        //输出(流)文件
        File dest = new File("D://projects/shop/upload", spuId + ".html");
        if (dest.exists()){
            dest.delete();
        }
        try (PrintWriter writer = new PrintWriter(dest, "UTF-8")) {
            //生成Html
            templateEngine.process("item", context, writer);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void deletePage(Long spuId) {
        File dest = new File("D://projects/shop/upload", spuId + ".html");
        if(dest.exists()){
            dest.delete();
        }
    }
}
