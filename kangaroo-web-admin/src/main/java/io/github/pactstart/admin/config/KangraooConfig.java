package io.github.pactstart.admin.config;

import io.github.pactstart.admin.adpater.KangarooWebAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KangraooConfig {

    @ConditionalOnMissingBean(KangarooWebAdapter.class)
    @Bean
    public KangarooWebAdapter kangarooWebAdapter() {
        return new KangarooWebAdapter();
    }
}
