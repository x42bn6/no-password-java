package org.x42bn6.nopassword.hashingstrategies;

import org.x42bn6.nopassword.NoPasswordException;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * An {@code AutomaticArgon2HashingStrategy} can create an {@link Argon2HashingStrategy} with optimal parameters using
 * JVM heuristics.
 * <p>
 * This follows the
 * <a href="https://tools.ietf.org/html/draft-irtf-cfrg-argon2-08#section-4">proposal in the IETF paper</a>
 * on parameter choice.
 * <p>
 * The heuristics are as follows:
 *
 * <dl>
 *     <dt>Parallelism</dt>
 *     <dd>Uses {@link Runtime#availableProcessors()}, which should return the number of native processor threads.</dd>
 *     <dt>Memory cost</dt>
 *     <dd>Uses {@link Runtime#maxMemory()}.  Assumes that the heap size used to run the program uses a
 *     relatively-insignificant amount of memory so some degree of paging is acceptable.</dd>
 *     <dt>Time cost</dt>
 *     <dd>An optimal value is calculated based on a desired estimation of 0.5s.</dd>
 *     <dt>Salt length</dt>
 *     <dd>Defaults to 16 per the specification.</dd>
 *     <dt>Hash length</dt>
 *     <dd>Defaults to 32.</dd>
 * </dl>
 */
public class AutomaticArgon2HashingStrategy {
    private static final SecureRandom SECURE_RANDOM;
    private static final int ITERATIONS_FOR_AVERAGING = 10;

    static {
        final String algorithm = "SHA1PRNG";
        try {
            SECURE_RANDOM = SecureRandom.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new NoPasswordException("Unable to find PRNG with name [" + algorithm + "]", e);
        }
    }

    public static Argon2HashingStrategy determineOptimalStrategy() {
        long timeTaken = Long.MIN_VALUE;
        int iterations = 1;

        Argon2HashingStrategy candidateStrategy = null;
        // 1 second
        while (timeTaken < 1000) {
            candidateStrategy = new Argon2HashingStrategy.Builder()
                    .parallelism(Runtime.getRuntime().availableProcessors())
                    .memoryCost((int) (Runtime.getRuntime().freeMemory() / 1024))
                    .timeCost(iterations++)
                    .saltLength(16)
                    .hashLength(32)
                    .build();

            long totalTime = 0L;
            for (int i = 0; i < ITERATIONS_FOR_AVERAGING; i++) {
                byte[] password = new byte[16];
                SECURE_RANDOM.nextBytes(password);
                long start = System.currentTimeMillis();
                candidateStrategy.generateHashWithNewSalt(password);
                long end = System.currentTimeMillis();
                totalTime += end - start;
            }
            timeTaken = totalTime / ITERATIONS_FOR_AVERAGING;
        }

        return candidateStrategy;
    }
}
