package com.laioffer.staybooking.filter;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//此class是判断是否支持跨域访问
@Component
@Order(Ordered.HIGHEST_PRECEDENCE) //把这个filter放在第一节
public class CorsFilter extends OncePerRequestFilter {

    @Override //只要继承这个，就要强制重写这个
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        //filter chain起到的作用，过滤本层成功后，再调用后面一个一个filter去过滤
        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*"); // * = 通配，支持所有跨域
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");

        if ("OPTIONS".equalsIgnoreCase(httpServletRequest.getMethod())) {
            //知道是option请求，就直接返回response ok
            //option请求先撩一下后端，判断后端是否支持跨域访问，就是个preflight
            //对于option请求来说，就足够了，因为option请求的目的就是check一下是否支持跨域
            //之后浏览器检测出后端支持跨域了，再做真正的filter
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        } else {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            //如果进来的不是pre flight，那交给filter chain里面的filter处理
        }
    }
}

//original:
//client -- > server
// HTTP GET(lat, lon, user, xxx)

//with cross region:                     |        这个class完成了这个范围的内容               |
//Client --> HTTP OPTIONS() --> server --> set responseHeader(1,2,3), set responseStatus(ok) --> Return Response to client

// Client(Browser) check response header, decide whether server supports CORS request
// if not, return error to js(frontend code)
// if yes, HTTP GET(lat, lon, user, xxx) --> Set ResponseHeader(1,2,3) --> DoFilter(jwt, uaf,xxx)--> dispatch request to SearchController
// if Dofilter failed --> 401 Unauthorized error to client