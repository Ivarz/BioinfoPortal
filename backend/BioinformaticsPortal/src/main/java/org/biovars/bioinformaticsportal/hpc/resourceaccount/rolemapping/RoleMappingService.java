package org.biovars.bioinformaticsportal.hpc.resourceaccount.rolemapping;

import org.biovars.bioinformaticsportal.hpc.resourceaccount.KeycloakService;
import org.biovars.bioinformaticsportal.hpc.resourceaccount.ResourceAccount;
import org.biovars.bioinformaticsportal.hpc.resourceaccount.ResourceAccountService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoleMappingService {
    private final RoleMappingRepository repo;
    private final ResourceAccountService resourceAccountService;
    private final KeycloakService keycloakService;
    public RoleMappingService(RoleMappingRepository repo,
                              ResourceAccountService resourceAccountService,
                              KeycloakService keycloakService) {
        this.repo = repo;
        this.resourceAccountService = resourceAccountService;
        this.keycloakService = keycloakService;
    }
    List<RoleMapping> findAllRoleMappings() {
        return repo.findAllRoleMappings();
    }
    List<RoleAccounts> findAllRoleAccounts() {
        return repo.findAllRoleAccounts();
    }

   public List<ResourceAccount> findAllowedResourceAccounts(List<String> roles) {
        Set<ResourceAccount> accountSet = new HashSet();
        for (String role : roles) {
            List<RoleAccounts> roleMap = repo.findAccountsByRole(role);
            for (var rm: roleMap) {
                accountSet.addAll(rm.resourceAccounts());
            }
        }
        return accountSet.stream().toList();
    }
    public List<ResourceAccount> findAllResourceAccounts() {
        Set<ResourceAccount> accountSet = new HashSet();
        List<RoleAccounts> roleMap = repo.findAllRoleAccounts();
        for (var rm: roleMap) {
            accountSet.addAll(rm.resourceAccounts());
        }
        return accountSet.stream().toList();
    }

    List<ResourceAccount> findAllowed() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        var roles = auth.getAuthorities()
                .stream()
                .map(r -> r.getAuthority()
                        .toString()
                        .replace("ROLE_", ""))
                .collect(Collectors.toList());
        var accs = findAllowedResourceAccounts(roles);
        return accs;
    }

    public void upsert(String role, String resourceAccountName) {
        long resourceAccountId = resourceAccountService.getIdByName(resourceAccountName);
        repo.upsert(role, resourceAccountId);
    }

    public void setAllRoleAccounts(List<RoleAccounts> roleAccountsList) {
        repo.truncate();
        for (RoleAccounts roleAccounts: roleAccountsList) {
            for (ResourceAccount resAcc: roleAccounts.resourceAccounts()) {
                upsert(roleAccounts.role(), resAcc.getName());
            }
        }
    }
    public Boolean userCanAccessAccount(String resourceAccountName) {
        for (ResourceAccount acc: findAllowed()) {
            if (acc.getName().equals(resourceAccountName)) {
                return true;
            }
        }
        return false;

    }
}
