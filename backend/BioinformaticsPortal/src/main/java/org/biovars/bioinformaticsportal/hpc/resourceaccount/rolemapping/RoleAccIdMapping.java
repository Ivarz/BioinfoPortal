package org.biovars.bioinformaticsportal.hpc.resourceaccount.rolemapping;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("RESOURCE_ACCOUNT_ROLE_MAPPING")
public record RoleAccIdMapping(
    @Id
    long id,
    String role,
    long resourceAccountId
) {}
