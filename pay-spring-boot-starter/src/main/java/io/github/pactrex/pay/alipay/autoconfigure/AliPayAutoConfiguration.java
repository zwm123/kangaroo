package io.github.pactrex.pay.alipay.autoconfigure;

import io.github.pactrex.pay.alipay.AliPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AliPayConfig.class)
public class AliPayAutoConfiguration {

    @Autowired
    private AliPayConfig aliPayConfig;

    @Bean
    public AliPayService aliPayService() {
        return new AliPayService(aliPayConfig);
    }

}
