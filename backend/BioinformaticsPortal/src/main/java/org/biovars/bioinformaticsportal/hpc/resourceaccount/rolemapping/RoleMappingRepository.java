package org.biovars.bioinformaticsportal.hpc.resourceaccount.rolemapping;

import org.biovars.bioinformaticsportal.hpc.resourceaccount.ResourceAccount;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class RoleMappingRepository {

    private final JdbcTemplate jdbcTemplate;

    public RoleMappingRepository(@Qualifier("portalJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    List<RoleAccounts> findAllRoleAccounts() {
        List<RoleMapping> roleMappings = findAllRoleMappings();
        var grouping = roleMappings.stream().collect(Collectors.groupingBy(RoleMapping::role));
        List<RoleAccounts> result = new ArrayList<>();
        for (var entry : grouping.entrySet()) {
            result.add(new RoleAccounts(entry.getKey(), entry.getValue()
                    .stream().map(e -> e.resourceAccount()).collect(Collectors.toList())
            ));
        }
        return result;
    }

    List<RoleAccounts> findAccountsByRole(String role) {
        List<RoleMapping> roleMappings = findRoleMappings(role);
        var grouping = roleMappings.stream().collect(Collectors.groupingBy(RoleMapping::role));
        List<RoleAccounts> result = new ArrayList<>();
        for (var entry : grouping.entrySet()) {
            result.add(new RoleAccounts(entry.getKey(), entry.getValue()
                    .stream().map(e -> e.resourceAccount()).collect(Collectors.toList())
            ));
        }
        return result;
    }

    List<RoleMapping> findRoleMappings(String role) {
        String sql = "SELECT resource_account_role_mapping.id AS id," +
                " resource_account_role_mapping.role AS role," +
                " resource_account_role_mapping.resource_account_id AS resource_account_id," +
                " resource_accounts.name AS name," +
                " resource_accounts.balance AS balance," +
                " resource_accounts.id AS res_acc_id" +
                " FROM resource_account_role_mapping" +
                " INNER JOIN resource_accounts ON " +
                "resource_accounts.id = resource_account_role_mapping.resource_account_id " +
                " WHERE resource_account_role_mapping.role = ?";
        return jdbcTemplate.query(sql, new RoleMappingDTORowMapper(), role);
    }

    List<RoleMapping> findAllRoleMappings() {
        String sql = "SELECT resource_account_role_mapping.id AS id," +
            " resource_account_role_mapping.role AS role," +
            " resource_account_role_mapping.resource_account_id AS resource_account_id," +
                " resource_accounts.name AS name," +
                " resource_accounts.balance AS balance," +
                " resource_accounts.id AS res_acc_id" +
                " FROM resource_account_role_mapping" +
                " INNER JOIN resource_accounts ON " +
                "resource_accounts.id = resource_account_role_mapping.resource_account_id";
        return jdbcTemplate.query(sql, new RoleMappingDTORowMapper());
    }

    void upsert(String role, long resource_account_id) {
        String sql = "INSERT INTO resource_account_role_mapping (role, resource_account_id) "+
                " VALUES (?, ?) ON CONFLICT (role, resource_account_id) DO NOTHING ";
        jdbcTemplate.update(sql, role, resource_account_id);
    }

    public void truncate() {
        jdbcTemplate.execute("TRUNCATE TABLE resource_account_role_mapping");
    }

    private static class RoleMappingDTORowMapper implements RowMapper<RoleMapping> {
        @Override
        public RoleMapping mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            long id = resultSet.getLong("id");
            String role = resultSet.getString("role");
            long resourceAccountId = resultSet.getLong("resource_account_id");
            String name = resultSet.getString("name");
            BigDecimal balance = resultSet.getBigDecimal("balance");
            return new RoleMapping(id, role, new ResourceAccount(resourceAccountId, name, balance));
        }
    }

    private static class RoleMappingRowMapper implements RowMapper<RoleAccIdMapping> {
        @Override
        public RoleAccIdMapping mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            long id = resultSet.getLong("record_id");
            String role = resultSet.getString("sample_id");
            long resourceAccountId = resultSet.getLong("resource_account_id");
            return new RoleAccIdMapping(id, role, resourceAccountId);
        }
    }
}
