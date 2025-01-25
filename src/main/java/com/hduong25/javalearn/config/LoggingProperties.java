package com.hduong25.javalearn.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Lớp cấu hình properties cho logging request/response
 * <p>
 * Chứa các thuộc tính có thể cấu hình thông qua application.properties/yaml:
 * <ul>
 *   <li>enabled: Bật/tắt logging</li>
 *   <li>maxBodyLength: Độ dài tối đa của body được log</li>
 *   <li>maskedFields: Danh sách các trường cần mask</li>
 *   <li>logLevel: Level log (INFO, DEBUG, etc)</li>
 * </ul>
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "logging.request")
public class LoggingProperties {
    private boolean enabled = true;
    private int maxBodyLength = 1000;
    private List<String> maskedFields = Arrays.asList("password", "token", "secret");
    private String logLevel = "DEBUG";
}