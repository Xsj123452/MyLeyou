package com.leyou.cart.service;

import com.leyou.auth.pojo.UserInfo;
import com.leyou.cart.client.GoodsClient;
import com.leyou.cart.interceptor.LoginInterceptor;
import com.leyou.cart.pojo.Cart;
import com.leyou.common.utils.JsonUtils;
import com.leyou.item.pojo.Sku;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class   CartService {
    @Autowired
    private StringRedisTemplate template;
    @Autowired
    private GoodsClient goodsClient;

    public void saveCart(Cart cart) {
        UserInfo userInfo = LoginInterceptor.get();
        //获取外部map
        BoundHashOperations<String, Object, Object> hashOption = template.boundHashOps(userInfo.getId().toString());
        String s = cart.getSkuId().toString();
        if (hashOption.hasKey(s)) {
            //如果有
            Integer num = cart.getNum();
            String s1 = hashOption.get(s).toString();
            cart = JsonUtils.parse(s1, Cart.class);
            cart.setNum(cart.getNum()+num);

        }else {
            //没有则新增
            cart.setUserId(userInfo.getId());
            Sku sku = goodsClient.querySkuBySkuId(cart.getSkuId());
            cart.setPrice(sku.getPrice());
            cart.setImage(StringUtils.isBlank(sku.getImages())?"":StringUtils.split(sku.getImages(),",")[0]);
            cart.setOwnSpec(sku.getOwnSpec());
            cart.setTitle(sku.getTitle());
        }
        hashOption.put(s,JsonUtils.serialize(cart));
    }

    public List<Cart> queryCards() {
        UserInfo user = LoginInterceptor.get();
        //判断hash对象是否存在
        if (!template.hasKey(user.getId().toString())) {
            return null;
        }
        //查询
        BoundHashOperations<String, Object, Object> map = template.boundHashOps(user.getId().toString());
        List<Object> jsons = map.values();
        return jsons.stream().map(json ->  JsonUtils.parse(json.toString(),Cart.class)).collect(Collectors.toList());
    }

    public void updateCart(Cart cart) {
        UserInfo user = LoginInterceptor.get();
        BoundHashOperations<String, Object, Object> map = template.boundHashOps(user.getId().toString());
        cart.setUserId(user.getId());
        Sku sku = goodsClient.querySkuBySkuId(cart.getSkuId());
        cart.setPrice(sku.getPrice());
        cart.setImage(StringUtils.isBlank(sku.getImages())?"":StringUtils.split(sku.getImages(),",")[0]);
        cart.setOwnSpec(sku.getOwnSpec());
        cart.setTitle(sku.getTitle());
        map.put(cart.getSkuId().toString(),JsonUtils.serialize(cart));
    }

    public void deleteCart(Long skuId) {
        UserInfo user = LoginInterceptor.get();
        BoundHashOperations<String, Object, Object> map = template.boundHashOps(user.getId().toString());
        map.delete(skuId.toString());
    }
}
