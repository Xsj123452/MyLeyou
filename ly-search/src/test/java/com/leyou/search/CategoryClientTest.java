package com.leyou.search;

import com.leyou.item.pojo.Category;
import com.leyou.search.client.CategoryClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CategoryClientTest {
    @Autowired
    private CategoryClient cc;

    @Test
    public void queryCategoryByIds() {
        List<Category> categories = cc.queryCategoryByIds(Arrays.asList(1L, 2L, 3L));
        System.out.println(categories);
    }
}