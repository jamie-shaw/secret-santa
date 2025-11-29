package com.secretsanta.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

/**
 * Configuration for serving the Angular application
 */
@Configuration
public class AngularWebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve Angular app static resources from /app/**
        registry.addResourceHandler("/app/**")
                .addResourceLocations("classpath:/static/app/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource requestedResource = location.createRelative(resourcePath);
                        
                        // If the resource exists, return it
                        if (requestedResource.exists() && requestedResource.isReadable()) {
                            return requestedResource;
                        }
                        
                        // Otherwise, return index.html for Angular routing
                        // This allows Angular to handle client-side routes
                        return new ClassPathResource("/static/app/index.html");
                    }
                });
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Forward /app and /app/ to /app/index.html
        registry.addViewController("/app").setViewName("forward:/app/index.html");
        registry.addViewController("/app/").setViewName("forward:/app/index.html");
    }
}
