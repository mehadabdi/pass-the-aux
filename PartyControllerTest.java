import student.TestCase;

/**
 * Tests the messages PartyController shows for each request outcome.
 * messageFor is static, so no window or timer is created.
 */
public class PartyControllerTest extends TestCase {

    /**
     * Tests that every RequestResult has its own, non-empty message.
     */
    public void testMessageForEveryResult() {
        assertEquals("Added to the queue!",
            PartyController.messageFor(RequestResult.ADDED));
        assertEquals("Please enter a song and name.",
            PartyController.messageFor(RequestResult.BLANK_INPUT));
        assertEquals("Titles must be 100 characters or fewer.",
            PartyController.messageFor(RequestResult.TITLE_TOO_LONG));
        assertEquals("Titles must contain letters or numbers.",
            PartyController.messageFor(RequestResult.NO_LETTERS_OR_DIGITS));
        assertEquals("That song is already in the queue.",
            PartyController.messageFor(RequestResult.DUPLICATE));
        assertEquals("You already have 2 songs in the queue.",
            PartyController.messageFor(RequestResult.GUEST_LIMIT));
        assertEquals("Song not found.",
            PartyController.messageFor(RequestResult.NOT_FOUND));

        for (RequestResult result : RequestResult.values()) {
            String message = PartyController.messageFor(result);
            assertNotNull(message);
            assertFalse(message.isEmpty());
        }
    }

    /**
     * Tests that a null result gets a generic message instead of crashing.
     */
    public void testMessageForNull() {
        assertEquals("Something went wrong.",
            PartyController.messageFor(null));
    }
}
