package org.biovars.bioinformaticsportal.hpc.resourceaccount.rolemapping;

import org.biovars.bioinformaticsportal.hpc.resourceaccount.ResourceAccount;

import java.util.List;

public record RoleAccounts(
    String role,
    List<ResourceAccount> resourceAccounts
) { }
