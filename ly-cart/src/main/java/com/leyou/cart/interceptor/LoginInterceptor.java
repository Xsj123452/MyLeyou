package com.leyou.cart.interceptor;

import com.leyou.auth.pojo.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.cart.config.JwtConfig;
import com.leyou.common.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@EnableConfigurationProperties(JwtConfig.class)
@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private JwtConfig jwtConfig;
    private static final ThreadLocal<UserInfo> threadLocal = new InheritableThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取token
        String token = CookieUtils.getCookieValue(request, jwtConfig.getCookieName());
        //判断token
        if (StringUtils.isBlank(token)) {
            return false;
        }
        //解析token
        UserInfo userinfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
        //放在threadlocal中在这个线程中都可以获取这个userinfo
        threadLocal.set(userinfo);
        return true;
    }
    public static UserInfo get(){
        return threadLocal.get();
    }

    /**
     * 清楚线程变量
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        threadLocal.remove();
    }
}
