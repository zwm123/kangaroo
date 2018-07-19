package io.github.pactstart.sms.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OldSmsConfig.class)
public class OldSmsAutoConfiguration {

    @Autowired
    private OldSmsConfig oldSmsConfig;

    @Bean
    public OldSmsClient smsClient() {
        return new OldSmsClient(oldSmsConfig);
    }
}
