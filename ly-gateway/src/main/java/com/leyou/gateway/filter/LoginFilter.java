package com.leyou.gateway.filter;

import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import com.leyou.cart.config.FilterConfig;
import com.leyou.cart.config.JwtConfig;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@EnableConfigurationProperties({JwtConfig.class, FilterConfig.class})
public class LoginFilter extends ZuulFilter {
    @Autowired
    private JwtConfig jwtConfig;
    @Autowired
    private FilterConfig filterConfig;
    @Override
    public boolean shouldFilter() {
        //初始化zuul网关的上下文对象
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest req = context.getRequest();
        String url = req.getRequestURL().toString();
        //判断接口是否在白名单中
        for (String path : filterConfig.getAllowPaths()) {
            if(url.contains(path)){
                return false;
            }
        }
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        //初始化zuul网关的上下文对象
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest req = context.getRequest();
        //获取cookie
        String token = CookieUtils.getCookieValue(req, jwtConfig.getCookieName());
        //解析cookie中的jwt
        try {
           JwtUtils.getInfoFromToken(token,jwtConfig.getPublicKey());
        } catch (Exception e) {
            //为false表示zull网关不会在进行转发，即为被拦截了
            context.setSendZuulResponse(false);
            context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());

        }
        return null;
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 10;
    }
}
