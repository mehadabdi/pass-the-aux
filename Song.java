public class Song {

    private final String title;
    private final String guestName;

    /**
     * Constructs a new Song with trimmed title and guest name.
     * 
     * @param title     the title of the song
     * @param guestName the name of the guest requesting the song
     * @throws IllegalArgumentException if title or guestName is null or blank
     */
    public Song(String title, String guestName) {
        if (title == null || title.trim().isEmpty() || 
            guestName == null || guestName.trim().isEmpty()) {
            throw new IllegalArgumentException("Title and guest name cannot be null or blank.");
        }
        this.title = title.trim();
        this.guestName = guestName.trim();
    }

    /**
     * Gets the song title.
     * 
     * @return the trimmed song title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the name of the guest who requested the song.
     * 
     * @return the trimmed guest name
     */
    public String getGuestName() {
        return guestName;
    }

    /**
     * Checks equality between two Song objects.
     * Two songs are equal when their titles match, ignoring case.
     * 
     * @param obj the object to compare with
     * @return true if the other object is a Song with the same title (case-insensitive)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Song other = (Song) obj;
        return this.title.equalsIgnoreCase(other.title);
    }

    /**
     * Returns a hash code consistent with equals (title, ignoring case).
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return title.toLowerCase().hashCode();
    }

    /**
     * Returns a string representation of the song request.
     *
     * @return formatted string containing title and guest name
     */
    @Override
    public String toString() {
        return title + " requested by " + guestName;
    }
}
