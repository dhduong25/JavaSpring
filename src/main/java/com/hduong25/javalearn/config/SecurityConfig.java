package com.hduong25.javalearn.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author: hduong25
 * Lớp cấu hình bảo mật (Spring Security) dùng để thiết lập các cấu hình bảo mật cho ứng dụng.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
public class SecurityConfig {

    private final CustomCorsConfig customCorsConfigCustom;

    @Value("${spring.security.user.name}")
    private String defaultUserName;

    @Value("${spring.security.user.password}")
    private String defaultPassword;

    /**
     * Constructor để inject dependency.
     *
     * @param corsConfig cấu hình CORS tùy chỉnh
     */
    public SecurityConfig(CustomCorsConfig corsConfig) {
        customCorsConfigCustom = corsConfig;
    }

    /**
     * Định nghĩa bean SecurityFilterChain để thiết lập các cấu hình bảo mật.
     *
     * @param httpSecurity đối tượng HttpSecurity để cấu hình bảo mật
     * @return một SecurityFilterChain đã được cấu hình đầy đủ
     * @throws Exception nếu có lỗi xảy ra trong quá trình cấu hình
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        // Bỏ kích hoạt CSRF (Cross-Site Request Forgery)
        // Thường được sử dụng khi xây dựng các API không trạng thái (stateless)
        // hoặc khi CSRF được quản lý theo cách tùy chỉnh.

        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                // Cấu hình CORS bằng cách sử dụng cấu hình CORS tùy chỉnh
                .cors(cors -> cors.configurationSource(customCorsConfigCustom))
                .formLogin(AbstractHttpConfigurer::disable)
                // Cấu hình các quy tắc ủy quyền
                .authorizeHttpRequests(req ->
                        // Cho phép tất cả các yêu cầu được truy cập mà không cần xác thực
                        // Thay permitAll() -> authenticated() để yêu cầu xác thực
                        // req.anyRequest().permitAll()

                        req.requestMatchers(
                                        "ping",
                                        "/auth/login",
                                        "/auth/register"
                                ).permitAll()
                                .anyRequest().authenticated()
                );

        // Xây dựng và trả về SecurityFilterChain đã được cấu hình
        return httpSecurity.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User
                .withUsername(defaultUserName)
                .password(passwordEncoder().encode(defaultPassword))
                .authorities("auth_read", "auth_write")
                .build();

        return new InMemoryUserDetailsManager(admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
