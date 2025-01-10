package com.hduong25.javalearn.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * LoggingRequestConfig
 * <p>
 * Class cấu hình ghi log request/response tự động cho các REST API endpoints
 * sử dụng Spring AOP (Aspect Oriented Programming).
 * <p>
 * Các annotation được sử dụng:
 * <ul>
 *     <li>{@code @Slf4j} - Tự động tạo logger từ Lombok.</li>
 *     <li>{@code @Aspect} - Đánh dấu class này là một aspect trong AOP.</li>
 *     <li>{@code @Component} - Đăng ký bean với Spring container.</li>
 *     <li>{@code @RequiredArgsConstructor} - Tự động tạo constructor với các field {@code final}.</li>
 * </ul>
 *
 * @author hduong25
 */

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingRequestConfig {

    /**
     * ObjectMapper dùng để serialize/deserialize JSON.
     * <ul>
     *     <li>Chuyển đổi object Java thành JSON string.</li>
     *     <li>Chuyển đổi JSON string thành object Java.</li>
     *     <li>Được sử dụng để format parameters và response body.</li>
     * </ul>
     */
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Định nghĩa pointcut cho AOP.
     * <p>
     * Pointcut chỉ áp dụng cho các class được đánh dấu {@code @RestController}.
     * Sử dụng bởi method {@link #logAround(ProceedingJoinPoint)} thông qua annotation {@code @Around}.
     */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restControllerPointcut() {
    }

    /**
     * Method chính xử lý logging thông qua AOP.
     * <p>
     * Quy trình xử lý:
     * <ol>
     *     <li>Lấy thông tin request từ {@code RequestContextHolder}.</li>
     *     <li>Tạo unique request ID để theo dõi request/response.</li>
     *     <li>Thu thập và format thông tin request:
     *         <ul>
     *             <li>Request ID</li>
     *             <li>HTTP Method (GET, POST, etc)</li>
     *             <li>URI path</li>
     *             <li>Timestamp request</li>
     *             <li>Headers (content-type, user-agent, etc)</li>
     *             <li>Query parameters</li>
     *             <li>Request body</li>
     *         </ul>
     *     </li>
     *     <li>Ghi log request.</li>
     *     <li>Đo thời gian xử lý.</li>
     *     <li>Thực thi controller method gốc.</li>
     *     <li>Ghi log response kèm thời gian xử lý.</li>
     *     <li>Xóa log context.</li>
     * </ol>
     *
     * @param joinPoint đối tượng chứa thông tin và method được gọi.
     * @return kết quả từ controller method.
     * @throws Throwable nếu có lỗi xảy ra trong quá trình xử lý.
     */
    @Around("restControllerPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // Lấy request từ Spring context
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        // Tạo ID định danh duy nhất cho request này
        String requestId = UUID.randomUUID().toString();

        // Tạo chuỗi log với các thông tin request
        String requestLog = "REQUEST [" + requestId + "] " +
                            request.getMethod() + " " +  // HTTP method
                            request.getRequestURI() + "\n" +  // URI path
                            "- Time request: " + LocalDateTime.now() + "\n" + // Thời gian
                            "- Headers: " + this.getHeaders(request) + "\n" + // Headers
                            "- Parameters: " + this.mapper.writeValueAsString(request.getParameterMap()) + "\n" + // Query params
                            "- Body: " + this.getRequestBody(request); // Body content

        // Ghi log request
        log.info(requestLog);

        // Bắt đầu tính thời gian xử lý
        long startTime = System.currentTimeMillis();
        Object result = null;

        try {
            // Thực thi method thật của controller
            result = joinPoint.proceed();
            return result;
        } finally {
            // Ghi log response kèm thời gian xử lý
            log.info("RESPONSE [{}] ({} ms): {}",
                    requestId,
                    System.currentTimeMillis() - startTime,
                    this.mapper.writeValueAsString(result)
            );

            // Xóa context logging
            MDC.clear();
        }
    }

    /**
     * Lấy tất cả headers từ HTTP request.
     * <p>
     * Quy trình:
     * <ol>
     *     <li>Tạo Map rỗng để chứa headers.</li>
     *     <li>Lấy danh sách tên của các headers.</li>
     *     <li>Với mỗi header name, lấy giá trị và thêm vào Map.</li>
     * </ol>
     *
     * @param request đối tượng {@link HttpServletRequest}.
     * @return {@code Map<String, String>} chứa cặp key-value của headers.
     */
    private Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Collections.list(request.getHeaderNames())
                .forEach(name -> headers.put(name, request.getHeader(name)));
        return headers;
    }

    /**
     * Đọc và trả về body content từ request.
     * <p>
     * Quy trình:
     * <ol>
     *     <li>Lấy reader từ request.</li>
     *     <li>Đọc tất cả các dòng.</li>
     *     <li>Gộp thành một chuỗi sử dụng system line separator.</li>
     *     <li>Xử lý exception nếu có lỗi đọc.</li>
     * </ol>
     *
     * @param request đối tượng {@link HttpServletRequest}.
     * @return {@code String} nội dung của request body.
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
}
