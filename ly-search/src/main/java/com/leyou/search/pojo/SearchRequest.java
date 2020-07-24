package com.leyou.search.pojo;

import lombok.Data;

import java.util.Map;

public class SearchRequest {
    private String key;//搜索条件
    private Integer page;//当前页面
    private Map<String,String> filter;//过滤条件

    public Map<String, String> getFilter() {
        return filter;
    }

    public void setFilter(Map<String, String> filter) {
        this.filter = filter;
    }


    private static final int DEFAULT_ROWS = 20;//默认当前页数据记录数
    private static final int DEFAULT_PAGE = 1;//默认当前页为1
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getPage() {
        if(page==null){
            this.page = DEFAULT_PAGE;
        }
        //简单的校验当前页，确保页面页码大于等于1
        return Math.max(DEFAULT_PAGE,page);
    }

    public void setPage(Integer page) {

        this.page = page;
    }

    public static int getDefaultRows() {
        return DEFAULT_ROWS;
    }

}
