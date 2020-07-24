package com.leyou.user.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ly.user")
public class UserConfig {
    private String exchange;
    private String routingKey;
    private Integer live_time;
}
