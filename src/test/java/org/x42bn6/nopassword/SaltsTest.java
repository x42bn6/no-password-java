package org.x42bn6.nopassword;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.mock;

class SaltsTest {

    private Salts salts;

    @BeforeEach
    void setUp() {
        salts = new Salts();
    }

    @Test
    void getPasswordForService() {
        Service service = mock(Service.class);

        byte[] unhashedPassword = "password".getBytes(StandardCharsets.UTF_8);
        byte[] firstSave = salts.getPasswordForService(service, unhashedPassword);
        byte[] secondSave = salts.getPasswordForService(service, unhashedPassword);

        assertArrayEquals(firstSave, secondSave);
    }
}