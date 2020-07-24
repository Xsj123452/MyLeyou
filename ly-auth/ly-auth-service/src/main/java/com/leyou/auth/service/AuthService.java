package com.leyou.auth.service;

import com.leyou.auth.client.UserClient;
import com.leyou.auth.config.JwtConfig;
import com.leyou.auth.pojo.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@EnableConfigurationProperties(JwtConfig.class)
public class AuthService {
    @Autowired
    private UserClient userClient;
    @Autowired
    private JwtConfig jwtConfig;

    public String login(String username, String password) {
        //掉用user的远程接口
        User user = userClient.queryUser(username, password);
        //判断user是否存在
        if(user == null){
            return null;
        }
        UserInfo userInfo = new UserInfo(user.getId(), user.getUsername());
        //生成jwt类型的token
        try {
            String s = JwtUtils.generateToken(userInfo, jwtConfig.getPrivateKey(), jwtConfig.getExpire()*60);
            System.out.println(s);
            return s;
        } catch (Exception e) {
            return null;
        }
    }
}
