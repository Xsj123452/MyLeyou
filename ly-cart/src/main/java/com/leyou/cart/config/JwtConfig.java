package com.leyou.cart.config;

import com.leyou.auth.utils.RsaUtils;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PublicKey;

@Getter
@ConfigurationProperties(prefix = "ly.jwt")
public class JwtConfig {

    private String pubKeyPath;
    private PublicKey publicKey;
    private String cookieName;

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    private static final Logger logger = LoggerFactory.getLogger(JwtConfig.class);

    /**
     * 在构造方法之后执行该方法
     */
    @PostConstruct
    public void init(){
        try {
            File pubKey = new File(pubKeyPath);
            this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        } catch (Exception e) {
            logger.error("初始化公钥和私钥失败！", e);
            throw new RuntimeException();
        }
    }
    public void setPubKeyPath(String pubKeyPath) {
        this.pubKeyPath = pubKeyPath;
    }

    // getter setter ...
}
