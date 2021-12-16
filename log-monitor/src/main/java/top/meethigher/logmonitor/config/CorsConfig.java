/**
 * Copyright (C) 2020 THL A29 Limited, a Tencent company. All rights reserved.
 */

package top.meethigher.logmonitor.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域处理,只有再测试阶段生效
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * 正式环境应该根据服务器IP进行配置
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .maxAge(3600);
    }
}
