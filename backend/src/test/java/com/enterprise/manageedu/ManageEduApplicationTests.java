package com.enterprise.manageedu;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "manageedu.cli.enabled=false")
class ManageEduApplicationTests {

    @Test
    void contextLoads() {
    }
}
