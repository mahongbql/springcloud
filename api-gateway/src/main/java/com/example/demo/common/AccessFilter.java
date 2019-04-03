package com.example.demo.common;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author mahongbin
 * @date 2019/4/3 9:47
 */
@Component
@Slf4j
public class AccessFilter extends ZuulFilter {

    /**
     * 过滤器类型，它决定过滤器在请求的哪个生命周期中执行，这里定义为pre，代表会在请求被路由之前执行
     * @return
     */
    @Override
    public String filterType() {
        return "pre";
    }

    /**
     * 过滤器的执行顺序，当请求在一个阶段中存在多个过滤器时，需要根据该方法返回的值依次执行
     * @return
     */
    @Override
    public int filterOrder() {
        return 0;
    }

    /**
     * 判断该过滤器是否需要被执行，这里直接返回了true，因此该过滤对所有请求都会生效。
     * 实际应用中，我们可以利用该函数来指定过滤器的有效范围
     * @return
     */
    @Override
    public boolean shouldFilter() {
//        RequestContext ctx = RequestContext.getCurrentContext();
//        HttpServletRequest request = ctx.getRequest();
//        String token = ctx.getRequest().getHeader(Constants.AUTHORIZATION);
//        if (StringUtils.isEmpty(token)) {
//            return false;
//        } else {
//            String url = request.getRequestURI();
//            return isIgnoreUrl(url);
//        }

        return true;
    }

    /**
     * 过滤器的具体逻辑，这里通过ctx.setSendZuulResponse(false)，令zuul过滤该请求，不对其进行路由，然后通过ctx.setResponseStatusCode(401)设置其错误码
     * 进一步优化我们的返回，通过ctx.setResponseBody(body)对返回的body内容进行编辑等
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        log.info("send {},request to {} ", request.getMethod(), request.getRequestURL().toString());

        Object accessToken = request.getParameter("accessToken");

        if(null == accessToken) {
            log.warn("access token is empty");
            ctx.setResponseStatusCode(401);
            ctx.setSendZuulResponse(false);
            return null;
        }

        log.info("access token ok");
        return null;
    }
}
