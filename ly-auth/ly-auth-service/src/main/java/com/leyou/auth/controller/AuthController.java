package com.leyou.auth.controller;

import com.leyou.auth.config.JwtConfig;
import com.leyou.auth.pojo.UserInfo;
import com.leyou.auth.service.AuthService;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private JwtConfig jwtConfig;
    @PostMapping("accredit")
    public ResponseEntity<Void> login(@RequestParam("username")String username, @RequestParam("password")String password, HttpServletRequest req, HttpServletResponse resp){
        //掉用service方法生成jwt
        String token = authService.login(username,password);
        if(StringUtils.isBlank(token)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        //使用cookieutils将jwt存入cookie
        CookieUtils.setCookie(req,resp,jwtConfig.getCookieName(),token,jwtConfig.getExpire()*60,"utf-8",true);
        return ResponseEntity.ok().build();
    }
    @GetMapping("verify")
    public ResponseEntity<UserInfo> verify(@CookieValue("LY_TOKEN")String token, HttpServletRequest req, HttpServletResponse resp){
        UserInfo userInfo = null;
        try {
            userInfo = JwtUtils.getInfoFromToken(token,jwtConfig.getPublicKey());

        if(userInfo == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        //刷新jwt的过期时间（重新生成一个jwt）
        JwtUtils.generateToken(userInfo,jwtConfig.getPrivateKey(),jwtConfig.getExpire());
        //刷新cookie过期时间
            CookieUtils.setCookie(req,resp,jwtConfig.getCookieName(),token,jwtConfig.getExpire(),"utf-8",true);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(userInfo);
    }
}
