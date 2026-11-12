import student.TestCase;
import java.util.ArrayList;

public class PartyQueueTest extends TestCase {

    private PartyQueue party;

    /**
     * Sets up a brand new party before each test method.
     */
    public void setUp() {
        party = new PartyQueue();
    }

    /**
     * Tests a new party: nothing playing, empty queue, full countdown.
     */
    public void testNewParty() {
        assertNull(party.getNowPlaying());
        assertEquals(0, party.getUpNext().size());
        assertEquals(10, party.getSecondsUntilNext());
        assertEquals(0, party.getHistory().size());
    }

    /**
     * Tests that a valid request returns ADDED and is last in Up Next.
     */
    public void testAddRequestNormal() {
        assertEquals(RequestResult.ADDED,
            party.addRequest("Tuan", "Levitating"));
        assertEquals(RequestResult.ADDED,
            party.addRequest("Toshi", "Blinding Lights"));

        ArrayList<Song> upNext = party.getUpNext();
        assertEquals(2, upNext.size());
        assertEquals("Blinding Lights", upNext.get(1).getTitle());
        assertEquals("Toshi", upNext.get(1).getGuestName());
    }

    /**
     * Tests that a duplicate, a 3rd song from one guest, and blank input
     * are each refused and leave the queue unchanged.
     */
    public void testAddRequestRefused() {
        party.addRequest("Tuan", "Levitating");
        party.addRequest("Tuan", "Uptown Funk");

        assertEquals(RequestResult.DUPLICATE,
            party.addRequest("Mehad", "  LEVITATING  "));
        assertEquals(RequestResult.GUEST_LIMIT,
            party.addRequest(" tuan ", "Blinding Lights"));
        assertEquals(RequestResult.BLANK_INPUT,
            party.addRequest("   ", "Blinding Lights"));
        assertEquals(RequestResult.BLANK_INPUT,
            party.addRequest("Mehad", null));

        ArrayList<Song> upNext = party.getUpNext();
        assertEquals(2, upNext.size());
        assertEquals("Levitating", upNext.get(0).getTitle());
        assertEquals("Uptown Funk", upNext.get(1).getTitle());
    }

    /**
     * Tests that the song playing counts as a duplicate, and that the
     * guest limit counts only waiting songs.
     */
    public void testAddRequestWhilePlaying() {
        party.addRequest("Tuan", "Levitating");
        party.addRequest("Tuan", "Uptown Funk");
        party.playNext();

        assertEquals(RequestResult.DUPLICATE,
            party.addRequest("Ehren", "levitating"));
        assertEquals(RequestResult.ADDED,
            party.addRequest("Tuan", "Blinding Lights"));
        assertEquals(2, party.getUpNext().size());
    }

    /**
     * Tests countSongsBy, ignoring case and spaces, with null or blank
     * names returning 0.
     */
    public void testCountSongsBy() {
        party.addRequest("Tuan", "Levitating");
        party.addRequest("Tuan", "Uptown Funk");
        party.addRequest("Toshi", "Blinding Lights");

        assertEquals(2, party.countSongsBy("Tuan"));
        assertEquals(2, party.countSongsBy("TUAN"));
        assertEquals(2, party.countSongsBy(" tuan "));
        assertEquals(1, party.countSongsBy("Toshi"));
        assertEquals(0, party.countSongsBy("Mehad"));
        assertEquals(0, party.countSongsBy(null));
        assertEquals(0, party.countSongsBy("   "));
    }

    /**
     * Tests that 10 ticks with 2 songs waiting plays the first song and
     * resets the countdown to 10.
     */
    public void testTickNormal() {
        party.addRequest("Tuan", "Levitating");
        party.addRequest("Toshi", "Blinding Lights");

        for (int i = 0; i < 9; i++) {
            assertFalse(party.tick());
        }
        assertEquals(1, party.getSecondsUntilNext());
        assertNull(party.getNowPlaying());

        assertTrue(party.tick());
        assertEquals("Levitating", party.getNowPlaying().getTitle());
        assertEquals(10, party.getSecondsUntilNext());
        assertEquals(1, party.getUpNext().size());
    }

    /**
     * Tests that 10 ticks with an empty queue and nothing playing throws
     * nothing, returns false, and resets the countdown.
     */
    public void testTickEmpty() {
        boolean changed = false;
        for (int i = 0; i < 10; i++) {
            changed = party.tick() || changed;
        }
        assertFalse(changed);
        assertNull(party.getNowPlaying());
        assertEquals(10, party.getSecondsUntilNext());
    }

    /**
     * Tests that tick returns true when Now Playing clears.
     */
    public void testTickClearsNowPlaying() {
        party.addRequest("Tuan", "Levitating");
        party.playNext();

        for (int i = 0; i < 9; i++) {
            party.tick();
        }
        assertTrue(party.tick());
        assertNull(party.getNowPlaying());
    }

    /**
     * Tests that 3 ticks leave 7 seconds on the countdown.
     */
    public void testGetSecondsUntilNext() {
        party.tick();
        party.tick();
        party.tick();
        assertEquals(7, party.getSecondsUntilNext());
    }

    /**
     * Tests that playNext on ["A", "B"] returns "A", then moves "A" to
     * history when "B" plays.
     */
    public void testPlayNextNormal() {
        party.addRequest("Tuan", "A");
        party.addRequest("Toshi", "B");

        assertEquals("A", party.playNext().getTitle());
        assertEquals("A", party.getNowPlaying().getTitle());

        assertEquals("B", party.playNext().getTitle());
        assertEquals(1, party.getHistory().size());
        assertEquals("A", party.getHistory().get(0).getTitle());
    }

    /**
     * Tests that playNext on an empty queue returns null and clears
     * Now Playing.
     */
    public void testPlayNextEmpty() {
        assertNull(party.playNext());

        party.addRequest("Tuan", "A");
        party.playNext();
        assertNull(party.playNext());
        assertNull(party.getNowPlaying());
        assertEquals(1, party.getHistory().size());
    }

    /**
     * Tests that getUpNext returns a copy in request order and calling it
     * twice dequeues nothing.
     */
    public void testGetUpNext() {
        party.addRequest("Tuan", "A");
        party.addRequest("Toshi", "B");
        party.addRequest("Mehad", "C");

        ArrayList<Song> first = party.getUpNext();
        ArrayList<Song> second = party.getUpNext();
        assertEquals(3, second.size());
        assertEquals("A", second.get(0).getTitle());
        assertEquals("B", second.get(1).getTitle());
        assertEquals("C", second.get(2).getTitle());

        first.clear();
        assertEquals(3, party.getUpNext().size());
    }
}
