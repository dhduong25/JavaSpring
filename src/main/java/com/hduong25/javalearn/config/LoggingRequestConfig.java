package com.hduong25.javalearn.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
class LoggingProperties {
    private boolean enabled = true;
    private int maxBodyLength = 1000;
    private List<String> maskedFields = Arrays.asList("password", "token", "secret");
    private String logLevel = "DEBUG";
}

/**
 * LoggingRequestConfig
 * <p>
 * Class cấu hình ghi log request/response tự động cho các REST API endpoints
 * sử dụng Spring AOP (Aspect Oriented Programming).
 * <p>
 * Tính năng chính:
 * <ul>
 *   <li>Ghi log tự động cho request/response</li>
 *   <li>Mask dữ liệu nhạy cảm</li>
 *   <li>Cấu hình linh hoạt thông qua properties</li>
 *   <li>Structured logging với JSON format</li>
 *   <li>MDC context cho distributed tracing</li>
 * </ul>
 * <p>
 * Các annotation được sử dụng:
 * <ul>
 *   <li>{@code @Slf4j} - Tự động tạo logger từ Lombok</li>
 *   <li>{@code @Aspect} - Đánh dấu class là một aspect trong AOP</li>
 *   <li>{@code @Component} - Đăng ký bean với Spring container</li>
 *   <li>{@code @RequiredArgsConstructor} - Tự động tạo constructor với final fields</li>
 * </ul>
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingRequestConfig {

    private final ObjectMapper mapper;
    private final LoggingProperties loggingProperties;

    /**
     * Định nghĩa pointcut cho AOP.
     * <p>
     * Chỉ áp dụng cho các class được đánh dấu {@code @RestController}.
     */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restControllerPointcut() {
    }

    /**
     * Method chính xử lý logging thông qua AOP.
     * <p>
     * Quy trình xử lý:
     * <ol>
     *   <li>Kiểm tra logging có được enable</li>
     *   <li>Setup MDC context</li>
     *   <li>Log request</li>
     *   <li>Xử lý request và log response</li>
     *   <li>Cleanup MDC context</li>
     * </ol>
     *
     * @param joinPoint đối tượng chứa thông tin method được gọi
     * @return kết quả từ controller method
     * @throws Throwable nếu có lỗi xảy ra
     */
    @Around("restControllerPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!loggingProperties.isEnabled()) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String requestId = UUID.randomUUID().toString();

        try {
            setupMDC(requestId, request);
            logRequest(requestId, request);
            return processRequest(joinPoint, requestId);
        } finally {
            MDC.clear();
        }
    }

    /**
     * Setup MDC context với thông tin request.
     * <p>
     * Thêm các thông tin:
     * <ul>
     *   <li>Request ID</li>
     *   <li>HTTP Method</li>
     *   <li>Request Path</li>
     *   <li>Client IP</li>
     * </ul>
     */
    private void setupMDC(String requestId, HttpServletRequest request) {
        MDC.put("requestId", requestId);
        MDC.put("method", request.getMethod());
        MDC.put("path", request.getRequestURI());
        MDC.put("clientIp", request.getRemoteAddr());
    }

    /**
     * Ghi log thông tin request.
     * <p>
     * Các thông tin được log:
     * <ul>
     *   <li>Request ID</li>
     *   <li>HTTP Method</li>
     *   <li>URI</li>
     *   <li>Timestamp</li>
     *   <li>Headers (đã mask dữ liệu nhạy cảm)</li>
     *   <li>Parameters</li>
     *   <li>Body (đã truncate nếu quá dài)</li>
     * </ul>
     */
    private void logRequest(String requestId, HttpServletRequest request) {
        try {
            Map<String, Object> logData = new HashMap<>();
            logData.put("type", "REQUEST");
            logData.put("id", requestId);
            logData.put("method", request.getMethod());
            logData.put("uri", request.getRequestURI());
            logData.put("timestamp", LocalDateTime.now());
            logData.put("headers", getHeaders(request));
            logData.put("parameters", maskSensitiveData(request.getParameterMap()));
            logData.put("body", truncateAndMaskBody(getRequestBody(request)));

            log.info("REQUEST [{}] {} {} \n- Time: {} \n- Headers: {} \n- Parameters: {} \n- Body: {}",
                    requestId,
                    request.getMethod(),
                    request.getRequestURI(),
                    logData.get("timestamp"),
                    mapper.writeValueAsString(logData.get("headers")),
                    mapper.writeValueAsString(logData.get("parameters")),
                    logData.get("body")
            );
        } catch (Exception e) {
            log.error("Error logging request", e);
        }
    }

    /**
     * Xử lý request và log response.
     * <p>
     * Quy trình:
     * <ol>
     *   <li>Bắt đầu timing</li>
     *   <li>Thực thi controller method</li>
     *   <li>Log response success/error</li>
     * </ol>
     */
    private Object processRequest(ProceedingJoinPoint joinPoint, String requestId) throws Throwable {
        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            logResponse(requestId, result, startTime, null);
            return result;
        } catch (Exception e) {
            logResponse(requestId, null, startTime, e);
            throw e;
        }
    }

    /**
     * Ghi log response.
     * <p>
     * Log các thông tin:
     * <ul>
     *   <li>Request ID</li>
     *   <li>Thời gian xử lý</li>
     *   <li>Response body hoặc error message</li>
     * </ul>
     */
    private void logResponse(String requestId, Object result, long startTime, Exception error) {
        try {
            long duration = System.currentTimeMillis() - startTime;
            if (error == null) {
                String response = result != null ? truncateAndMaskBody(mapper.writeValueAsString(result)) : "null";
                log.info("RESPONSE [{}] ({} ms): {}", requestId, duration, response);
            } else {
                log.error("ERROR RESPONSE [{}] ({} ms): {}", requestId, duration, error.getMessage());
            }
        } catch (Exception e) {
            log.error("Error logging response", e);
        }
    }

    /**
     * Lấy và mask headers từ request.
     * <p>
     * Headers nhạy cảm được mask theo cấu hình maskedFields.
     */
    private Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Collections.list(request.getHeaderNames())
                .forEach(name -> headers.put(name, maskHeaderIfSensitive(name, request.getHeader(name))));
        return headers;
    }

    /**
     * Mask giá trị header nếu tên header chứa từ khóa nhạy cảm.
     */
    private String maskHeaderIfSensitive(String name, String value) {
        return loggingProperties.getMaskedFields().stream()
                .anyMatch(field -> name.toLowerCase().contains(field)) ? "******" : value;
    }

    /**
     * Đọc body từ request.
     */
    private String getRequestBody(HttpServletRequest request) {
        try {
            return request.getReader().lines()
                    .collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            log.error("Error reading body", e);
            return "";
        }
    }

    /**
     * Truncate và mask dữ liệu trong body.
     * <p>
     * - Truncate nếu độ dài vượt quá maxBodyLength
     * - Mask các trường nhạy cảm
     */
    private String truncateAndMaskBody(String content) {
        if (!StringUtils.hasText(content)) {
            return content;
        }

        String maskedContent = maskSensitiveDataInString(content);
        if (maskedContent.length() > loggingProperties.getMaxBodyLength()) {
            return maskedContent.substring(0, loggingProperties.getMaxBodyLength()) + "... (truncated)";
        }
        return maskedContent;
    }

    /**
     * Mask dữ liệu nhạy cảm trong string.
     * <p>
     * Sử dụng regex để tìm và thay thế các trường nhạy cảm.
     */
    private String maskSensitiveDataInString(String content) {
        String maskedContent = content;
        for (String field : loggingProperties.getMaskedFields()) {
            maskedContent = maskedContent.replaceAll(
                    String.format("(\"%s\"\\s*:\\s*\")(.*?)(\")", field),
                    "$1******$3"
            );
        }
        return maskedContent;
    }

    /**
     * Mask dữ liệu nhạy cảm trong Map.
     * <p>
     * Xử lý đệ quy nếu value là Map con.
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> maskSensitiveData(Map<String, ?> data) {
        Map<String, Object> maskedData = new HashMap<>();
        data.forEach((key, value) -> {
            if (loggingProperties.getMaskedFields().contains(key.toLowerCase())) {
                maskedData.put(key, "******");
            } else if (value instanceof Map) {
                maskedData.put(key, maskSensitiveData((Map<String, ?>) value));
            } else {
                maskedData.put(key, value);
            }
        });
        return maskedData;
    }
}