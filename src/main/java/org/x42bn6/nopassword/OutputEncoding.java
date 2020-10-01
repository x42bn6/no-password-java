package org.x42bn6.nopassword;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.x42bn6.nopassword.utilities.ByteArrayOperations;

import java.security.SecureRandom;
import java.util.Arrays;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static org.x42bn6.nopassword.utilities.ByteArrayOperations.checkIn;
import static org.x42bn6.nopassword.utilities.ByteArrayOperations.clear;

/**
 * {@code OutputEncoding} represents the set of symbols that a {@link Service}'s password can span.
 * <p>
 * By default, the password follows lowercase Base64 - all lowercase letters (without diacritics) and numbers - but some
 * services may have stronger requirements.  No Password supports the following:
 *
 * <ul>
 *     <li>Lowercase letters (default)</li>
 *     <li>Numbers (default)</li>
 *     <li>Uppercase letters</li>
 *     <li>Symbols</li>
 * </ul>
 * <p>
 * No Password will attempt to generate a password that satisfies all the requirements.  It does this by replacing
 * the entire Base64 alphabet with random characters from each of the required sets above, and applying this
 * bijection on top of the generated password, saving this bijection.
 * <p>
 * For symbols, No Password will pull a number of random symbols (the user can specify the number required).
 */
@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE)
public class OutputEncoding {
    private static final byte[] BASE64_INDEX_TABLE = {
            (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F', (byte) 'G',
            (byte) 'H', (byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N',
            (byte) 'O', (byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U',
            (byte) 'V', (byte) 'W', (byte) 'X', (byte) 'Y', (byte) 'Z',
            (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f', (byte) 'g',
            (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l', (byte) 'm', (byte) 'n',
            (byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r', (byte) 's', (byte) 't', (byte) 'u',
            (byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z',
            (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5', (byte) '6',
            (byte) '7', (byte) '8', (byte) '9',
            (byte) '+', (byte) '/'
    };

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

    /**
     * Denotes the minimum number of uppercase letters required (default: 0).
     */
    private final int uppercaseCount;

    /**
     * Denotes the minimum number of lowercase letters required (default: 1).
     */
    private final int lowercaseCount;

    /**
     * Denotes the minimum number of numbers required (default: 1).
     */
    private final int numberCount;

    /**
     * Denotes the minimum number of symbols required (default: 0).
     */
    private final int symbolCount;

    /**
     * Denotes the bijection between Base64 and output alphabet randomisation.  This array is of length 64 (matching
     * Base64).  The bijection is defined such that index <pre>i</pre> of {@link #SYMBOLS} maps to index
     * <pre>i</pre> of this array.
     */
    private final byte[] mapping;

    public OutputEncoding(Builder builder) {
        this.uppercaseCount = builder.uppercaseCount;
        this.lowercaseCount = builder.lowercaseCount;
        this.numberCount = builder.numberCount;
        this.symbolCount = builder.symbolCount;
        this.mapping = builder.mapping;
    }

    public int getUppercaseCount() {
        return uppercaseCount;
    }

    public int getLowercaseCount() {
        return lowercaseCount;
    }

    public int getNumberCount() {
        return numberCount;
    }

    public int getSymbolCount() {
        return symbolCount;
    }

    public byte[] getMapping() {
        return mapping;
    }

    /**
     * Remaps a password based on the input Base64-encoded password.
     * <p>
     * This method avoids using collection-based classes to avoid putting password-based information on the heap.
     *
     * @param password The input password
     * @return The remapped password
     */
    public byte[] remap(byte[] password) {
        byte[] uniques = new byte[0];
        boolean[] required = new boolean[0];
        int[] outputCounts = new int[0];
        byte[] shuffledUniques = new byte[0];
        byte[] unpicked = new byte[0];
        byte[] unmapped = new byte[0];
        byte[] finalAlphabet = new byte[0];
        try {
            // Get all unique characters
            uniques = new byte[0];
            for (byte b : password) {
                if (checkIn(b, uniques)) {
                    continue;
                }

                // Resize array
                uniques = Arrays.copyOf(uniques, uniques.length + 1);
                uniques[uniques.length - 1] = b;
            }

            // How many of each set do we need?
            int setCount =
                    (uppercaseCount > 0 ? 1 : 0) +
                            (lowercaseCount > 0 ? 1 : 0) +
                            (numberCount > 0 ? 1 : 0) +
                            (symbolCount > 0 ? 1 : 0);

            // We may have leftovers after dividing.
            // Distribute leftovers - prefer symbol -> number -> uppercase
            int perSet = uniques.length / setCount;
            int remainder = uniques.length - perSet * setCount;
            required = new boolean[]{symbolCount > 0, numberCount > 0, uppercaseCount > 0, lowercaseCount > 0};
            outputCounts = new int[]{perSet, perSet, perSet, perSet};
            int remainderIndex = 0;
            while (remainder > 0) {
                if (required[remainderIndex]) {
                    outputCounts[remainderIndex]++;
                    remainderIndex++;
                    remainder--;
                }
            }

            // Pick the required number of unique characters per set
            final SecureRandom secureRandom = PRNG.newInstance();
            shuffledUniques = new byte[uniques.length];
            int shuffledUniquesIndex = 0;
            for (int i = 0; i < outputCounts.length; i++) {
                Type type;
                switch (i) {
                    case 0:
                        type = Type.SYMBOL;
                        break;
                    case 1:
                        type = Type.NUMBER;
                        break;
                    case 2:
                        type = Type.UPPERCASE;
                        break;
                    case 3:
                        type = Type.LOWERCASE;
                        break;
                    default:
                        throw new IllegalStateException("Unable to map index [" + i + "] to type");
                }
                final int targetRandomCharacters = outputCounts[i];
                final byte[] randomBytes = type.randomN(targetRandomCharacters, secureRandom);
                for (byte randomByte : randomBytes) {
                    shuffledUniques[shuffledUniquesIndex++] = randomByte;
                }
            }

            // Now we need to map the other, unmapped characters, and generate the full bijection
            // First, get the full alphabet (i.e. if symbols are required, make sure all symbols are included -
            // repeat for all types)
            // Second, get the unpicked characters from the input
            // Third, get the unmapped characters from the previous step
            // Fourth, create a bijection from the remainder
            int count = 0;
            for (int i = 0; i < required.length; i++) {
                if (required[i]) {
                    count += Type.values()[i].alphabet.length;
                }
            }

            finalAlphabet = new byte[count];
            int fI = 0;
            for (int i = 0; i < required.length; i++) {
                if (!required[i]) {
                    continue;
                }

                final byte[] targetAlphabet = Type.values()[i].alphabet;
                for (byte b : targetAlphabet) {
                    finalAlphabet[fI++] = b;
                }
            }

            // Get all characters not in uniques - we need to map these too
            byte[] allBase64Characters = Arrays.copyOf(finalAlphabet, finalAlphabet.length);
            unpicked = ByteArrayOperations.subtract(allBase64Characters, uniques);

            // Now do the same but for unmapped
            unmapped = ByteArrayOperations.subtract(allBase64Characters, shuffledUniques);

            throw new UnsupportedOperationException("TODO");
        } finally {
            clear(uniques);
            clear(required);
            clear(outputCounts);
            clear(shuffledUniques);
            clear(unpicked);
            clear(unmapped);
            clear(finalAlphabet);
        }
    }

    public static class Builder {
        private int uppercaseCount = 0;
        private int lowercaseCount = 1;
        private int numberCount = 1;
        private int symbolCount = 0;
        private byte[] mapping;

        public Builder uppercaseCount(int uppercaseCount) {
            this.uppercaseCount = uppercaseCount;
            return this;
        }

        public Builder lowercaseCount(int lowercaseCount) {
            this.lowercaseCount = lowercaseCount;
            return this;
        }

        public Builder numberCount(int numberCount) {
            this.numberCount = numberCount;
            return this;
        }

        public Builder symbolCount(int symbolCount) {
            this.symbolCount = symbolCount;
            return this;
        }

        public OutputEncoding build() {
            return new OutputEncoding(this);
        }
    }

    private enum Type {
        UPPERCASE(OutputEncoding.UPPERCASE),
        LOWERCASE(OutputEncoding.LOWERCASE),
        NUMBER(NUMBERS),
        SYMBOL(SYMBOLS);

        private final byte[] alphabet;

        Type(byte[] alphabet) {
            this.alphabet = alphabet;
        }

        /**
         * Picks <pre>n</pre> random characters from the alphabet.
         *
         * @param n            The number of random characters
         * @param secureRandom The PRNG
         * @return <pre>n</pre> random characters from the alphabet
         */
        public byte[] randomN(int n, SecureRandom secureRandom) {
            byte[] copy = Arrays.copyOf(alphabet, alphabet.length);
            byte[] output = new byte[n];
            // Pick, without replacement, n characters
            // Pick a character at random and then swap with the end.  Then pick from reduced set
            for (int i = 0; i < n; i++) {
                final int maskedLength = copy.length - i;
                final int index = secureRandom.nextInt(maskedLength);
                output[i] = copy[index];

                byte temp = copy[maskedLength - 1];
                copy[maskedLength - 1] = copy[index];
                copy[index] = temp;
            }

            return output;
        }
    }
}