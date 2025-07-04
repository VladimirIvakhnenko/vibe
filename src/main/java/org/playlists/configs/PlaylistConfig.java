package org.playlists.configs;

import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.annotations.*;

@Configuration
public class PlaylistConfig {
    @Bean
    public CacheManager cacheManager() {
        return new CaffeineCacheManager("userTopLikes");
    }
}