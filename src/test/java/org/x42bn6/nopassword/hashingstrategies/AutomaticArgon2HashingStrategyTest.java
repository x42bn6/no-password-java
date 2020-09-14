package org.x42bn6.nopassword.hashingstrategies;

import org.junit.jupiter.api.Test;

class AutomaticArgon2HashingStrategyTest {

    @Test
    void determineOptimalStrategy() {
        // 1 core, 256 MB RAM, 1ms
        AutomaticArgon2HashingStrategy.determineOptimalStrategy(1, (1 << 20) * 256, 1);
    }
}