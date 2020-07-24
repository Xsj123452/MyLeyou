package com.leyou.sms.mqListener;

import com.aliyuncs.exceptions.ClientException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.sms.config.SmsProperties;
import com.leyou.sms.util.SmsUtils;
import com.rabbitmq.tools.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

@Component
@Slf4j
@EnableConfigurationProperties
public class SmsListener {
    @Autowired
    private SmsProperties props;
    @Autowired
    private SmsUtils smsUtils;
    @RabbitListener(bindings =@QueueBinding(
            value = @Queue(name = "sms.verify.code.queue",durable = "true"),
            exchange = @Exchange(name = "ly.sms.exchange",type = ExchangeTypes.TOPIC),
            key = "ly.verify.code"
    ))
    public void listenerSend(Map<String,String> msg){
        log.info("开始");
        if(CollectionUtils.isEmpty(msg)){
            return;
        }
        String phone = msg.remove("phone");
        if(StringUtils.isBlank(phone)){
            return;
        }
            smsUtils.sendSms(phone,props.getSignname(),props.getVerifyCodeTemplate(), JsonUtils.serialize(msg));
        log.info("[短信服务]，发送短信验证码，手机号{}",phone);
    }
}
