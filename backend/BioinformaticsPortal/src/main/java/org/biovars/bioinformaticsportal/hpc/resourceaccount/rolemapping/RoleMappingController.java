package org.biovars.bioinformaticsportal.hpc.resourceaccount.rolemapping;

import org.biovars.bioinformaticsportal.hpc.resourceaccount.ResourceAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RoleMappingController {

    private static final Logger logger = LoggerFactory.getLogger(RoleMappingController.class);
    private final RoleMappingService roleMappingService;
    public RoleMappingController(RoleMappingService roleMappingService) {
        this.roleMappingService = roleMappingService;
    }

    @PostMapping("admin/role-mapping")
    public void addRoleMapping(@RequestBody RoleMapping roleMapping) {
        logger.info("Calling addRoleMapping");
        logger.info(roleMapping.toString());
        logger.info(roleMapping.resourceAccount().getName());
        roleMappingService.upsert(roleMapping.role(), roleMapping.resourceAccount().getName());
    }

    @GetMapping("admin/role-mapping")
    public List<RoleMapping> getRoleMapping() {
        return roleMappingService.findAllRoleMappings();
    }
    @GetMapping("admin/role-accounts")
    public List<RoleAccounts> getRoleAccounts() {
        return roleMappingService.findAllRoleAccounts();
    }
    @PostMapping("admin/role-accounts")
    public void setAllRoleAccounts(@RequestBody List<RoleAccounts> roleAccounts) {
        roleMappingService.setAllRoleAccounts(roleAccounts);
    }
    @GetMapping("hpc/resource-accounts")
    public List<ResourceAccount> findAllResourceAccounts() {
        return roleMappingService.findAllowed();
    }
}
