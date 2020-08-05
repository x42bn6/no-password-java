package org.x42bn6.nopassword.hashingstrategies;

import at.favre.lib.crypto.bcrypt.BCrypt;
import at.favre.lib.crypto.bcrypt.Radix64Encoder;

/**
 * {@code BcryptHashingStrategy} is a cryptographic hashing strategy that employs the
 * <a href="https://en.wikipedia.org/wiki/Bcrypt">bcrypt</a> hashing scheme, outputing a Modular Crypt Format hash.
 * <p>
 * The underlying library used is
 * <a href="https://github.com/patrickfav/bcrypt">Patrick Favre-Bulle's implementation</a>.
 */
public class BcryptHashingStrategy implements HashingStrategy {
    public static final int DEFAULT_COST = 6;
    public static final int PREFIX_LENGTH = BCrypt.Version.VERSION_2A.versionIdentifier.length;
    public static final int COST_LENGTH_OCTAL = 2;
    public static final int HASHED_PASSWORD_LENGTH = 31;
    public static final int SEPARATOR_LENGTH = 1;
    public static final int SALT_LENGTH = 22;
    //                                           $                  2a              $                  06                  $
    public static final int LENGTH_BEFORE_SALT = SEPARATOR_LENGTH + PREFIX_LENGTH + SEPARATOR_LENGTH + COST_LENGTH_OCTAL + SEPARATOR_LENGTH;

    private final int cost;

    /**
     * Constructs a {@code BcryptHashingStrategy} with default cost ({@link #DEFAULT_COST}).
     */
    public BcryptHashingStrategy() {
        this(DEFAULT_COST);
    }

    /**
     * Constructs a {@code BcryptHashingStrategy} with a specified cost.
     */
    public BcryptHashingStrategy(int cost) {
        this.cost = cost;
    }

    @Override
    public HashOutput generateHashWithNewSalt(byte[] unhashedPassword) {
        return wrapBytes(BCrypt.withDefaults().hash(cost, unhashedPassword));
    }

    @Override
    public HashOutput generateHashWithExistingSalt(byte[] salt, byte[] unhashedPassword) {
        Radix64Encoder.Default radix64Encoder = new Radix64Encoder.Default();
        byte[] decodedSalt = radix64Encoder.decode(salt);
        return wrapBytes(BCrypt.withDefaults().hash(cost, decodedSalt, unhashedPassword));
    }

    private HashOutput wrapBytes(byte[] hash) {
        byte[] salt = new byte[SALT_LENGTH];
        byte[] hashedPassword = new byte[HASHED_PASSWORD_LENGTH];
        // Sample hash:
        // $2a$06$Xj6qWTDv.Jbhk8Z44PHolewAq4uwZrBjwUplueEk0ns1kstNsCWri
        // |A |B |C                    |D                             |
        // A = prefix, B = cost (0x), C = salt, D = password
        System.arraycopy(hash, LENGTH_BEFORE_SALT, salt, 0, SALT_LENGTH);
        System.arraycopy(hash, LENGTH_BEFORE_SALT + SALT_LENGTH, hashedPassword, 0, HASHED_PASSWORD_LENGTH);
        return new HashOutput(salt, hashedPassword);
    }
}
