package com.leyou.item.web;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.service.BrandService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/brand")
public class BrandController {
    @Resource(name = "brandService")
    private BrandService brandService;
    @GetMapping("/page")
    public ResponseEntity<PageResult<Brand>> queryBrand(
            @RequestParam(value = "page",defaultValue = "1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows,
            @RequestParam(value = "sortBy",required = false) String sortBy,
            @RequestParam(value = "desc",defaultValue = "false")Boolean desc,
            @RequestParam(value = "key",required = false)String key
    ){
        return ResponseEntity.ok(brandService.queryBrandBypage(page,rows,sortBy,desc,key));
    }
    @PostMapping
    public ResponseEntity<Void> save(Brand brand,@RequestParam(name = "cids")List<Long> cids){
        brandService.saveBrand(brand,cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    /**
     * 查询修改用户信息
     */
    @GetMapping("bid/{id}")
    public ResponseEntity<Brand> findById(@PathVariable("id") Long bid){
        return ResponseEntity.ok(brandService.findById(bid));
    }
    @PutMapping
    public ResponseEntity<Void> update(Brand brand,@RequestParam(name = "cids")List<Long> cids){
        brandService.update(brand);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id){
        brandService.delete(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 通过分类id查询品牌
     * 查询当前分类下的所有品牌
     * @param cid
     * @return
     */
    @GetMapping("cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandByBid(@PathVariable("cid") Long cid){
        List<Brand> brands = brandService.queryBrandByCid(cid);
        if(CollectionUtils.isEmpty(brands)){
            return ResponseEntity.notFound().build();
        }else {
            return ResponseEntity.ok(brands);
        }
    }
    @GetMapping("brands")
    ResponseEntity<List<Brand>> queryBrandByIds(@RequestParam("ids")List<Long> ids){
        return ResponseEntity.ok(brandService.queryBrandByIds(ids));
    }
}
