package org.x42bn6.nopassword;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * {@code PRNG} is a utility class that generates a SHA1PRNG {@link SecureRandom} instance for convenience.
 */
public final class PRNG {
    public static final String ALGORITHM = "SHA1PRNG";

    public static SecureRandom newInstance() {
        try {
            return SecureRandom.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new NoPasswordException("Unable to find PRNG with name [" + ALGORITHM + "]", e);
        }
    }
}
