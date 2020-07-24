package com.leyou.item.api;

import com.leyou.common.dto.CartDto;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface GoodsApi {
    /**
     * 分页查询商品spu
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("spu/page")
    PageResult<Spu> queryAllGoods(@RequestParam(value = "key",required = false)String key, @RequestParam(value = "saleable",required = false)Boolean saleable
            , @RequestParam(value = "page",required = false,defaultValue = "1")Integer page, @RequestParam(value = "rows",required = false,defaultValue = "5")Integer rows);

    /**
     * 查询spu下的所有sku
     * @param id
     * @return
     */
    @GetMapping("/sku/list")
    List<Sku> querySkuListBySpuid(@RequestParam("id")Long id);

    /**
     * 查询商品详情
     * @param id
     * @return
     */
    @GetMapping("spu/detail/{sid}")
    SpuDetail queryDetailById(@PathVariable("sid")Long id);

    /**
     * 根据spuid查询spu
     * @param id
     * @return
     */
    @GetMapping("spu/{id}")
    Spu querySpuById(@PathVariable("id")Long id);

    /**
     * 根据skuid查询sku
     * @param skuid
     * @return
     */
    @GetMapping("sku/{skuId}")
    Sku querySkuBySkuId(@PathVariable("skuId") Long skuId);
    /**
     * 根据ids查询sku
     */
    @GetMapping("sku/list/ids")
    List<Sku> querySkusBySkuids(@RequestParam("ids") List<Long> ids);

    /**
     * 修改库存
     * @param carts
     * @return
     */
    @PostMapping("stock/decrease")
    public Void decreaseStock(@RequestBody List<CartDto> carts);
}
