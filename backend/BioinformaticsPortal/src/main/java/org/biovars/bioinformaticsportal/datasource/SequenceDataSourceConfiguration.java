package org.biovars.bioinformaticsportal.datasource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class SequenceDataSourceConfiguration {
    @ConfigurationProperties("spring.datasource.sequence")
    @Bean(name="sequenceProperties")
    public DataSourceProperties sequenceDataSourceProperties() {
        return new DataSourceProperties();
    }

//    @ConfigurationProperties(prefix="spring.datasource.sequence")
    @Bean(name="sequenceDataSource")
    public DataSource sequenceDataSource(@Qualifier("sequenceProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    @Bean
    public JdbcTemplate sequenceJdbcTemplate(@Qualifier("sequenceDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
