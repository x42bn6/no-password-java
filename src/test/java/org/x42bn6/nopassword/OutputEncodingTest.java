package org.x42bn6.nopassword;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.migrationsupport.EnableJUnit4MigrationSupport;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

@EnableJUnit4MigrationSupport
class OutputEncodingTest {
    private static final byte[] UPPERCASE = {
            (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F', (byte) 'G',
            (byte) 'H', (byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N',
            (byte) 'O', (byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U',
            (byte) 'V', (byte) 'W', (byte) 'X', (byte) 'Y', (byte) 'Z'
    };

    private static final byte[] LOWERCASE = {
            (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f', (byte) 'g',
            (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l', (byte) 'm', (byte) 'n',
            (byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r', (byte) 's', (byte) 't', (byte) 'u',
            (byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z'
    };

    private static final byte[] NUMBERS = {
            (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5', (byte) '6',
            (byte) '7', (byte) '8', (byte) '9'
    };

    private static final byte[] SYMBOLS = {
            (byte) '!', (byte) '"', (byte) '#', (byte) '$', (byte) '%', (byte) '&', (byte) '\'',
            (byte) '(', (byte) ')', (byte) '*', (byte) '+', (byte) ',', (byte) '-', (byte) '.',
            (byte) '/'
    };

    @Ignore
    @Test
    void remap_All() {
        OutputEncoding outputEncoding = new OutputEncoding.Builder()
                .uppercaseCount(1)
                .lowercaseCount(1)
                .numberCount(1)
                .symbolCount(1)
                .build();

        final byte[] remap = outputEncoding.remap("password".getBytes(StandardCharsets.UTF_8));

        assertTrue(checkAtLeastN(remap, UPPERCASE, outputEncoding.getUppercaseCount()));
        assertTrue(checkAtLeastN(remap, LOWERCASE, outputEncoding.getLowercaseCount()));
        assertTrue(checkAtLeastN(remap, NUMBERS, outputEncoding.getNumberCount()));
        assertTrue(checkAtLeastN(remap, SYMBOLS, outputEncoding.getSymbolCount()));
    }

    private boolean checkAtLeastN(byte[] remap, byte[] alphabet, int count) {
        // Intersect remap and alphabet and get uniques
        Set<Byte> uniques = new HashSet<>();
        for (byte b : remap) {
            if (checkIn(b, alphabet)) {
                uniques.add(b);
            }
        }

        return uniques.size() >= count;
    }

    private boolean checkIn(byte thisByte, byte[] alphabet) {
        for (byte thatByte : alphabet) {
            if (thatByte == thisByte) {
                return true;
            }
        }

        return false;
    }
}