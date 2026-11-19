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

    /**
     * Tests that too-long and symbols-only titles are refused through
     * addRequest and leave the queue unchanged.
     */
    public void testAddRequestBadTitles() {
        assertEquals(RequestResult.TITLE_TOO_LONG,
            party.addRequest("Tuan", "a".repeat(101)));
        assertEquals(RequestResult.NO_LETTERS_OR_DIGITS,
            party.addRequest("Tuan", "@@@###"));
        assertEquals(0, party.getUpNext().size());
    }

    /**
     * Tests that Play Again re-requests a played song: it joins the back
     * of Up Next under the new guest's name, and history is unchanged.
     */
    public void testPlayAgainNormal() {
        party.addRequest("Tuan", "Levitating");
        party.addRequest("Toshi", "Uptown Funk");
        party.playNext();
        party.playNext();
        party.addRequest("Ehren", "Hey Ya");

        assertEquals(RequestResult.ADDED, party.playAgain("Mehad", 0));

        ArrayList<Song> upNext = party.getUpNext();
        assertEquals(2, upNext.size());
        assertEquals("Hey Ya", upNext.get(0).getTitle());
        assertEquals("Levitating", upNext.get(1).getTitle());
        assertEquals("Mehad", upNext.get(1).getGuestName());
        assertEquals(1, party.getHistory().size());
    }

    /**
     * Tests that a replayed song plays again and goes back into history.
     */
    public void testPlayAgainPlaysThrough() {
        party.addRequest("Tuan", "Levitating");
        party.playNext();
        party.playNext();
        party.playAgain("Mehad", 0);

        assertEquals("Levitating", party.playNext().getTitle());
        party.playNext();
        assertEquals(2, party.getHistory().size());
        assertEquals("Levitating", party.getHistory().get(1).getTitle());
    }

    /**
     * Tests that a bad history index returns NOT_FOUND: nothing picked
     * (-1), past the end, and an empty history.
     */
    public void testPlayAgainBadIndex() {
        assertEquals(RequestResult.NOT_FOUND, party.playAgain("Mehad", 0));

        party.addRequest("Tuan", "Levitating");
        party.playNext();
        party.playNext();
        assertEquals(RequestResult.NOT_FOUND, party.playAgain("Mehad", -1));
        assertEquals(RequestResult.NOT_FOUND, party.playAgain("Mehad", 1));
        assertEquals(0, party.getUpNext().size());
    }

    /**
     * Tests that Play Again follows the normal rules: blank or null name,
     * a song already playing or waiting, and the 2-song guest limit.
     */
    public void testPlayAgainRefused() {
        party.addRequest("Tuan", "Levitating");
        party.addRequest("Toshi", "Uptown Funk");
        party.playNext();
        party.playNext();
        // History: [Levitating]; Now Playing: Uptown Funk

        assertEquals(RequestResult.BLANK_INPUT, party.playAgain("   ", 0));
        assertEquals(RequestResult.BLANK_INPUT, party.playAgain(null, 0));

        party.playNext();
        // History: [Levitating, Uptown Funk]; nothing playing
        assertEquals(RequestResult.ADDED, party.playAgain("Mehad", 0));
        assertEquals(RequestResult.DUPLICATE, party.playAgain("Ehren", 0));

        party.addRequest("Mehad", "Hey Ya");
        assertEquals(RequestResult.GUEST_LIMIT,
            party.playAgain(" MEHAD ", 1));
        assertEquals(2, party.getUpNext().size());
    }

    /**
     * Tests that a song in history cannot be replayed while it is the song
     * currently playing.
     */
    public void testPlayAgainWhilePlaying() {
        party.addRequest("Tuan", "Levitating");
        party.playNext();
        party.playNext();
        party.playAgain("Mehad", 0);
        party.playNext();
        // Levitating is playing again and is also history[0]
        assertEquals(RequestResult.DUPLICATE, party.playAgain("Ehren", 0));
    }
}
