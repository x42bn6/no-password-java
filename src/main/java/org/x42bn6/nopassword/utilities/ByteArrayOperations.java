package org.x42bn6.nopassword.utilities;

import java.util.Arrays;
import java.util.Random;

/**
 * {@code ByteArrayOperations} is a utilities class for byte arrays, aimed to be used to manipulate password or
 * sensitive data.
 * <p>
 * If an array is returned, callees should zero-out this value (and its input parameters, as a best-practice) in a
 * {@code finally} block.
 */
public final class ByteArrayOperations {
    /**
     * Performs a Fisher-Yates shuffle on an array, randomizing it.  Mutates {@code arr}.
     *
     * @param arr  The array to be randomized.  Mutated
     * @param prng The PRNG.  Recommendation is to use an instance of {@code SecureRandom}.
     */
    public static byte[] fisherYatesShuffle(byte[] arr, Random prng) {
        byte[] result = Arrays.copyOf(arr, arr.length);
        for (int i = result.length - 1; i > 0; i--) {
            final int j = prng.nextInt(i);
            byte temp = result[i];
            result[i] = result[j];
            result[j] = temp;
        }

        return result;
    }

    /**
     * Returns true if {@code thisByte} is in {@code arr}; false otherwise.
     *
     * @param thisByte The byte to be checked
     * @param arr      The array
     * @return true if {@code thisByte} is in {@code arr}; false otherwise
     */
    public static boolean checkIn(byte thisByte, byte[] arr) {
        for (byte thatByte : arr) {
            if (thatByte == thisByte) {
                return true;
            }
        }

        return false;
    }

    /**
     * Subtracts {@code subset} from the {@code superset}.  If a byte exists in {@code subset} but not {@code superset},
     * it is ignored.
     * <p>
     * The resulting order should not be assumed.
     *
     * @param superset The superset of bytes
     * @param subset   The subset of bytes
     */
    public static byte[] subtract(byte[] superset, byte[] subset) {
        int intersectionCount = 0;

        // There is no way to do this in one loop without temporarily allocating a byte[] temp variable which should
        // be avoided if it contains sensitive data (we don't know how long the array will be).
        // Instead, run two loops - one to find the intersection count (to derive array length) and one to
        // actually populate it.
        for (byte b : subset) {
            if (checkIn(b, superset)) {
                ++intersectionCount;
            }
        }

        byte[] result = new byte[superset.length - intersectionCount];
        int rI = 0;
        for (byte b : superset) {
            if (!checkIn(b, subset)) {
                result[rI++] = b;
            }
        }

        return result;
    }

    /**
     * Clears an array of bytes, filling it with 0.
     *
     * @param arr The array to fill
     */
    public static void clear(byte[] arr) {
        Arrays.fill(arr, (byte) 0);
    }

    /**
     * Clears an array of integers, filling it with 0.
     *
     * @param arr The array to fill
     */
    public static void clear(int[] arr) {
        Arrays.fill(arr, 0);
    }

    /**
     * Clears an array of boolean values, filling it with false.
     *
     * @param arr The array to fill
     */
    public static void clear(boolean[] arr) {
        Arrays.fill(arr, false);
    }
}
