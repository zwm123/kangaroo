package io.github.pactstart.simple.web.framework.interceptor;

import com.google.common.collect.Maps;
import io.github.pactstart.commonutils.JsonUtils;
import io.github.pactstart.simple.web.framework.auth.AuthenticationInfo;
import io.github.pactstart.simple.web.framework.common.RequestHolder;
import io.github.pactstart.simple.web.framework.utils.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;

@Slf4j
public class HttpInterceptor extends HandlerInterceptorAdapter {

    private static final String START_TIME = "requestStartTime";

    private boolean servletRequestWrapperEnabled = false;

    public HttpInterceptor(boolean servletRequestWrapperEnabled) {
        this.servletRequestWrapperEnabled = servletRequestWrapperEnabled;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        long start = System.currentTimeMillis();
        request.setAttribute(START_TIME, start);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        HashMap<String, Object> data = Maps.newHashMap();
        String url = request.getRequestURI().toString();
        long start = (Long) request.getAttribute(START_TIME);
        long end = System.currentTimeMillis();

        String params = "";

        MediaType mediaType = null;
        if (request.getContentType() != null) {
            mediaType = MediaType.valueOf(request.getContentType());
        }
        if (mediaType != null && (MediaType.APPLICATION_JSON.equals(mediaType) || MediaType.APPLICATION_JSON_UTF8.equals(mediaType))) {
            InputStream in = request.getInputStream();
            // 框架已使用流，指针已指向到末尾，servlet流不支持复位，读取之后再读取会抛出异常，HttpMessageNotReadableException Required request body is missing
            // 开启了wrapper才能对流进行复位，否则抛异常java.io.IOException: mark/reset not supported
            if (servletRequestWrapperEnabled && in.markSupported()) {
                in.reset();
                StringWriter sw = new StringWriter();
                IOUtils.copy(in, sw);
                params = sw.toString();
            }
        } else {
            params = JsonUtils.obj2String(request.getParameterMap());
        }
        data.put("requestMethod", request.getMethod());
        data.put("contentType", request.getContentType());
        data.put("startTime", start);
        data.put("endTime", end);
        data.put("cost", end - start);
        data.put("params", params);
        data.put("url", url);
        data.put("ip", IpUtils.getClientIpAddr(request));
        data.put("device", request.getHeader("device"));
        AuthenticationInfo authenticationInfo = RequestHolder.getAuthenticationInfo();
        if (authenticationInfo != null) {
            data.put("userId", authenticationInfo.getUserId());
            data.put("userName", authenticationInfo.getUserName());
        }
        //在GlobalExceptionHandler中设置
        Object e = request.getAttribute("ex");
        boolean hasEx = e != null;
        data.put("hasEx", hasEx);
        if (hasEx) {
            Exception exception = (Exception) e;
            data.put("exceptionClass", exception.getClass().getName());
            data.put("exceptionMessage", exception.getMessage());
            request.removeAttribute("ex");
        }
        log.info(JsonUtils.obj2String(data));
        removeThreadLocalInfo();
    }

    public void removeThreadLocalInfo() {
        RequestHolder.remove();
    }
}
