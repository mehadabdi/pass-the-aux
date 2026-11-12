import java.util.ArrayList;

/**
 * Manages the party's song queue: Up Next, Now Playing, the countdown,
 * and the party rules (no duplicates, at most 2 waiting songs per guest).
 */
public class PartyQueue {

    public static final int SONG_INTERVAL_SECONDS = 10;
    public static final int MAX_SONGS_PER_GUEST = 2;

    private final LinkedQueue<Song> upNext;
    private Song nowPlaying;
    private int secondsUntilNext;
    private final ArrayList<Song> history;

    /**
     * Creates an empty party with nothing playing and a full countdown.
     */
    public PartyQueue() {
        upNext = new LinkedQueue<Song>();
        nowPlaying = null;
        secondsUntilNext = SONG_INTERVAL_SECONDS;
        history = new ArrayList<Song>();
    }

    /**
     * Validates a request, checks for duplicates and the per-guest limit,
     * then adds it to the back of Up Next.
     *
     * @param name  the guest's name
     * @param title the song title
     * @return ADDED, or the validator's error, DUPLICATE, or GUEST_LIMIT
     *         (queue unchanged)
     */
    public RequestResult addRequest(String name, String title) {
        RequestResult result = RequestValidator.validateRequest(name, title);
        if (result != RequestResult.VALID) {
            return result;
        }
        Song song = new Song(title, name);
        if (song.equals(nowPlaying) || upNext.contains(song)) {
            return RequestResult.DUPLICATE;
        }
        if (countSongsBy(name) >= MAX_SONGS_PER_GUEST) {
            return RequestResult.GUEST_LIMIT;
        }
        upNext.enqueue(song);
        return RequestResult.ADDED;
    }

    /**
     * Counts down one second. At 0, plays the next song and resets the
     * countdown to SONG_INTERVAL_SECONDS.
     *
     * @return true if Now Playing changed, including when it clears
     */
    public boolean tick() {
        secondsUntilNext--;
        if (secondsUntilNext > 0) {
            return false;
        }
        secondsUntilNext = SONG_INTERVAL_SECONDS;
        Song previous = nowPlaying;
        playNext();
        return nowPlaying != previous;
    }

    /**
     * Moves the current song to history and starts the next one in line.
     *
     * @return the new Now Playing song, or null if the queue was empty
     */
    public Song playNext() {
        if (nowPlaying != null) {
            history.add(nowPlaying);
        }
        if (upNext.isEmpty()) {
            nowPlaying = null;
        } else {
            nowPlaying = upNext.dequeue();
        }
        return nowPlaying;
    }

    /**
     * Gets the song currently playing.
     *
     * @return the current song, or null if nothing is playing
     */
    public Song getNowPlaying() {
        return nowPlaying;
    }

    /**
     * Gets a copy of the waiting songs in request order.
     *
     * @return a new list of waiting songs (empty if none)
     */
    public ArrayList<Song> getUpNext() {
        ArrayList<Song> copy = new ArrayList<Song>();
        for (Object entry : upNext.toArray()) {
            copy.add((Song) entry);
        }
        return copy;
    }

    /**
     * Gets the seconds left before the next song plays.
     *
     * @return a value from 1 to SONG_INTERVAL_SECONDS
     */
    public int getSecondsUntilNext() {
        return secondsUntilNext;
    }

    /**
     * Counts a guest's waiting songs, ignoring case and surrounding spaces.
     *
     * @param guestName the guest's name
     * @return the number of waiting songs, or 0 if the name is null or blank
     */
    public int countSongsBy(String guestName) {
        if (guestName == null || guestName.trim().isEmpty()) {
            return 0;
        }
        String target = guestName.trim();
        int count = 0;
        for (Song song : getUpNext()) {
            if (song.getGuestName().equalsIgnoreCase(target)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Gets a copy of the songs that have already played, oldest first.
     *
     * @return a new list of played songs (empty if none)
     */
    public ArrayList<Song> getHistory() {
        return new ArrayList<Song>(history);
    }
}
