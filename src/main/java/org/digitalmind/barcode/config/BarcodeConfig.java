package org.digitalmind.barcode.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = BarcodeModuleConfig.ENABLED, havingValue = "true")
@ConfigurationProperties(prefix = BarcodeModuleConfig.PREFIX)
@EnableConfigurationProperties
@Getter
@Setter
public class BarcodeConfig {
    private boolean enabled;
    private BarcodeConfigProperties config;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class BarcodeConfigProperties {
        int logoSizeFactor;
    }
}
