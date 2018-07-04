package io.github.pactrex.pay.wxpay.autoconfigure;

import io.github.pactrex.pay.wxpay.WxPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * https://github.com/wxpay/WXPay-SDK-Java/tree/d7ecb4020780b676953ed7de58ef807bd871023f
 */
@Configuration
@EnableConfigurationProperties(WxPayConfig.class)
public class WxPayAutoConfiguration {

    @Autowired
    private WxPayConfig wxPayConfig;

    @Bean
    public WxPayService wxPayService() throws Exception {
        return new WxPayService(wxPayConfig);
    }
}
