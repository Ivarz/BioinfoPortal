package org.biovars.bioinformaticsportal.hpc.resourceaccount;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ResourceAccountRepository {
    JdbcTemplate jdbcTemplate;
    ResourceAccountRepository(@Qualifier("portalJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    void truncate() {
        jdbcTemplate.execute("TRUNCATE TABLE resource_accounts");
    }
    void insert(String name, BigDecimal balance) {
        String sql = "INSERT INTO resource_accounts (name, balance) VALUES (?, ?)";
        jdbcTemplate.update(sql, name, balance);
    }
    void upsert(String name, BigDecimal balance) {
        String sql = "INSERT INTO resource_accounts (name, balance, active) VALUES (?, ?, ?) ON CONFLICT (name) DO UPDATE SET balance = EXCLUDED.balance, active = EXCLUDED.active";
        jdbcTemplate.update(sql, name, balance, true);
    }

    List<ResourceAccount> findAll() {
        return jdbcTemplate.query("SELECT * FROM resource_accounts", new ResourceAccountRowMapper());
    }

    List<ResourceAccount> findActive() {
        return jdbcTemplate.query("SELECT * FROM resource_accounts WHERE active = TRUE", new ResourceAccountRowMapper());
    }


    long getIdByName(String name) {
        String sql = "SELECT id FROM resource_accounts WHERE name = ? LIMIT 1";
        return jdbcTemplate.queryForObject(sql, Long.class, name);
    }

    ResourceAccount findByName(String name) {
        String sql = "SELECT * FROM resource_accounts WHERE name = ? LIMIT 1";
        return jdbcTemplate.query(sql, new ResourceAccountRowMapper(), name).get(0);
    }

    void deactivateAll() {
        String sql = "UPDATE resource_accounts SET active = false";
        jdbcTemplate.update(sql);
    }

    private static class ResourceAccountRowMapper implements RowMapper<ResourceAccount> {
        @Override
        public ResourceAccount mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            long id = resultSet.getLong("id");
            String name = resultSet.getString("name");
            BigDecimal balance = resultSet.getBigDecimal("balance");
            return new ResourceAccount(id, name, balance);
        }
    }
}
