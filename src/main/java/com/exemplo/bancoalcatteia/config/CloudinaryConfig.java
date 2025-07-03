package com.exemplo.bancoalcatteia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;
import com.cloudinary.Cloudinary;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary(){
        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name", "dfykqixct");
        config.put("api_key", "982797947962238");
        config.put("api_secret", "Yz9o2Md73F450i5LXO-X2cvlNe0");
        return new Cloudinary(config);
    }
}
