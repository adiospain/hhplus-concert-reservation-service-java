package io.hhplus.concert_reservation_service_java.core.configuration.jpa;

import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@Configuration
public class JpaConfig {

  @Bean
  public DataSource dataSource() {
    return DataSourceBuilder.create()
        .url("jdbc:h2:mem:test")
        .driverClassName("org.h2.Driver")
        .username("sa")
        .password("")
        .build();
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
    LocalContainerEntityManagerFactoryBean emfb = new LocalContainerEntityManagerFactoryBean();
    emfb.setDataSource(dataSource);
    emfb.setPackagesToScan("io.hhplus.concert_reservation_service_java.domain");
    emfb.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

    return emfb;
  }
}
