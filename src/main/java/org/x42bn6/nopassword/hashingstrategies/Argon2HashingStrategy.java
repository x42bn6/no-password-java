package org.x42bn6.nopassword.hashingstrategies;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.x42bn6.nopassword.NoPasswordException;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

/**
 * An {@code Argon2HashingStrategy} is a hashing strategy implementing the
 * <a href="https://en.wikipedia.org/wiki/Argon2">Argon2</a>
 * hashing algorithm, using
 * <a href="https://docs.spring.io/spring-security/site/docs/5.0.x/reference/html/crypto.html">Spring Security</a>
 * in conjunction with <a href="https://www.bouncycastle.org/">Bouncy Castle</a>.
 * <p>
 * Instances of this class can be built using {@link Builder}, allowing users to specify the Argon2 tuning parameters,
 * or by using {@link #withDefaultParameters()}, which uses the default parameters (see link for more information).
 * <p>
 * The output hash is in bytes, and should be converted to a human-readable format (such as via Base64 encoding) for
 * user-friendliness.
 */
@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE)
public class Argon2HashingStrategy implements HashingStrategy {
    private static final SecureRandom SECURE_RANDOM;

    static {
        final String algorithm = "SHA1PRNG";
        try {
            SECURE_RANDOM = SecureRandom.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new NoPasswordException("Unable to find PRNG with name [" + algorithm + "]", e);
        }
    }

    private final int type;
    private final int memoryCost;
    private final int timeCost;
    private final int parallelism;
    private final int saltLength;
    private final int hashLength;

    // Only used by deserialization
    @SuppressWarnings("unused")
    private Argon2HashingStrategy() {
        this.type = 0;
        this.memoryCost = 0;
        this.timeCost = 0;
        this.parallelism = 0;
        this.saltLength = 0;
        this.hashLength = 0;
    }

    public Argon2HashingStrategy(Builder builder) {
        this.type = builder.type;
        this.memoryCost = builder.memoryCost;
        this.timeCost = builder.timeCost;
        this.parallelism = builder.parallelism;
        this.saltLength = builder.saltLength;
        this.hashLength = builder.hashLength;
    }

    /**
     * Generates a default hashing strategy with the following parameters:
     * <p>
     * : *
     * <dl>
     *     <dt>Type</dt>
     *     <dd>Argon2id</dd>
     *     <dt>Memory cost</dt>
     *     <dd>65536 (64 Mb)</dd>
     *     <dt>Time cost</dt>
     *     <dd>3</dd>
     *     <dt>Parallelism</dt>
     *     <dd>4</dd>
     *     <dt>Salt length (bytes)</dt>
     *     <dd>16</dd>
     *     <dt>Hash length (bytes)</dt>
     *     <dd>32</dd>
     * </dl>
     *
     * @return A builder configured with default settings as detailed above
     */
    public static Argon2HashingStrategy withDefaultParameters() {
        return new Builder().build();
    }

    @Override
    public HashOutput generateHashWithNewSalt(byte[] unhashedPassword) {
        byte[] salt = new byte[saltLength];
        SECURE_RANDOM.nextBytes(salt);
        return generateHashWithExistingSalt(salt, unhashedPassword);
    }

    @Override
    public HashOutput generateHashWithExistingSalt(byte[] salt, byte[] unhashedPassword) {
        assert saltLength == salt.length;
        byte[] hash = new byte[hashLength];

        Argon2Parameters params = new Argon2Parameters.Builder(type)
                .withSalt(salt)
                .withParallelism(parallelism)
                .withMemoryAsKB(memoryCost)
                .withIterations(timeCost)
                .build();
        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        generator.init(params);
        generator.generateBytes(unhashedPassword, hash);

        return new HashOutput(salt, hash);
    }

    public static class Builder {
        public static final int DEFAULT_MEMORY_COST = 65536;
        public static final int DEFAULT_TIME_COST = 3;
        public static final int DEFAULT_PARALLELISM = 4;
        public static final int DEFAULT_SALT_LENGTH = 16;
        public static final int DEFAULT_HASH_LENGTH = 32;

        private int type = Argon2Parameters.ARGON2_id;
        private int memoryCost = DEFAULT_MEMORY_COST;
        private int timeCost = DEFAULT_TIME_COST;
        private int parallelism = DEFAULT_PARALLELISM;
        private int saltLength = DEFAULT_SALT_LENGTH;
        private int hashLength = DEFAULT_HASH_LENGTH;

        public Builder type(int type) {
            this.type = type;
            return this;
        }

        public Builder memoryCost(int memoryCost) {
            this.memoryCost = memoryCost;
            return this;
        }

        public Builder timeCost(int timeCost) {
            this.timeCost = timeCost;
            return this;
        }

        public Builder parallelism(int parallelism) {
            this.parallelism = parallelism;
            return this;
        }

        public Builder saltLength(int saltLength) {
            this.saltLength = saltLength;
            return this;
        }

        public Builder hashLength(int hashLength) {
            this.hashLength = hashLength;
            return this;
        }

        public Argon2HashingStrategy build() {
            return new Argon2HashingStrategy(this);
        }
    }
}