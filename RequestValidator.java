/**
 * Static checks on the raw text a guest types in. Pure text rules only:
 * anything that needs to look at the queue (duplicates, the per-guest
 * limit) lives in PartyQueue instead.
 */
public class RequestValidator {

    /** Longest song title we accept, in characters (after trimming). */
    public static final int MAX_TITLE_LENGTH = 100;

    /**
     * Not meant to be instantiated; every method is static.
     */
    private RequestValidator() {
        // utility class
    }

    /**
     * Trims surrounding whitespace.
     *
     * @param raw the text as typed
     * @return the trimmed text, or null if raw is null or blank
     */
    public static String sanitize(String raw) {
        if (raw == null) {
            return null;
        }
        String trimmed = raw.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return trimmed;
    }

    /**
     * Checks a request against the text rules, in this order: blank input,
     * title length, then whether the title has any letter or digit.
     *
     * @param name  the guest's name as typed
     * @param title the song title as typed
     * @return VALID, or BLANK_INPUT, TITLE_TOO_LONG, or NO_LETTERS_OR_DIGITS
     */
    public static RequestResult validateRequest(String name, String title) {
        String cleanName = sanitize(name);
        String cleanTitle = sanitize(title);
        if (cleanName == null || cleanTitle == null) {
            return RequestResult.BLANK_INPUT;
        }
        if (cleanTitle.length() > MAX_TITLE_LENGTH) {
            return RequestResult.TITLE_TOO_LONG;
        }
        if (!hasLetterOrDigit(cleanTitle)) {
            return RequestResult.NO_LETTERS_OR_DIGITS;
        }
        return RequestResult.VALID;
    }

    /**
     * Checks whether the text contains at least one letter or digit.
     *
     * @param text the text to check (not null)
     * @return true if any character is a letter or digit
     */
    private static boolean hasLetterOrDigit(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (Character.isLetterOrDigit(text.charAt(i))) {
                return true;
            }
        }
        return false;
    }
}
