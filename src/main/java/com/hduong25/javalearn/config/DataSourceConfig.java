package com.hduong25.javalearn.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;

/**
 * Lớp cấu hình DataSource chính cho ứng dụng Spring Boot.
 *
 * <p>
 * Lớp này cấu hình các thành phần cần thiết để làm việc với cơ sở dữ liệu:
 * <ul>
 * <li><b>DataSource:</b> Tạo kết nối tới cơ sở dữ liệu.</li>
 * <li><b>EntityManagerFactory:</b> Quản lý các thực thể (Entity) trong
 * JPA.</li>
 * <li><b>PlatformTransactionManager:</b> Quản lý các giao dịch
 * (transaction).</li>
 * </ul>
 * </p>
 *
 * <p>
 * <b>Cách hoạt động:</b></p>
 * <ul>
 * <li>Spring Boot đọc cấu hình từ file {@code application.yml} hoặc
 * {@code application.properties}.</li>
 * <li>Tạo các bean DataSource, EntityManagerFactory và TransactionManager dựa
 * trên các thông tin đã cấu hình.</li>
 * <li>Sử dụng annotation {@link EnableJpaRepositories} để quét các repository
 * trong package {@code com.hduong25.javalearn} và kết nối với cấu hình
 * này.</li>
 * </ul>
 *
 * @author hduong25
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = DataSourceConfig.ENTITY_MANAGER_FACTORY_NAME,
        transactionManagerRef = DataSourceConfig.TRANSACTION_MANAGER_NAME,
        basePackages = {"com.hduong25.javalearn"})
public class DataSourceConfig {

    public static final String DATASOURCE_PREFIX = "spring.datasource";

    /// Tiền tố để đọc cấu hình cho DataSource từ file cấu hình.
    public static final String DATASOURCE_PROPERTIES_PREFIX = DATASOURCE_PREFIX + ".learn-java";

    /// Tiền tố để đọc cấu hình cho HikariCP (connection pool).
    public static final String DATASOURCE_PROPERTIES_HIKARI_PREFIX = DATASOURCE_PREFIX + ".hikari";

    /// Tên bean DataSourceProperties.
    public static final String DATASOURCE_PROPERTIES_NAME = "primaryProperties";

    /// Tên bean DataSource.
    public static final String DATASOURCE_NAME = "primaryDataSource";

    /// Tên bean EntityManagerFactory.
    public static final String ENTITY_MANAGER_FACTORY_NAME = "primaryEntityManagerFactory";

    /// Tên bean TransactionManager.
    public static final String TRANSACTION_MANAGER_NAME = "primaryTransactionManager";

    /// Tiền tố cấu hình cho JPA trong file cấu hình.
    public static final String JPA_PREFIX = "spring.jpa";

    /// Các package chứa các entity JPA.
    protected static final String[] ENTITY_PACKAGE = {"com.hduong25.javalearn"};

    /**
     * Tạo đối tượng {@link DataSourceProperties} từ file cấu hình của ứng dụng.
     *
     * <p>
     * Phương thức này ánh xạ các thuộc tính được cấu hình trong file cấu hình
     * (ví dụ: {@code application.yml} hoặc {@code application.properties}) vào
     * đối tượng {@link DataSourceProperties}. Các thuộc tính bao gồm thông tin
     * kết nối như: URL cơ sở dữ liệu, tên người dùng, mật khẩu, driver class,
     * v.v.</p>
     *
     * <p>
     * <b>Cách hoạt động:</b></p>
     * <ol>
     * <li>Annotation {@code @ConfigurationProperties} chỉ định tiền tố
     * {@code spring.datasource.learn-java} để ánh xạ các thuộc tính trong file
     * cấu hình.</li>
     * <li>Spring Boot sẽ tự động tạo và trả về một đối tượng
     * {@link DataSourceProperties} với các giá trị đã được ánh xạ.</li>
     * <li>Annotation {@code @Primary} được sử dụng để đánh dấu đây là cấu hình
     * chính mặc định.</li>
     * </ol>
     *
     * <p>
     * <b>Ví dụ cấu hình:</b></p>
     * <pre>
     * spring:
     *   datasource:
     *     learn-java:
     *       url: jdbc:mysql://localhost:3306/learn_java_db
     *       username: root
     *       password: secret
     *       driver-class-name: com.mysql.cj.jdbc.Driver
     * </pre>
     *
     * @return {@link DataSourceProperties} chứa thông tin cấu hình kết nối cơ
     * sở dữ liệu.
     */
    @Primary
    @Bean(name = DATASOURCE_PROPERTIES_NAME)
    @ConfigurationProperties(prefix = DATASOURCE_PROPERTIES_PREFIX)
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * Tạo đối tượng {@link DataSource} từ {@link DataSourceProperties}.
     *
     * <p>
     * Phương thức này khởi tạo một đối tượng {@link DataSource} dựa trên thông
     * tin cấu hình trong {@link DataSourceProperties}. Đây là nguồn kết nối cơ
     * sở dữ liệu thực tế được sử dụng trong ứng dụng.</p>
     *
     * <p>
     * <b>Cách hoạt động:</b></p>
     * <ol>
     * <li>Phương thức nhận {@link DataSourceProperties} đã được cấu hình.</li>
     * <li>Sử dụng phương thức {@code initializeDataSourceBuilder()} của
     * {@link DataSourceProperties} để khởi tạo một {@link DataSource} mới.</li>
     * <li>Trả về đối tượng {@link DataSource} đã được cấu hình.</li>
     * </ol>
     *
     * <p>
     * <b>Ví dụ cấu hình:</b></p>
     * <pre>
     * spring:
     *   datasource:
     *     learn-java:
     *       hikari:
     *         maximum-pool-size: 10
     *         minimum-idle: 5
     * </pre>
     *
     * @param properties {@link DataSourceProperties} chứa thông tin cấu hình
     * kết nối.
     * @return {@link DataSource} đối tượng kết nối tới cơ sở dữ liệu.
     */
    @Primary
    @ConfigurationProperties(prefix = DATASOURCE_PROPERTIES_HIKARI_PREFIX)
    @Bean(name = DATASOURCE_NAME)
    public DataSource dataSource(
            @Qualifier(DATASOURCE_PROPERTIES_NAME) DataSourceProperties properties
    ) {
        return properties.initializeDataSourceBuilder().build();
    }

    /**
     * Tạo bean {@link LocalContainerEntityManagerFactoryBean} để quản lý các
     * thực thể JPA.
     *
     * <p>
     * Phương thức này khởi tạo một
     * {@link LocalContainerEntityManagerFactoryBean} để cung cấp chức năng quản
     * lý các thực thể JPA trong ứng dụng.</p>
     *
     * <p>
     * <b>Cách hoạt động:</b></p>
     * <ol>
     * <li>Nhận đối tượng {@link DataSource} làm nguồn kết nối cơ sở dữ
     * liệu.</li>
     * <li>Nhận {@link EntityManagerFactoryBuilder} để cấu hình factory.</li>
     * <li>Sử dụng phương thức {@code packages()} để chỉ định các package chứa
     * các entity.</li>
     * <li>Trả về một {@link LocalContainerEntityManagerFactoryBean} đã được cấu
     * hình.</li>
     * </ol>
     *
     * <p>
     * <b>Ví dụ:</b></p>
     * <ul>
     * <li>Package entity: {@code com.hduong25.javalearn}.</li>
     * <li>Persistence unit name: {@code primaryDataSource}.</li>
     * </ul>
     *
     * @param builder {@link EntityManagerFactoryBuilder} đối tượng hỗ trợ xây
     * dựng factory.
     * @param dataSource {@link DataSource} để kết nối tới cơ sở dữ liệu.
     * @return {@link LocalContainerEntityManagerFactoryBean} đã được cấu hình.
     */
    @Primary
    @Bean(name = ENTITY_MANAGER_FACTORY_NAME)
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(
            EntityManagerFactoryBuilder builder,
            @Qualifier(DATASOURCE_NAME) DataSource dataSource
    ) {
        return builder.dataSource(dataSource)
                .packages(ENTITY_PACKAGE)
                .persistenceUnit(DATASOURCE_NAME)
                .build();
    }

    /**
     * Tạo bean {@link PlatformTransactionManager} để quản lý các giao dịch
     * trong JPA.
     *
     * <p>
     * Phương thức này khởi tạo một {@link PlatformTransactionManager} để xử lý
     * và quản lý các giao dịch (transaction) liên quan tới JPA.</p>
     *
     * <p>
     * <b>Cách hoạt động:</b></p>
     * <ol>
     * <li>Nhận {@link EntityManagerFactory} từ phương thức cấu hình trước
     * đó.</li>
     * <li>Sử dụng {@link JpaTransactionManager} để tạo TransactionManager.</li>
     * </ol>
     *
     * <p>
     * <b>Ví dụ:</b></p>
     * <ul>
     * <li>Giao dịch đọc dữ liệu: Tự động commit sau khi hoàn thành.</li>
     * <li>Giao dịch ghi dữ liệu: Rollback nếu xảy ra lỗi.</li>
     * </ul>
     *
     * @param entityManagerFactory {@link EntityManagerFactory} để xử lý các
     * thực thể.
     * @return {@link PlatformTransactionManager} đối tượng quản lý giao dịch.
     */
    @Primary
    @Bean(name = TRANSACTION_MANAGER_NAME)
    @ConfigurationProperties(JPA_PREFIX)
    public PlatformTransactionManager platformTransactionManager(
            @Qualifier(ENTITY_MANAGER_FACTORY_NAME) EntityManagerFactory entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
