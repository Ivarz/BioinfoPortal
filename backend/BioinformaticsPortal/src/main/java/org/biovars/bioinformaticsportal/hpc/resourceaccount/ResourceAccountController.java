package org.biovars.bioinformaticsportal.hpc.resourceaccount;

import com.jcraft.jsch.JSchException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ResourceAccountController {
    private final KeycloakService keycloakService;
    private ResourceAccountService resourceAccountService;
    ResourceAccountController(ResourceAccountService resourceAccountService, KeycloakService keycloakService) {
        this.resourceAccountService = resourceAccountService;
        this.keycloakService = keycloakService;
    }
    @GetMapping("/hpc/resource-accounts/update")
    public String updateResourceAccounts() {
        try {
            resourceAccountService.updateResourceAccounts();
            return "OK";
        } catch (JSchException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("admin/resource-accounts")
    public List<ResourceAccount> findActiveResourceAccountsForAdmin() {
        return resourceAccountService.findActive();
    }

    @GetMapping("admin/roles")
    public List<String> findAllRoles() {
        return keycloakService.getSpaRoleNames();
    }
}
