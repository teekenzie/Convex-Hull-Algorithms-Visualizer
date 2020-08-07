package setup;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Queue<Item> implements Iterable<Item> {

    // based on the first in first out principle

    private class Node {
        Item item;
        Node next;
    }

    private int size;
    private Node first, last;

    public Queue()
    {
        size = 0;
        first = null;
        last = null;
    }

    /**
     * @param item the item that wants to be added
     */
    public void enqueue(Item item){
        size++;
        Node tempLast = last;
        last = new Node();
        last.item = item;
        if(isEmpty()) first = last;
        else tempLast.next = last;
    }

    /**
     * @return the oldest item that was inserted
     */
    public Item dequeue() {
        size--;
        Item result = first.item;
        first = first.next;
        if (isEmpty()) last = first;
        return result;
    }

    /**
     * @return whether the queue has any more elements
     */
    public boolean isEmpty() {
        return first == null;
    }

    /**
     * @return the size of the queue
     */
    public int size() {
        return size;
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<Item> iterator() {
        return new queueIterator();
    }

    private class queueIterator implements Iterator<Item> {
        Node current = first;
        /**
         * Returns {@code true} if the iteration has more elements.
         * (In other words, returns {@code true} if {@link #next} would
         * return an element rather than throwing an exception.)
         *
         * @return {@code true} if the iteration has more elements
         */
        @Override
        public boolean hasNext() {
            return current != null;
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
                throw new NoSuchFieldException("No more elements in the queue");
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            Item result = current.item;
            current = current.next;
            return result;
        }
    }
}
