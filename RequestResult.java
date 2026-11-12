/**
 * Every possible outcome of a song request.
 */
public enum RequestResult {
    VALID,
    ADDED,
    BLANK_INPUT,
    TITLE_TOO_LONG,
    NO_LETTERS_OR_DIGITS,
    DUPLICATE,
    GUEST_LIMIT,
    NOT_FOUND
}
