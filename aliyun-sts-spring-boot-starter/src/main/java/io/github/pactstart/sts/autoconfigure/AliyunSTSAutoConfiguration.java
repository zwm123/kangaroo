package io.github.pactstart.sts.autoconfigure;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(StsConfig.class)
public class AliyunSTSAutoConfiguration implements InitializingBean, ApplicationContextAware {

    @Autowired
    private StsConfig stsConfig;
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, ProductConfig> productConfigMap = new HashMap<>();
        productConfigMap.put("oss", stsConfig.getOss());
        registerSTSClientBeanDefinition(productConfigMap);
    }

    private void registerSTSClientBeanDefinition(Map<String, ProductConfig> productConfigMap) throws Exception {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) ((ConfigurableApplicationContext) this.applicationContext).getBeanFactory();
        for (Map.Entry<String, ProductConfig> entry : productConfigMap.entrySet()) {
            String beanName = "STS_".concat(entry.getKey());
            //后期动态注册的Spring Bean不能直接注入，应当取得ApplicationContext对象再通过getBean()方式获得
            if (!beanFactory.containsBean("STS_".concat(entry.getKey()))) {
                BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(StsClient.class);
                builder.setScope("singleton");
                builder.setLazyInit(false);

                ProductConfig productConfig = entry.getValue();

                String policy = readToString(productConfig.getPolicyFile());

                builder.getBeanDefinition().getPropertyValues().addPropertyValue("policy", policy);
                builder.getBeanDefinition().getPropertyValues().addPropertyValue("productConfig", productConfig);
                beanFactory.registerBeanDefinition(beanName, builder.getBeanDefinition());
            }
        }
    }

    private String readToString(String policyFile) throws Exception {
        BufferedReader reader = null;
        //返回值,使用StringBuffer
        StringBuffer data = new StringBuffer();
        //
        try {
            reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(policyFile)));
            //每次读取文件的缓存
            String temp = null;
            while ((temp = reader.readLine()) != null) {
                data.append(temp);
            }
        } finally {
            //关闭文件流
            if (reader != null) {
                reader.close();
            }
        }
        return data.toString();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
