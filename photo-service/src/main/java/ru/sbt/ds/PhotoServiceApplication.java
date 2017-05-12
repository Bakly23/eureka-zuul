package ru.sbt.ds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class PhotoServiceApplication {
	public static void main(String[] args) {
		new SpringApplicationBuilder(PhotoServiceApplication.class)
				.properties("picture.path=" + args[0]).run(args);
	}
}
