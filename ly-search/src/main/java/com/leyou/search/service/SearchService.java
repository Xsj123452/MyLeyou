package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.*;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClent;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.repository.GoodsRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SearchService {
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecificationClent specificationClent;
    @Autowired
    private GoodsRepository repository;
    @Autowired
    private ElasticsearchTemplate template;
    public Goods buildGoods(Spu spu){
        //查询分类
        List<Category> categories = categoryClient.queryCategoryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        List<String> name = categories.stream().map(Category::getName).collect(Collectors.toList());
        //查询品牌
        Brand brand = brandClient.findById(spu.getBrandId());
        if (brand == null) {
            throw new LyException(ExceptionEnum.BRAND_NOT_FIND);
        }
        
        //sku
        List<Sku> skus = goodsClient.querySkuListBySpuid(spu.getId());
        //过滤sku中无用的属性
        List<Map<String,Object>> skuVo = new ArrayList<>();
        Set<Long> price = new HashSet<>();
        for (Sku sku : skus) {
            Map<String,Object> map = new HashMap<>();
            map.put("id",sku.getId());
            map.put("title",sku.getTitle());
            map.put("price",sku.getPrice());
            map.put("image",StringUtils.substringBefore(sku.getImages(),","));
            price.add(sku.getPrice());
            skuVo.add(map);
        }
        //查询规格参数
        List<SpecParam> params = specificationClent.queryParamsByGid(null, spu.getCid3(), true);
        if (params == null) {
            throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }
        //查询商品详情
        SpuDetail spuDetail = goodsClient.queryDetailById(spu.getId());
        //获取通用规格参数
        Map<String, String> genericSpec = JsonUtils.parseMap(spuDetail.getGenericSpec(), String.class, String.class);
        //获取特有规格参数
        Map<String, List<Object>> specialSpec = JsonUtils.nativeRead(spuDetail.getSpecialSpec(), new TypeReference<Map<String, List<Object>>>() {});
        //specs  key是参数名,value是参数值
        Map<String ,Object> specs = new HashMap<>();
        for (SpecParam param : params) {
            String key = param.getName();
            Object value = "";
            if(param.getGeneric()){
                value = genericSpec.get(param.getId().toString());
                //判断是否是数值类型
                if(param.getNumeric()){
                    //处理成区间参数
                    value = chooseSegment(value.toString(),param);
                }
            }else {
                value = specialSpec.get(param.getId().toString());
            }
            specs.put(key,value);
        }
        //价格集合
//        Set<Long> price = skus.stream().map(Sku::getPrice).collect(Collectors.toSet());
        //搜索字段
        String all = spu.getTitle()+ StringUtils.join(name," ")+brand.getName();
        //构建goods对象
        Goods goods = new Goods();
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setId(spu.getId());
        goods.setSubTitle(spu.getSubTitle());
        goods.setAll(all);   //搜索字段，包括标题，分类，品牌，规格等
        goods.setPrice(price);
        goods.setSkus(JsonUtils.serialize(skus));//将skus序列化成json
        goods.setSpecs(specs);
        return goods;
    }

    /**
     * 数据转换，将尺寸转换成区间
     * @param value
     * @param p
     * @return
     */
    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    /**
     * 数据查询
     * @param request
     * @return
     */
    public PageResult<Goods> search(SearchRequest request){
        int page = request.getPage()-1;//elaticsearch中0未第一页
        int rows = SearchRequest.getDefaultRows();
        //创建查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //结果过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","subTitle","skus"},null));
        //分页
        queryBuilder.withPageable(PageRequest.of(page,rows));
        //查询条件
        QueryBuilder base = buildBasicQuery(request);
        //过滤
        queryBuilder.withQuery(base);
        //聚合,展示过滤字段分类与品牌
        //聚合分类
        String categoryAggName = "category_agg";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        //聚合品牌
        String brandAggName = "brand_agg";
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));
        //查询
//        Page<Goods> result = repository.search(queryBuilder.build());
        AggregatedPage<Goods> result = template.queryForPage(queryBuilder.build(), Goods.class);
        //解析结果
        //分页结果
        long total = result.getTotalElements();
        int totalPages = result.getTotalPages();
        List<Goods> goods = result.getContent();
        //聚合结果
        Aggregations aggs = result.getAggregations();
        List<Category> categories = parseCategoryAgg(aggs.get(categoryAggName));
        //规格参数聚合
        List<Map<String,Object>> specs = null;
        if(!CollectionUtils.isEmpty(categories)&&categories.size() == 1){
            //商品分类为1且不为空则开始聚合
            specs = buildSpecAgg(categories.get(0).getId(),base);
        }
        List<Brand> brands = parseBrandAgg(aggs.get(brandAggName));
        return new SearchResult(total,totalPages,goods,categories,brands,specs);

    }

    private QueryBuilder buildBasicQuery(SearchRequest request) {
        //创建bool查询
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        //查询条件
        queryBuilder.must(QueryBuilders.matchQuery("all",request.getKey()));
        //过滤条件
        Map<String, String> map = request.getFilter();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            //处理key
            if(!"cid3".equals(key) && !"brandId".equals(key)){
                key = "specs."+key+".keyword";
            }
            String value = entry.getValue();
            queryBuilder.filter(QueryBuilders.termQuery(key,value));
        }
        return queryBuilder;
    }

    private List<Brand> parseBrandAgg(LongTerms terms) {
        List<Long> longStream = null;
        try {
            longStream = terms.getBuckets().stream().map(bucket -> bucket.getKeyAsNumber().longValue()).collect(Collectors.toList());
        } catch (Exception e) {
            return null;
        }
        return CollectionUtils.isEmpty(longStream)?null: brandClient.queryBrandByIds(longStream);
    }

    private List<Category> parseCategoryAgg(LongTerms terms) {
        List<Long> longStream = null;
        try {
            longStream = terms.getBuckets().stream().map(bucket -> bucket.getKeyAsNumber().longValue()).collect(Collectors.toList());
        } catch (Exception e) {

            return null;
        }
        return CollectionUtils.isEmpty(longStream)?null:categoryClient.queryCategoryByIds(longStream);
    }

    /**
     * 规格参数聚合查询
     * @param cid
     * @param base
     * @return
     */
    private List<Map<String,Object>> buildSpecAgg(Long cid,QueryBuilder base){
        List<Map<String,Object>> specs = new ArrayList<>();
        //查询需要聚合的规格参数
        List<SpecParam> params = specificationClent.queryParamsByGid(null, cid, true);
        //完成聚合
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //带上初始查询条件
        queryBuilder.withQuery(base);
        //聚合
        for (SpecParam param : params) {
            String name = param.getName();
            queryBuilder.addAggregation(AggregationBuilders.terms(name).field("specs."+name+".keyword"));
        }
        //获取结果
        AggregatedPage<Goods> result = template.queryForPage(queryBuilder.build(), Goods.class);
        //解析结果
        Aggregations aggs = result.getAggregations();
        for (SpecParam param : params) {
            Map<String,Object> map = new HashMap<>();
            //规格参数名
            String name = param.getName();
            //待选项
            StringTerms terms = aggs.get(name);
            List<String> options = terms.getBuckets().stream().map(StringTerms.Bucket::getKeyAsString).collect(Collectors.toList());
            //准备map
            map.put("k",name);
            map.put("options",options);
            if(!CollectionUtils.isEmpty(options)){
            specs.add(map);
            }
        }
        return specs;
    }

    public void createOrUpdateIndex(Long spuId) {
        //查询spu
        Spu spu = goodsClient.querySpuById(spuId);
        // 构建Goods对象
        Goods goods = buildGoods(spu);
        //存入索引库
        repository.save(goods);
    }

    public void deleteIndex(Long spuId) {
        repository.deleteById(spuId);
    }
}
