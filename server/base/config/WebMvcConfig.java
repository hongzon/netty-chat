package com.nettychat.server.base.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * MVC 配置
 *
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${storage.path}")
    private String storagePath;

    /**
     * 配置静态资源访问
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/img/**")
                .addResourceLocations("file:" + storagePath);
    }
}
