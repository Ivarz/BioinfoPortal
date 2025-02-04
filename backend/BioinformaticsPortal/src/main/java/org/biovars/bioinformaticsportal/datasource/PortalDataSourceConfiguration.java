package org.biovars.bioinformaticsportal.datasource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class PortalDataSourceConfiguration {
    @Bean(name="portalProperties")
    @Primary
    @ConfigurationProperties("spring.datasource.portal")
    public DataSourceProperties portalDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name="portalDataSource")
    @Primary
    public DataSource portalDataSource(@Qualifier("portalProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    @Bean
    @Primary
    public JdbcTemplate portalJdbcTemplate(@Qualifier("portalDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
