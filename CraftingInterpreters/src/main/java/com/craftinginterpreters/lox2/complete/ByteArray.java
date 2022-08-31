package com.craftinginterpreters.lox2.complete;

import java.util.AbstractList;
import java.util.RandomAccess;

public class ByteArray extends AbstractList<Byte> implements RandomAccess {
    private static final int MINIMUM_CAPACITY = 8;
    private int count;
    private int capacity;
    private byte[] contents;

    public ByteArray() {
        this.count = 0;
        this.capacity = MINIMUM_CAPACITY;
        this.contents = new byte[capacity];
    }

    @Override
    public Byte get(int index) {
        if (index < 0 || index >= count) {
            throw new IndexOutOfBoundsException(index);
        }

        return contents[index];
    }

    @Override
    public Byte set(int index, Byte element) {
        if (index < 0 || index >= count) {
            throw new IndexOutOfBoundsException(index);
        }

        byte old = contents[index];
        contents[index] = element;
        return old;
    }

    @Override
    public void add(int index, Byte element) {
        if (index < 0 || index > count) {
            throw new IndexOutOfBoundsException(index);
        }

        if (count == capacity) {
            // Double the available space
            int newCapacity = capacity * 2;
            byte[] newContents = new byte[newCapacity];
            System.arraycopy(contents, 0, newContents, 0, index);
            newContents[index] = element;
            System.arraycopy(contents, index, newContents, index + 1, count - index);
            contents = newContents;
            capacity = newCapacity;
            count = count + 1;
        } else {
            System.arraycopy(contents, index, contents, index + 1, count - index);
            contents[index] = element;
            count = count + 1;
        }
    }

    @Override
    public Byte remove(int index) {
        if (index < 0 || index >= count) {
            throw new IndexOutOfBoundsException(index);
        }

        byte old = contents[index];
        if (capacity > MINIMUM_CAPACITY && count <= capacity / 4) {
            int newCapacity = capacity / 2;
            byte[] newContents = new byte[newCapacity];
            System.arraycopy(contents, 0, newContents, 0, index);
            System.arraycopy(contents, index + 1, newContents, index, count - index - 1);
            contents = newContents;
            capacity = newCapacity;
            count = count - 1;
        } else {
            System.arraycopy(contents, index + 1, contents, index, count - index - 1);
            count = count - 1;
        }

        return old;
    }

    @Override
    public int size() {
        return count;
    }
}
