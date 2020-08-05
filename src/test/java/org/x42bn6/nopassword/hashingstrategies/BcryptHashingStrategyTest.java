package org.x42bn6.nopassword.hashingstrategies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class BcryptHashingStrategyTest {

    public static final byte[] UNHASHED_PASSWORD = "password".getBytes(StandardCharsets.UTF_8);
    private BcryptHashingStrategy bcryptHashingStrategy;

    @BeforeEach
    void setUp() {
        bcryptHashingStrategy = new BcryptHashingStrategy();
    }

    @Test
    void generateHash() {
        final HashOutput firstRun = bcryptHashingStrategy.generateHashWithNewSalt(UNHASHED_PASSWORD);
        final HashOutput secondRun = bcryptHashingStrategy.generateHashWithExistingSalt(firstRun.getSalt(), UNHASHED_PASSWORD);

        assertArrayEquals(firstRun.getSalt(), secondRun.getSalt());
        assertArrayEquals(firstRun.getPassword(), secondRun.getPassword());
    }
}