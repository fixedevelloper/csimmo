package com.immogest.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import({TestcontainersConfiguration.class, SeaweedFsTestConfiguration.class})
class ImmoGestApplicationTest {
    @Test
    void contextLoads() {
    }
}
