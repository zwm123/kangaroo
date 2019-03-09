package io.github.pactstart.simple.web.framework.encrypt.core;

import com.google.common.collect.Maps;
import io.github.pactstart.biz.common.errorcode.ResponseCode;
import io.github.pactstart.commonutils.ValidUtils;
import io.github.pactstart.simple.web.framework.encrypt.algorithm.AesEncryptAlgorithm;
import io.github.pactstart.simple.web.framework.encrypt.algorithm.EncryptAlgorithm;
import io.github.pactstart.simple.web.framework.encrypt.springboot.autoconfigure.EncryptApiScanner;
import io.github.pactstart.simple.web.framework.encrypt.springboot.autoconfigure.EncryptConfig;
import io.github.pactstart.simple.web.framework.encrypt.utils.SignUtils;
import io.github.pactstart.simple.web.framework.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
public class EncryptFilter implements Filter {

    private EncryptConfig encryptConfig;

    private EncryptAlgorithm encryptAlgorithm;

    public EncryptFilter(EncryptConfig encryptConfig, EncryptAlgorithm encryptAlgorithm) {
        this.encryptConfig = encryptConfig;
        if (encryptAlgorithm == null) {
            this.encryptAlgorithm = new AesEncryptAlgorithm();
        } else {
            this.encryptAlgorithm = encryptAlgorithm;
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        if (req.getContentType() == null || !req.getContentType().contains("application/json")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        if (encryptConfig.isDebug()) {
            filterChain.doFilter(req, resp);
            return;
        }
        String uri = req.getRequestURI();
        List<String> actualEncryptUriList = this.getActualEncryptUriList();

        boolean isEnableEncrypt = false;
        String device = req.getHeader("device");
        if (encryptConfig.getRequestSourceList().contains(device)) {
            // 没有配置具体加解密的URI默认全部都开启加解密
            if (ValidUtils.isValid(actualEncryptUriList)) {
                isEnableEncrypt = this.contains(actualEncryptUriList, uri, req.getMethod());
            } else {
                isEnableEncrypt = true;
            }
        }

        EncryptRequestWrapper requestWrapper = null;
        EncryptResponseWrapper responseWrapper = null;

        if (!isEnableEncrypt) {
            filterChain.doFilter(req, resp);
            return;
        }
        //请求解密
        requestWrapper = new EncryptRequestWrapper(req);
        String requestData = requestWrapper.getRequestData();
        log.debug("uri:{},RequestData: {}", uri, requestData);

        if (encryptConfig.isCheckSign()) {
            String sign = req.getHeader("sign");
            String timestamp = req.getHeader("timestamp");
            if (!ValidUtils.isValid(timestamp)) {
                ResponseUtils.outputJson(resp, ResponseCode.LACK_NECESSARY_REQUEST_HEADER);
                return;
            }
            if (Math.abs(System.currentTimeMillis() - Long.valueOf(timestamp)) > encryptConfig.getExpireTime()) {
                ResponseUtils.outputJson(resp, ResponseCode.REQUEST_EXPIRED);
                return;
            }
            Map<String, Object> params = Maps.newHashMap();
            params.put("timestamp", timestamp);
            params.put("data", requestData);
            String sign2 = SignUtils.sign(params, encryptConfig.getKey());
            if (!sign2.equalsIgnoreCase(sign)) {
                ResponseUtils.outputJson(resp, ResponseCode.SIGN_INVALID);
                return;
            }
        }
        if (requestData.startsWith("{")) {
            filterChain.doFilter(req, resp);
            return;
        }
        try {
            String decyptRequestData = encryptAlgorithm.decrypt(requestData, encryptConfig.getKey());
            log.debug("DecryptRequestData: {}", decyptRequestData);
            requestWrapper.setRequestData(decyptRequestData);
        } catch (Exception e) {
            log.error("请求数据解密失败", e);
            ResponseUtils.outputJson(resp, new ResponseCode(ResponseCode.SYSTEM_ERROR, "数据解密失败"));
            return;
        }
        //构造响应加密请求
        responseWrapper = new EncryptResponseWrapper(resp);
        //过滤链处理
        filterChain.doFilter(requestWrapper, responseWrapper);
        //响应加密
        String responseData = responseWrapper.getResponseData();
        log.debug("ResponseData: {}", responseData);
        ServletOutputStream out = null;
        try {
            responseData = encryptAlgorithm.encrypt(responseData, encryptConfig.getKey());
            log.debug("EncryptResponseData: {}", responseData);

            servletResponse.setContentLength(responseData.length());
            servletResponse.setCharacterEncoding(encryptConfig.getResponseCharset());
            out = servletResponse.getOutputStream();
            out.write(responseData.getBytes(encryptConfig.getResponseCharset()));
        } catch (Exception e) {
            log.error("响应数据加密失败", e);
            throw new RuntimeException(e);
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }

    @Override
    public void destroy() {

    }

    public List<String> getActualEncryptUriList() {
        // 配置了注解则用注解获取的URI
        if (ValidUtils.isValid(EncryptApiScanner.encryptUriList)) {
            return EncryptApiScanner.encryptUriList;
        }
        return encryptConfig.getEncryptUriList();
    }

    private boolean contains(List<String> list, String uri, String methodType) {
        if (list.contains(uri)) {
            return true;
        }
        String prefixUri = methodType.toLowerCase() + ":" + uri;
        if (list.contains(prefixUri)) {
            return true;
        }
        return false;
    }
}
