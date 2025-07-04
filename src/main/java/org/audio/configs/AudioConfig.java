package org.audio.configs;

import jakarta.servlet.MultipartConfigElement;
import org.audio.db.FingerprintDatabase;
import org.audio.db.InMemoryFingerprintDatabase;
import org.audio.db.repo.FingerprintRepository;
import org.audio.db.repo.SongRepository;
import org.audio.services.AudioMatchingService;
import org.audio.services.FingerprintService;
import org.audio.services.FingerprintServiceBase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.util.unit.DataSize;
import io.swagger.v3.oas.annotations.*;


@Configuration
public class AudioConfig {

    @Bean
    public FingerprintDatabase fingerprintDatabase(SongRepository songRepository, FingerprintRepository fingerprintRepository) {
        return new InMemoryFingerprintDatabase(songRepository, fingerprintRepository);
    }

    @Bean
    public FingerprintService fingerprintService() {
        return new FingerprintService();
    }

    @Bean
    public AudioMatchingService audioMatchingService(
            FingerprintDatabase fingerprintDatabase,
            FingerprintServiceBase fingerprintService) {
        return new AudioMatchingService(fingerprintDatabase, fingerprintService);
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofMegabytes(512));
        factory.setMaxRequestSize(DataSize.ofMegabytes(512));
        return factory.createMultipartConfig();
    }
}