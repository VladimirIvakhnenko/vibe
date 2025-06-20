package org.audio.configs;

import org.audio.db.FingerprintDatabase;
import org.audio.db.InMemoryFingerprintDatabase;
import org.audio.services.AudioMatchingService;
import org.audio.services.FingerprintService;
import org.audio.services.FingerprintServiceBase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AudioConfig {

    @Bean
    public FingerprintDatabase fingerprintDatabase() {
        return new InMemoryFingerprintDatabase();
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
}