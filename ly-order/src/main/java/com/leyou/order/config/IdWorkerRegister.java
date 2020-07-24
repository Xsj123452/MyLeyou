package com.leyou.order.config;


import com.leyou.common.utils.IdWorker;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(IdWorkerConfig.class)
public class IdWorkerRegister {
    @Bean
    public IdWorker idWorker(IdWorkerConfig idWorkerConfig){
        return new IdWorker(idWorkerConfig.getWorkerId(),idWorkerConfig.getDataCenterId());
    }
}
