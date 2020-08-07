package setup;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Chose not to use java provided stack because it grows only 1 every time it is full,
 *      copying the array over every time is too expensive
 * @param <Item> the item the stack is storing
 */

public class Stack<Item> implements Iterable<Item> {

    private int size;
    private Item[] items;

    public Stack()
    {
        size = 0;
        items = (Item[]) new Object[1]; // the problem of unchecked cast will show up but can be ignored
    }

    /**
     * whether the stack is empty
     * @return true if the stack doesn't have anything in it
     *      otherwise false
     */
    public boolean isEmpty() { return size==0; }

    /**
     * @return size of the stack
     */
    public int size() { return size; }

    /**
     * copies the old array to a new array with the indicated size
     * Used to increase and the decrease the capacity of the array
     * @param newSize the size of the new array
     * @return a copied array with the specified size
     */
    private Item[] makeCopy(int newSize) {
        Item[] tempItems = (Item[]) new Object[newSize];
        for (int i = 0; i < size; i++)
            tempItems[i] = items[i];
        return tempItems;
    }

    /**
     * Insert a new item to top of the stack
     * @param item the element to be inserted
     */
    public void push(Item item) {
        if(size == items.length) items = makeCopy(2 * items.length);
        items[size] = item;
        size++;
    }

    /**
     * Removes the most recently added item from the stack and return it
     * @return the most recently added item
     */
    public Item pop() {
        if (size == 0) try {
            throw new NoSuchFieldException("No more elements in the stack");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        if (size > 0 && size == items.length/4)
            items = makeCopy(items.length/4);
        size--;
        return items[size];
    }

    /**
     * Returns the most recently added item without removing it from the stack
     * @return the most recently added item
     */
    public Item peek() {
        if (size == 0) try {
            throw new NoSuchFieldException("No more elements in the stack");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return items[size-1];
    }

    public <T> T[] toArray(T[] a) {
        System.arraycopy(items, 0, a, 0, size);
        return a;
    }

    /**
     * Returns an iterator over the item specified
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<Item> iterator() {
        return new stackIterator();
    }

    private class stackIterator implements Iterator<Item>
    {
        int current = size;

        /**
         * Returns {@code true} if the iteration has more elements.
         * (In other words, returns {@code true} if {@link #next} would
         * return an element rather than throwing an exception.)
         *
         * @return {@code true} if the iteration has more elements
         */
        @Override
        public boolean hasNext() {
            return current != 0;
        }

        /**
         * Returns the next element in the iteration.
         *
         * @return the next element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        @Override
        public Item next() {
            if (! hasNext()) try {
                throw new NoSuchFieldException("No more elements in the stack");
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            return items[--current];
        }
    }
}
