import student.TestCase;

public class SongTest extends TestCase {

    private Song song1;
    private Song song2;

    /**
     * Sets up test fixtures before each test method.
     */
    public void setUp() {
        song1 = new Song("Levitating", "Tuan");
        song2 = new Song("  Blinding Lights  ", "  Toshi  ");
    }

    /**
     * Tests getters and automatic trimming of title and guest name.
     */
    public void testGettersAndTrimming() {
        assertEquals("Levitating", song1.getTitle());
        assertEquals("Tuan", song1.getGuestName());

        assertEquals("Blinding Lights", song2.getTitle());
        assertEquals("Toshi", song2.getGuestName());
    }

    /**
     * Tests constructor bad inputs with null or blank strings.
     */
    public void testConstructorBadInput() {
        Exception exception = null;
        try {
            new Song(null, "Tuan");
        }
        catch (IllegalArgumentException e) {
            exception = e;
        }
        assertNotNull(exception);

        exception = null;
        try {
            new Song("Levitating", null);
        }
        catch (IllegalArgumentException e) {
            exception = e;
        }
        assertNotNull(exception);

        exception = null;
        try {
            new Song("   ", "Tuan");
        }
        catch (IllegalArgumentException e) {
            exception = e;
        }
        assertNotNull(exception);

        exception = null;
        try {
            new Song("Levitating", "   ");
        }
        catch (IllegalArgumentException e) {
            exception = e;
        }
        assertNotNull(exception);
    }

    /**
     * Tests equals method matching titles ignoring case.
     */
    public void testEquals() {
        Song sameTitleDifferentCase = new Song("levitating", "Mehad");
        Song sameTitleWithSpaces = new Song("  LEVITATING  ", "Ehren");
        Song differentSong = new Song("Uptown Funk", "Tuan");

        assertTrue(song1.equals(song1));
        assertTrue(song1.equals(sameTitleDifferentCase));
        assertTrue(song1.equals(sameTitleWithSpaces));

        assertFalse(song1.equals(differentSong));
        assertFalse(song1.equals(song2));
        assertFalse(song1.equals(null));
        assertFalse(song1.equals("Levitating"));
    }

    /**
     * Tests toString formatting.
     */
    public void testToString() {
        assertEquals("Levitating requested by Tuan", song1.toString());
        assertEquals("Blinding Lights requested by Toshi", song2.toString());
    }
}
