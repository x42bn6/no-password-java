package org.x42bn6.nopassword.hashingstrategies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class Argon2HashingStrategyTest {

    public static final byte[] UNHASHED_PASSWORD = "password".getBytes(StandardCharsets.UTF_8);
    private Argon2HashingStrategy argon2HashingStrategy;

    @BeforeEach
    void setUp() {
        argon2HashingStrategy = Argon2HashingStrategy.withDefaultParameters();
    }

    @Test
    void generateHash() {
        final HashOutput firstRun = argon2HashingStrategy.generateHashWithNewSalt(UNHASHED_PASSWORD);
        final HashOutput secondRun = argon2HashingStrategy.generateHashWithExistingSalt(firstRun.getSalt(), UNHASHED_PASSWORD);

        assertArrayEquals(firstRun.getSalt(), secondRun.getSalt());
        assertArrayEquals(firstRun.getPassword(), secondRun.getPassword());
    }
}