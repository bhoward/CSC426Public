package com.craftinginterpreters.lox2.ch14;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ByteArrayTest {

    @Test
    void testEmpty() {
        ByteArray a = new ByteArray();
        assertEquals(0, a.size());
        assertThrows(IndexOutOfBoundsException.class, () -> {
            a.get(0);
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            a.set(0, (byte) 0);
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            a.remove(0);
        });
    }

    @Test
    void testAdd() {
        ByteArray a = new ByteArray();
        for (int i = 0; i < 256; i++) {
            byte b = (byte) (i - 128);
            assertThrows(IndexOutOfBoundsException.class, () -> {
                a.add(-1, b);
            });
            final int beyond = i + 1;
            assertThrows(IndexOutOfBoundsException.class, () -> {
                a.add(beyond, b);
            });
            a.add(b);
            assertEquals(i + 1, a.size());
            assertEquals(b, a.get(i));
        }

        for (int i = 0; i < 256; i++) {
            byte b = (byte) (i - 128);
            assertEquals(b, a.get(i));
        }
    }

    @Test
    void testSet() {
        ByteArray a = new ByteArray();
        for (int i = 0; i < 1000; i++) {
            a.add(0, (byte) 0);
            assertEquals(i + 1, a.size());
        }

        for (int i = 0; i < 1000; i++) {
            byte b = (byte) (i % 256 - 128);
            assertEquals((byte) 0, a.set(i, b));
        }

        assertEquals(1000, a.size());
        for (int i = 0; i < 1000; i++) {
            byte b = (byte) (i % 256 - 128);
            assertEquals(b, a.get(i));
        }
    }

    @Test
    void testRemove() {
        ByteArray a = new ByteArray();
        for (int i = 0; i < 1000; i++) {
            byte b = (byte) (i % 256 - 128);
            a.add(i, b);
        }

        for (int i = 0; i < 500; i++) {
            byte b = (byte) (i * 2 % 256 - 128);
            assertEquals(b, a.remove(i));
            assertEquals(999 - i, a.size());
        }

        for (int i = 0; i < 500; i++) {
            byte b = (byte) (i * 2 % 256 - 127);
            assertEquals(b, a.get(i));
        }

        for (int i = 0; i < 250; i++) {
            byte b = (byte) (i * 4 % 256 - 127);
            assertEquals(b, a.remove(i));
            assertEquals(499 - i, a.size());
        }

        assertEquals(250, a.size());

        for (int i = 0; i < 250; i++) {
            a.remove(0);
        }

        assert (a.isEmpty());
    }
}
