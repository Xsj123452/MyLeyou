package com.leyou.order.service;

import com.leyou.auth.pojo.UserInfo;
import com.leyou.common.dto.CartDto;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.IdWorker;
import com.leyou.item.pojo.Sku;
import com.leyou.order.client.AddressClient;
import com.leyou.order.client.GoodsClient;
import com.leyou.order.dto.AddressDto;
import com.leyou.order.dto.OrderDto;
import com.leyou.order.enums.StatusEnums;
import com.leyou.order.interceptor.LoginInterceptor;
import com.leyou.order.mapper.OrderDetailMapper;
import com.leyou.order.mapper.OrderMapper;
import com.leyou.order.mapper.OrderStatusMapper;
import com.leyou.order.pojo.Order;
import com.leyou.order.pojo.OrderDetail;
import com.leyou.order.pojo.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private OrderStatusMapper orderStatusMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private GoodsClient goodsClient;
    @Transactional
    public Long createOrder(OrderDto orderDto) {
        //新增订单
        Order order = new Order();
        //1.组织订单数据
        //1.1订单编号
        long l = idWorker.nextId();//生成订单编号
        order.setOrderId(l);
        order.setCreateTime(new Date());
        order.setPaymentType(orderDto.getPaymentType());
        //1.2获取用户信息
        UserInfo user = LoginInterceptor.get();
        order.setUserId(user.getId());
        order.setBuyerNick(user.getUsername());
        order.setBuyerRate(false);//评价
        //1.3收货人信息
        AddressDto address = AddressClient.findById(orderDto.getAddressId());
        assert address != null;
        order.setReceiver(address.getName());
        order.setReceiverAddress(address.getAddress());
        order.setReceiverCity(address.getCity());
        order.setReceiverDistrict(address.getDistrict());
        order.setReceiverMobile(address.getPhone());
        order.setReceiverState(address.getState());
        order.setReceiverZip(address.getZipCode());
        //1.4金额
        long totalPay = 0;
        Map<Long, Integer> carts = orderDto.getCarts().stream().collect(Collectors.toMap(CartDto::getSkuId, CartDto::getNum));
        Set<Long> ids = carts.keySet();
        List<Sku> skus = goodsClient.querySkusBySkuids(new ArrayList<>(ids));
        //准备orderDetail集合
        List<OrderDetail>  details = new ArrayList<>();
        for (Sku sku : skus) {
            totalPay += sku.getPrice()*carts.get(sku.getId());
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setSkuId(sku.getId());
            orderDetail.setImage(StringUtils.substringBefore(sku.getImages(),","));
            orderDetail.setNum(carts.get(sku.getId()));
            orderDetail.setOrderId(order.getOrderId());
            orderDetail.setOwnSpec(sku.getOwnSpec());
            orderDetail.setPrice(sku.getPrice());
            orderDetail.setTitle(sku.getTitle());
            details.add(orderDetail);
        }
        order.setTotalPay(totalPay);
        //实付金额
        order.setActualPay(totalPay+order.getPostFee()-0);
        //1.5把order写入数据库
        int i = orderMapper.insertSelective(order);
        if (i != 1) {
        log.error("创建订单失败,orderId:{}",order.getOrderId());
        throw new LyException(ExceptionEnum.CREATE_ORDER_ERROR);
        }
        //新增订单详情
        i = orderDetailMapper.insertList(details);
        if (i == 0) {
            log.error("创建订单失败,orderId:{}",order.getOrderId());
            throw new LyException(ExceptionEnum.CREATE_ORDER_ERROR);
        }
        //新增订单状态
        OrderStatus status = new OrderStatus();
        status.setCreateTime(order.getCreateTime());
        status.setOrderId(order.getOrderId());
        status.setStatus(StatusEnums.UN_PAY.getCode());
        orderStatusMapper.insertSelective(status);
        //删减库存
        goodsClient.decreaseStock(orderDto.getCarts());
        return  order.getOrderId();
    }
}
