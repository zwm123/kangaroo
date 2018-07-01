package io.github.pactstart.admin;

import io.github.pactstart.jpush.autoconfigure.EnableJPush;
import io.github.pactstart.redismq.autoconfigure.consumer.EnableRedisMQConsumer;
import io.github.pactstart.redismq.autoconfigure.producer.EnableRedisMQProducer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@EnableRedisMQConsumer
@EnableRedisMQProducer
@EnableJPush
@SpringBootApplication
@ComponentScan(basePackages = {"io.github.pactstart"})
@MapperScan(basePackages = {"io.github.pactstart.system.dao"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
