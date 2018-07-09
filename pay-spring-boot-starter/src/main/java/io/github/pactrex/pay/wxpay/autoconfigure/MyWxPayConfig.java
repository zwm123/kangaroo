package io.github.pactrex.pay.wxpay.autoconfigure;

import com.github.wxpay.sdk.IWXPayDomain;
import com.github.wxpay.sdk.WXPayConfig;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class MyWxPayConfig extends WXPayConfig {

    private WxPayProperties wxPayProperties;

    private IWXPayDomain wxPayDomain;

    public MyWxPayConfig(WxPayProperties wxPayProperties) {
        this.wxPayProperties = wxPayProperties;
        this.wxPayDomain = new MyWxPayDomain();
    }

    @Override
    public String getAppID() {
        return wxPayProperties.getAppId();
    }

    @Override
    public String getMchID() {
        return wxPayProperties.getMchId();
    }

    @Override
    public String getKey() {
        return wxPayProperties.getKey();
    }

    @Override
    public InputStream getCertStream() {
        try {
            File file = ResourceUtils.getFile(wxPayProperties.getCertFile());
            return new FileInputStream(file);
        } catch (Exception e) {
            throw new RuntimeException("wxpay certFile read fail");
        }
    }

    @Override
    public int getHttpConnectTimeoutMs() {
        return super.getHttpConnectTimeoutMs();
    }

    @Override
    public int getHttpReadTimeoutMs() {
        return super.getHttpReadTimeoutMs();
    }

    @Override
    public IWXPayDomain getWXPayDomain() {
        return null;
    }

    @Override
    public boolean shouldAutoReport() {
        return super.shouldAutoReport();
    }

    @Override
    public int getReportWorkerNum() {
        return super.getReportWorkerNum();
    }

    @Override
    public int getReportQueueMaxSize() {
        return super.getReportQueueMaxSize();
    }

    @Override
    public int getReportBatchSize() {
        return super.getReportBatchSize();
    }
}
