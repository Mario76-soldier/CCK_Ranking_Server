package com.cubingclub.ranking.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Value("${api.prefix}")
    private String apiPrefix;

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // @RestController 어노테이션이 있는 모든 컨트롤러에 /api prefix 추가
        configurer.addPathPrefix(apiPrefix, c -> c.isAnnotationPresent(RestController.class));
    }
}
