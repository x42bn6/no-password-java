package org.x42bn6.nopassword.hashingstrategies;

import at.favre.lib.crypto.bcrypt.Radix64Encoder;
import com.lambdaworks.crypto.SCrypt;
import org.x42bn6.nopassword.NoPasswordException;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

/**
 * {@code ScryptHashingStrategy} is a cryptographic hashing strategy that employs the
 * <a href="https://en.wikipedia.org/wiki/scrypt">scrypt</a> hashing scheme, outputing a PDC hash.
 * <p>
 * The underlying library used is <a href="https://github.com/wg/scrypt">Will Glozer's implementation</a>.
 */
public class ScryptHashingStrategy implements HashingStrategy {
    public static final int DEFAULT_COST = 16384;
    public static final int DEFAULT_BLOCK_SIZE_FACTOR = 8;
    public static final int DEFAULT_PARALLELIZATION_FACTOR = 1;
    public static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;
    public static final String PRNG_ALGORITHM = "SHA1PRNG";
    public static final int DERIVED_KEY_LENGTH = 32;

    /**
     * The cost of the algorithm; needs to be a power of 2 (default 16384).
     */
    private final int cost;

    /**
     * The blocksize parameter of the algorithm (default 8).
     */
    private final int blockSizeFactor;

    /**
     * The parallelisation parameter, default 1.
     */
    private final int parallelisationFactor;

    /**
     * Constructs a {@code ScryptHashingStrategy} with default cost, block size factor and parallelisation factor
     * parameters.
     */
    public ScryptHashingStrategy() {
        this(DEFAULT_COST, DEFAULT_BLOCK_SIZE_FACTOR, DEFAULT_PARALLELIZATION_FACTOR);
    }

    /**
     * Constructs a {@code ScryptHashingStrategy} with specified cost, block size factor and parallelisation factor
     * parameters.
     */
    public ScryptHashingStrategy(int cost, int blockSizeFactor, int parallelisationFactor) {
        this.cost = cost;
        this.blockSizeFactor = blockSizeFactor;
        this.parallelisationFactor = parallelisationFactor;
    }

    @Override
    public HashOutput generateHashWithNewSalt(byte[] unhashedPassword) {
        try {
            byte[] salt = new byte[16];
            SecureRandom.getInstance(PRNG_ALGORITHM).nextBytes(salt);
            return generateHashWithExistingSalt(salt, unhashedPassword);
        } catch (GeneralSecurityException e) {
            throw new NoPasswordException("Unable to find algorithm [" + PRNG_ALGORITHM + "]", e);
        }
    }

    @Override
    public HashOutput generateHashWithExistingSalt(byte[] salt, byte[] unhashedPassword) {
        try {
            byte[] hashedPassword = SCrypt.scrypt(unhashedPassword, salt, cost, blockSizeFactor, parallelisationFactor,
                    DERIVED_KEY_LENGTH);

            String params = Long.toString(log2(cost) << 16L | blockSizeFactor << 8 | parallelisationFactor, 16);

            byte[] separatorBytes = "$".getBytes(DEFAULT_ENCODING);
            byte[] algorithmBytes = "s0".getBytes(DEFAULT_ENCODING);
            byte[] paramsBytes = params.getBytes(DEFAULT_ENCODING);
            Radix64Encoder.Default radix64Encoder = new Radix64Encoder.Default();
            byte[] encodedSalt = radix64Encoder.encode(salt);
            byte[] encodedHashedPassword = radix64Encoder.encode(hashedPassword);

            final int separatorByteLength = separatorBytes.length;
            ByteBuffer byteBuffer = ByteBuffer.allocate(separatorByteLength + algorithmBytes.length +
                    separatorByteLength + paramsBytes.length +
                    separatorByteLength + encodedSalt.length +
                    separatorByteLength + encodedHashedPassword.length
            );
            byteBuffer
                    .put(separatorBytes)
                    .put(algorithmBytes)
                    .put(separatorBytes)
                    .put(paramsBytes)
                    .put(separatorBytes)
                    .put(encodedSalt)
                    .put(separatorBytes)
                    .put(encodedHashedPassword);
            return new HashOutput(salt, byteBuffer.array());
        } catch (GeneralSecurityException e) {
            throw new NoPasswordException("Unable to find algorithm [" + PRNG_ALGORITHM + "]", e);
        }
    }

    private int appendBytesToFormattedOutput(byte[] bytes, byte[] output, int index) {
        System.arraycopy(bytes, 0, output, index, bytes.length);
        return bytes.length;
    }

    private static int log2(int n) {
        int log = 0;
        if ((n & 0xffff0000) != 0) {
            n >>>= 16;
            log = 16;
        }
        if (n >= 256) {
            n >>>= 8;
            log += 8;
        }
        if (n >= 16) {
            n >>>= 4;
            log += 4;
        }
        if (n >= 4) {
            n >>>= 2;
            log += 2;
        }
        return log + (n >>> 1);
    }
}
