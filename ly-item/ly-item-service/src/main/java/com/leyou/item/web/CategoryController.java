package com.leyou.item.web;

import com.leyou.item.pojo.Category;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @GetMapping("/list")
    public ResponseEntity<List<Category>> queryCategoryByPid(@RequestParam(name = "pid",required = false)Long pid){
        return ResponseEntity.ok(categoryService.queryCategoryListByPid(pid));
    }

    /**
     * 通过品牌查询分类
     * @param id
     * @return
     */
    @GetMapping("bid/{cid}")
    public ResponseEntity<List<Category>> findCategoryBybid(@PathVariable("cid")Long id){
        System.out.println(id);
        return ResponseEntity.ok(categoryService.findByBid(id));
    }

    /**
     * 根据分类id列表查商品  分类列表
     * @param ids
     * @return
     */
    @GetMapping("list/ids")
    public ResponseEntity<List<Category>> queryCategoryByIds(@RequestParam("ids")List<Long> ids){
        return ResponseEntity.ok(categoryService.queryByIds(ids));
    }
    @GetMapping("list/parentid")
    public ResponseEntity<List<Category>> queryCategoryByParentId(@RequestParam(value = "id",required = false)Long id){
        return ResponseEntity.ok(categoryService.queryCategoryListByPid(id));
    }
    /**
     * 根据三级分类查询1-3及分类
     * @param cid3
     * @return
     */
    @GetMapping("all/level")
    public ResponseEntity<List<Category>> queryAllByCid3(@RequestParam("cid3") Long cid3){
        List<Category> list = categoryService.queryCategoryByCid3(cid3);
        if (CollectionUtils.isEmpty(list)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(list);
    }
}
