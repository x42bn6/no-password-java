package org.x42bn6.nopassword.utilities;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ByteArrayOperationsTest {

    @Test
    void fisherYatesShuffle() {
        byte[] input = new byte[]{(byte) 'a', (byte) 'b', (byte) 'c'};

        final Random prng = mock(Random.class);
        // Swap c with a
        when(prng.nextInt(2)).thenReturn(0);
        // Swap b with itself
        when(prng.nextInt(1)).thenReturn(1);

        byte[] result = ByteArrayOperations.fisherYatesShuffle(input, prng);

        assertArrayEquals(new byte[]{(byte) 'c', (byte) 'b', (byte) 'a'}, result);
    }

    @Test
    void checkIn() {
        byte[] input = new byte[]{(byte) 'a', (byte) 'b', (byte) 'c'};

        assertTrue(ByteArrayOperations.checkIn((byte) 'b', input));
        assertFalse(ByteArrayOperations.checkIn((byte) 'd', input));
    }

    @Test
    void subtract() {
        byte[] superset = new byte[]{(byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e'            };
        byte[] subset   = new byte[]{(byte) 'a',             (byte) 'c',                         (byte) 'f'};

        byte[] result = ByteArrayOperations.subtract(superset, subset);

        assertArrayEquals(new byte[]{(byte) 'b', (byte) 'd', (byte) 'e'}, result);
    }

    @Test
    void clear_Byte() {
        byte[] input = new byte[]{(byte) 'a', (byte) 'b', (byte) 'c'};

        ByteArrayOperations.clear(input);

        assertArrayEquals(new byte[]{(byte) 0, (byte) 0, (byte) 0}, input);
    }

    @Test
    void clear_Int() {
        int[] input = new int[]{0, 1, 2};

        ByteArrayOperations.clear(input);

        assertArrayEquals(new int[]{0, 0, 0}, input);
    }

    @Test
    void clear_Bool() {
        boolean[] input = new boolean[]{true, false, true, false};

        ByteArrayOperations.clear(input);

        assertArrayEquals(new boolean[]{false, false, false, false}, input);
    }
}