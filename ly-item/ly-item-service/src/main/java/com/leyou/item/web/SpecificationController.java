package com.leyou.item.web;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/spec")
public class SpecificationController {
    @Autowired
    private SpecificationService specificationService;

    /**
     * 通过放分类id查找组
     * @param cid
     * @return
     */
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>>queryGroupByCid(@PathVariable("cid")Long cid){
        return ResponseEntity.ok(specificationService.queryGroupById(cid));
    }
    @PostMapping("/group")
    public ResponseEntity<Void> saveGroup(@RequestBody()SpecGroup specGroup){
        specificationService.saveGroup(specGroup);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/group/{id}")
    public ResponseEntity<Void> deleteGroupById(@PathVariable("id")Long id){
        specificationService.deleteGroupById(id);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/params")
    public ResponseEntity<List<SpecParam>> queryParamsByGid(@RequestParam(name = "gid",required = false) Long gid,@RequestParam(name = "cid",required = false)Long cid,@RequestParam(value = "searching",required = false)Boolean searching){
        return ResponseEntity.ok(specificationService.querySpecParamByGid(gid,cid,searching));
    }

    /**
     * 根据分类查询规格组及规格参数
     * @param cid
     * @return
     */
    @GetMapping("/group")
    public ResponseEntity<List<SpecGroup>> queryListByCid(@RequestParam("cid")Long cid){
        return ResponseEntity.ok(specificationService.queryListByCid(cid));
    }
}
