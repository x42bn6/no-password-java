package org.x42bn6.nopassword.hashingstrategies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class ScryptHashingStrategyTest {

    public static final byte[] UNHASHED_PASSWORD = "password".getBytes(StandardCharsets.UTF_8);
    private ScryptHashingStrategy scryptHashingStrategy;

    @BeforeEach
    void setUp() {
        scryptHashingStrategy = new ScryptHashingStrategy();
    }

    @Test
    void generateHash() {
        final HashOutput firstRun = scryptHashingStrategy.generateHashWithNewSalt(UNHASHED_PASSWORD);
        final HashOutput secondRun = scryptHashingStrategy.generateHashWithExistingSalt(firstRun.getSalt(), UNHASHED_PASSWORD);

        assertArrayEquals(firstRun.getSalt(), secondRun.getSalt());
        assertArrayEquals(firstRun.getPassword(), secondRun.getPassword());
    }

}