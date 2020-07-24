package com.leyou.auth.config;

import com.leyou.auth.utils.RsaUtils;
import lombok.Data;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;
@Getter
@ConfigurationProperties(prefix = "ly.jwt")
public class JwtConfig {
    private String secret;
    private String pubKeyPath;
    private String priKeyPath;
    private Integer expire;
    private PublicKey publicKey;
    private PrivateKey privateKey;
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
            File priKey = new File(priKeyPath);
            if (!pubKey.exists() || !priKey.exists()) {
                // 生成公钥和私钥
                RsaUtils.generateKey(pubKeyPath, priKeyPath, secret);
            }
            // 获取公钥和私钥
            this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
            this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
        } catch (Exception e) {
            logger.error("初始化公钥和私钥失败！", e);
            throw new RuntimeException();
        }
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setPubKeyPath(String pubKeyPath) {
        this.pubKeyPath = pubKeyPath;
    }

    public void setPriKeyPath(String priKeyPath) {
        this.priKeyPath = priKeyPath;
    }

    public void setExpire(Integer expire) {
        this.expire = expire;
    }

    // getter setter ...
}
