package com.leyou.page.web;

import com.leyou.page.client.GoodsClient;
import com.leyou.page.client.SpecificationClent;
import com.leyou.page.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
public class PageController {
    @Autowired
    private PageService pageService;

    @GetMapping("item/{id}.html")
    public String toItemPage(@PathVariable("id")Long id, Model model){
        //查询模型数据
        Map<String,Object> attributes = pageService.loadModel(id);
        //准备数据放入model
        model.addAllAttributes(attributes);
        //返回视图名
        return "item";
    }
}
