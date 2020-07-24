package com.leyou.item.web;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.common.dto.CartDto;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

public class GoodsController {
    @Autowired
    private GoodsService goodsService;

    /**
     * 新增商品
     * @param spu
     * @return
     */
    @PostMapping("goods")
    public ResponseEntity<Void> saveGood(@RequestBody Spu spu){
        goodsService.saveGood(spu);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 分页查询商品spu
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("spu/page")
    public ResponseEntity<PageResult<Spu>> queryAllGoods(@RequestParam(value = "key",required = false)String key,@RequestParam(value = "saleable",required = false)Boolean saleable
    ,@RequestParam(value = "page",required = false,defaultValue = "1")Integer page,@RequestParam(value = "rows",required = false,defaultValue = "5")Integer rows){
        System.out.println(page);
        return ResponseEntity.ok(goodsService.queryByTerm(key,saleable,page,rows));
    }

    /**
     * 查询商品细节
     * @param id
     * @return
     */
    @GetMapping("spu/detail/{sid}")
    public ResponseEntity<SpuDetail> queryDetailById(@PathVariable("sid")Long id){
        return ResponseEntity.ok(goodsService.querySpudetailBySid(id));
    }
    @GetMapping("/sku/list")
    public ResponseEntity<List<Sku>> querySkuListBySpuid(@RequestParam("id")Long id){
        return ResponseEntity.ok(goodsService.querySkusBySpuId(id));
    }
    @GetMapping("spu/{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(goodsService.querySpuByid(id));
    }
    @PutMapping("goods")
    public ResponseEntity<Void> updateSpu(@RequestBody Spu spu){
        goodsService.updateSpu(spu);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @DeleteMapping("spu/delete/{id}")
    public ResponseEntity<Void> deleteSpu(@PathVariable(name = "id")Long id){
        goodsService.deleteSpu(id);
        return ResponseEntity.ok().build();
    }
    /**
     * 根据skuid查询sku
     */
    @GetMapping("sku/{skuId}")
    public ResponseEntity<Sku> querySkuBySkuId(@PathVariable("skuId") Long skuId){
        Sku sku = goodsService.querySkuBuSkuid(skuId);
        if(sku == null){
            //sku没有则抛出异常
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(sku);
    }
    /**
     * 根据skuids查询sku
     */

    @GetMapping("sku/list/ids")
    public ResponseEntity<List<Sku>> querySkusBySkuids(@RequestParam("ids") List<Long> ids){
        return ResponseEntity.ok(goodsService.querySkuByIds(ids));
    }
    /**
     * 减少库存
     */
    @PostMapping("stock/decrease")
    public ResponseEntity<Void> decreaseStock(@RequestBody List<CartDto> carts){
        goodsService.decreaseStock(carts);
        return ResponseEntity.ok().build();
    }
}
