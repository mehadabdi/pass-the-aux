import java.util.NoSuchElementException;

public class LinkedQueue<T> implements QueueInterface<T> {
    private Node<T> firstNode;
    private Node<T> lastNode;
    private int numberOfEntries;

    public LinkedQueue() {
        firstNode = null;
        lastNode = null;
        numberOfEntries = 0;
    }

    @Override
    public void enqueue(T newEntry) {
        if (newEntry == null) {
            throw new IllegalArgumentException("Cannot enqueue null entry[cite: 3].");
        }
        Node<T> newNode = new Node<>(newEntry);
        if (isEmpty()) {
            firstNode = newNode;
        } else {
            lastNode.next = newNode;
        }
        lastNode = newNode;
        numberOfEntries++;
    }

    @Override
    public T dequeue() {
        T front = peek();
        firstNode = firstNode.next;
        if (firstNode == null) {
            lastNode = null;
        }
        numberOfEntries--;
        return front;
    }

    @Override
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty[cite: 3].");
        }
        return firstNode.data;
    }

    @Override
    public boolean isEmpty() {
        return firstNode == null && numberOfEntries == 0;
    }

    @Override
    public int getSize() {
        return numberOfEntries;
    }

    @Override
    public void clear() {
        firstNode = null;
        lastNode = null;
        numberOfEntries = 0;
    }

    @Override
    public boolean contains(T anEntry) {
        if (anEntry == null) {
            return false;
        }
        boolean found = false;
        Node<T> currentNode = firstNode;
        while (!found && currentNode != null) {
            if (anEntry.equals(currentNode.data)) {
                found = true;
            } else {
                currentNode = currentNode.next;
            }
        }
        return found;
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[numberOfEntries];
        Node<T> currentNode = firstNode;
        for (int i = 0; i < numberOfEntries; i++) {
            result[i] = currentNode.data;
            currentNode = currentNode.next;
        }
        return result;
    }

    // Stretch Methods
    public void enqueueFront(T newEntry) {
        if (newEntry == null) {
            throw new IllegalArgumentException("Cannot enqueue null entry[cite: 3].");
        }
        Node<T> newNode = new Node<>(newEntry, firstNode);
        firstNode = newNode;
        if (lastNode == null) {
            lastNode = newNode;
        }
        numberOfEntries++;
    }

    public boolean remove(T anEntry) {
        if (anEntry == null || isEmpty()) {
            return false;
        }
        if (firstNode.data.equals(anEntry)) {
            dequeue();
            return true;
        }
        Node<T> prevNode = firstNode;
        Node<T> currNode = firstNode.next;
        while (currNode != null) {
            if (currNode.data.equals(anEntry)) {
                prevNode.next = currNode.next;
                if (currNode == lastNode) {
                    lastNode = prevNode;
                }
                numberOfEntries--;
                return true;
            }
            prevNode = currNode;
            currNode = currNode.next;
        }
        return false;
    }

    private static class Node<T> {
        private final T data;
        private Node<T> next;

        private Node(T data) {
            this.data = data;
            this.next = null;
        }

        private Node(T data, Node<T> next) {
            this.data = data;
            this.next = next;
        }
    }
}
