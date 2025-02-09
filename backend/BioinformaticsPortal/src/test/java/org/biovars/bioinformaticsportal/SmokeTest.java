package org.biovars.bioinformaticsportal;

import org.biovars.bioinformaticsportal.hpc.resourceaccount.ResourceAccountController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class SmokeTest {
    @Autowired
    private ResourceAccountController resourceAccountController;

    @Test
    void contextLoads() throws Exception {
        assertThat(resourceAccountController).isNotNull();
    }
}
