package com.leyou.item.api;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface SpecificationApi {
    @GetMapping("spec/params")
    List<SpecParam> queryParamsByGid(@RequestParam(name = "gid",required = false) Long gid, @RequestParam(name = "cid",required = false)Long cid, @RequestParam(value = "searching",required = false)Boolean searching);
    @GetMapping("spec/group")
    List<SpecGroup> queryListByCid(@RequestParam("cid")Long cid);
}