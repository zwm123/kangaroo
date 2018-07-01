package io.github.pactstart.admin.apidoc;

import io.github.pactstart.simple.web.framework.config.SwaggerDocConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableConfigurationProperties(SwaggerDocConfig.class)
@EnableSwagger2
public class Swagger2Config {

    @Autowired
    private SwaggerDocConfig swaggerDocConfig;

    @Bean
    public Docket systemApiDoc() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("系统API接口文档")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("io.github.pactstart.admin.system.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(swaggerDocConfig.getTitle())
                .description(swaggerDocConfig.getDescription())
                .termsOfServiceUrl(swaggerDocConfig.getTermsOfServiceUrl())
                .license(swaggerDocConfig.getLicense())
                .licenseUrl(swaggerDocConfig.getLicenseUrl())
                .contact(new Contact(swaggerDocConfig.getName(), swaggerDocConfig.getUrl(), swaggerDocConfig.getEmail()))
                .version(swaggerDocConfig.getVersion())
                .build();
    }

}
