package com.cubingclub.ranking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class RankingApplication {
	public static void main(String[] args) {
		SpringApplication.run(RankingApplication.class, args);
	}
}
