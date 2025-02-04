package org.biovars.bioinformaticsportal.hpc.resourceaccount;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

@Service
public class ResourceAccountService {

    @Value("${hpc.key}")
    private String privateKeyPath;

    @Value("${hpc.host}")
    private String host;

    @Value("${hpc.user}")
    private String user;

    @Value("${hpc.port}")
    private int port;

    private final ResourceAccountRepository resourceAccountRepository;
    private final KeycloakService keycloakService;

    ResourceAccountService(ResourceAccountRepository resourceAccountRepository,
                           KeycloakService keycloakService) {
        this.resourceAccountRepository = resourceAccountRepository;
        this.keycloakService = keycloakService;
    }

    public void updateResourceAccounts() throws JSchException, InterruptedException {
        JSch jsch = new JSch();

        jsch.addIdentity(privateKeyPath);

        // Create session
        Session session = jsch.getSession(user, host, port);

        // Configure session settings
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);

        // Connect to the server
        session.connect();

        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand("mam-balance --full --format raw");
        ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
        channel.setOutputStream(responseStream);
        channel.connect();

        while (channel.isConnected()) {
            Thread.sleep(100);
        }

        String responseString = new String(responseStream.toByteArray());
        var accLines = responseString.split("\n");

        resourceAccountRepository.deactivateAll();
        for (var line: accLines) {
            if (!line.contains("=")) {
                continue;
            }
            var resAcc = ResourceAccount.parseHpcAccountString(line);
            resourceAccountRepository.upsert(resAcc.getName(), resAcc.getBalance());
        }

        session.disconnect();
        channel.disconnect();
    }
    public long getIdByName(String name) {
        return resourceAccountRepository.getIdByName(name);
    }

    public ResourceAccount findByName(String name) {
        return resourceAccountRepository.findByName(name);
    }

    public List<ResourceAccount> findAll() {
        return resourceAccountRepository.findAll();
    }

    public List<ResourceAccount> findActive() {
        return resourceAccountRepository.findActive();
    }

    List<ResourceAccount> findAllowed(Jwt jwt) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        var roles = keycloakService.getClientRoles("940ed264-651b-4508-b216-fc88172c295b");
        System.out.println(roles);
        return resourceAccountRepository.findActive()
                .stream()
                .collect(Collectors.toList());
    }
}
