package ru.sbt.ds;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

@EnableFeignClients
@EnableEurekaClient
@SpringBootApplication
public class PhotoServiceClientApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(PhotoServiceClientApplication.class)
                .properties("app.version=" + args[0], "eureka.instance.version=" + args[0], "eureka.instance.metadataMap.version=" + args[0]).run(args);
    }
}
