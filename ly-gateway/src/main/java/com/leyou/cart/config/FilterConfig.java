package com.leyou.cart.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("ly.filter")
@Data
public class FilterConfig {
    private List<String> allowPaths;
}
