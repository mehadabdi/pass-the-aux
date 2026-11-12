public interface QueueInterface<T> {
    void enqueue(T newEntry);
    T dequeue();
    T peek();
    boolean isEmpty();
    int getSize();
    void clear();
    boolean contains(T anEntry);
    Object[] toArray();
}
