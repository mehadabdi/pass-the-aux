import student.TestCase;
import java.util.NoSuchElementException;

public class LinkedQueueTest extends TestCase {

    private LinkedQueue<String> queue;

    /**
     * Sets up a brand new empty queue before each test method.
     */
    public void setUp() {
        queue = new LinkedQueue<String>();
    }

    /**
     * Tests normal enqueue behavior and size tracking.
     */
    public void testEnqueueNormal() {
        queue.enqueue("A");
        queue.enqueue("B");
        assertEquals(2, queue.getSize());
        assertFalse(queue.isEmpty());
        assertEquals("A", queue.peek());
    }

    /**
     * Tests bad input for enqueue: null throws IllegalArgumentException and keeps size at 0.
     */
    public void testEnqueueNull() {
        Exception exception = null;
        try {
            queue.enqueue(null);
        }
        catch (IllegalArgumentException e) {
            exception = e;
        }
        assertNotNull(exception);
        assertEquals(0, queue.getSize());
        assertTrue(queue.isEmpty());
    }

    /**
     * Tests normal dequeue behavior.
     */
    public void testDequeueNormal() {
        queue.enqueue("A");
        queue.enqueue("B");
        assertEquals("A", queue.dequeue());
        assertEquals(1, queue.getSize());
        assertEquals("B", queue.peek());
    }

    /**
     * Tests bad input for dequeue on an empty queue throws NoSuchElementException.
     */
    public void testDequeueEmpty() {
        Exception exception = null;
        try {
            queue.dequeue();
        }
        catch (NoSuchElementException e) {
            exception = e;
        }
        assertNotNull(exception);
    }

    /**
     * Tests normal peek behavior without altering the queue size.
     */
    public void testPeekNormal() {
        queue.enqueue("A");
        queue.enqueue("B");
        assertEquals("A", queue.peek());
        assertEquals(2, queue.getSize());
    }

    /**
     * Tests bad input for peek on an empty queue throws NoSuchElementException.
     */
    public void testPeekEmpty() {
        Exception exception = null;
        try {
            queue.peek();
        }
        catch (NoSuchElementException e) {
            exception = e;
        }
        assertNotNull(exception);
    }

    /**
     * Tests isEmpty and clear functionality.
     */
    public void testIsEmptyAndClear() {
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.getSize());

        queue.enqueue("A");
        queue.enqueue("B");
        assertFalse(queue.isEmpty());
        assertEquals(2, queue.getSize());

        queue.clear();
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.getSize());

        // Clearing an empty queue should be safe and throw no exceptions
        Exception exception = null;
        try {
            queue.clear();
        }
        catch (Exception e) {
            exception = e;
        }
        assertNull(exception);
    }

    /**
     * Tests contains functionality, including bad input (null returns false).
     */
    public void testContains() {
        queue.enqueue("A");
        queue.enqueue("B");
        assertTrue(queue.contains("B"));
        assertFalse(queue.contains("C"));
        assertFalse(queue.contains(null));
    }

    /**
     * Tests toArray functionality for both empty and populated queues.
     */
    public void testToArray() {
        Object[] emptyArray = queue.toArray();
        assertNotNull(emptyArray);
        assertEquals(0, emptyArray.length);

        queue.enqueue("A");
        queue.enqueue("B");
        queue.enqueue("C");

        Object[] array = queue.toArray();
        assertNotNull(array);
        assertEquals(3, array.length);
        assertEquals("A", array[0]);
        assertEquals("B", array[1]);
        assertEquals("C", array[2]);
        assertEquals(3, queue.getSize()); // Queue remains unchanged
    }

    /**
     * Tests the stretch method enqueueFront.
     */
    public void testEnqueueFrontStretch() {
        queue.enqueue("B");
        queue.enqueueFront("A");
        assertEquals(2, queue.getSize());
        assertEquals("A", queue.peek());
        assertEquals("A", queue.dequeue());
        assertEquals("B", queue.dequeue());
    }

    /**
     * Tests enqueueFront with null throws IllegalArgumentException.
     */
    public void testEnqueueFrontNull() {
        Exception exception = null;
        try {
            queue.enqueueFront(null);
        }
        catch (IllegalArgumentException e) {
            exception = e;
        }
        assertNotNull(exception);
    }

    /**
     * Tests the stretch method remove.
     */
    public void testRemoveStretch() {
        queue.enqueue("A");
        queue.enqueue("B");
        queue.enqueue("C");

        assertTrue(queue.remove("B"));
        assertEquals(2, queue.getSize());

        Object[] array = queue.toArray();
        assertEquals("A", array[0]);
        assertEquals("C", array[1]);

        assertFalse(queue.remove("Z"));
        assertFalse(queue.remove(null));
        assertEquals(2, queue.getSize());
    }
}
