package org.x42bn6.nopassword;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.x42bn6.nopassword.hashingstrategies.HashOutput;
import org.x42bn6.nopassword.hashingstrategies.HashingStrategy;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SaltsTest {

    public static final Charset ENCODING = StandardCharsets.UTF_8;
    private Salts salts;

    @BeforeEach
    void setUp() {
        salts = new Salts();
    }

    @Test
    void getPasswordForService() {
        Service service = mock(Service.class);
        final HashingStrategy hashingStrategy = mock(HashingStrategy.class);
        when(service.getHashingStrategy()).thenReturn(hashingStrategy);

        byte[] unhashedPassword = "password".getBytes(ENCODING);
        final HashOutput hashOutput = mock(HashOutput.class);
        final byte[] hashedPassword = "hashedPassword".getBytes(ENCODING);
        when(hashOutput.getPassword()).thenReturn(hashedPassword);
        final byte[] salt = "salt".getBytes(ENCODING);
        when(hashOutput.getSalt()).thenReturn(salt);
        when(hashingStrategy.generateHashWithNewSalt(unhashedPassword)).thenReturn(hashOutput);
        when(hashingStrategy.generateHashWithExistingSalt(salt, unhashedPassword)).thenReturn(hashOutput);

        byte[] firstSave = salts.getPasswordForService(service, unhashedPassword);
        byte[] secondSave = salts.getPasswordForService(service, unhashedPassword);

        assertArrayEquals(firstSave, secondSave);
        assertArrayEquals(hashedPassword, firstSave);
    }
}