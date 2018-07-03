package io.github.pactrex.pay.wxpay;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConfig;
import io.github.pactrex.pay.wxpay.autoconfigure.WxPayConfig;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class WxPayService {

    private final WxPayConfig wxPayConfig;

    private final WXPay wxPay;

    public WxPayService(WxPayConfig wxPayConfig) throws Exception {
        this.wxPayConfig = wxPayConfig;
        File file = ResourceUtils.getFile(wxPayConfig.getCertFile());
        final InputStream in = new FileInputStream(file);
        this.wxPay = new WXPay(new WXPayConfig() {
            @Override
            public String getAppID() {
                return wxPayConfig.getAppId();
            }

            @Override
            public String getMchID() {
                return wxPayConfig.getMchId();
            }

            @Override
            public String getKey() {
                return wxPayConfig.getKey();
            }

            @Override
            public InputStream getCertStream() {
                return in;
            }

            @Override
            public int getHttpConnectTimeoutMs() {
                return wxPayConfig.getHttpConnectTimeoutMs();
            }

            @Override
            public int getHttpReadTimeoutMs() {
                return wxPayConfig.getHttpReadTimeoutMs();
            }
        });
    }

    public WxPayConfig getWxPayConfig() {
        return wxPayConfig;
    }

    public WXPay getWxPay() {
        return wxPay;
    }
}
