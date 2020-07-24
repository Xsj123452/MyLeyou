package com.leyou.item.service;

import com.leyou.item.mapper.StockMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GoodsServiceTest {
    @Autowired
    private StockMapper stockMapper;
    @Test
    public void decreaseStock() {
        stockMapper.decreaseStock(2600242L,1);
    }
}