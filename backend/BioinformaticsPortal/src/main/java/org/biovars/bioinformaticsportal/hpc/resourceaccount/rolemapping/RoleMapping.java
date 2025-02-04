package org.biovars.bioinformaticsportal.hpc.resourceaccount.rolemapping;

import org.biovars.bioinformaticsportal.hpc.resourceaccount.ResourceAccount;

public record RoleMapping(
    long id,
    String role,
    ResourceAccount resourceAccount
) {}