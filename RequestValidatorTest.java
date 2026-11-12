import student.TestCase;

public class RequestValidatorTest extends TestCase {

    /**
     * Tests that sanitize trims surrounding spaces.
     */
    public void testSanitizeNormal() {
        assertEquals("Levitating", RequestValidator.sanitize("  Levitating "));
        assertEquals("Uptown Funk", RequestValidator.sanitize("Uptown Funk"));
    }

    /**
     * Tests that sanitize returns null for null, empty, and spaces-only text.
     */
    public void testSanitizeBlank() {
        assertNull(RequestValidator.sanitize(null));
        assertNull(RequestValidator.sanitize(""));
        assertNull(RequestValidator.sanitize("   "));
    }

    /**
     * Tests that a normal request is VALID, including titles with digits,
     * symbols mixed with letters, and a title exactly at the limit.
     */
    public void testValidateRequestValid() {
        assertEquals(RequestResult.VALID,
            RequestValidator.validateRequest("Tuan", "Levitating"));
        assertEquals(RequestResult.VALID,
            RequestValidator.validateRequest("Toshi", "22"));
        assertEquals(RequestResult.VALID,
            RequestValidator.validateRequest("Ehren", "!!Hey Ya!!"));

        String exactly100 = "a".repeat(RequestValidator.MAX_TITLE_LENGTH);
        assertEquals(RequestResult.VALID,
            RequestValidator.validateRequest("Mehad", exactly100));
    }

    /**
     * Tests that blank, spaces-only, or null name or title is BLANK_INPUT.
     */
    public void testValidateRequestBlank() {
        assertEquals(RequestResult.BLANK_INPUT,
            RequestValidator.validateRequest("", "Levitating"));
        assertEquals(RequestResult.BLANK_INPUT,
            RequestValidator.validateRequest("Tuan", "   "));
        assertEquals(RequestResult.BLANK_INPUT,
            RequestValidator.validateRequest(null, "Levitating"));
        assertEquals(RequestResult.BLANK_INPUT,
            RequestValidator.validateRequest("Tuan", null));
    }

    /**
     * Tests that a 101-character title is TITLE_TOO_LONG, and that
     * surrounding spaces do not count toward the limit.
     */
    public void testValidateRequestTooLong() {
        String tooLong = "a".repeat(RequestValidator.MAX_TITLE_LENGTH + 1);
        assertEquals(RequestResult.TITLE_TOO_LONG,
            RequestValidator.validateRequest("Tuan", tooLong));

        String paddedButOk = "   " + "a".repeat(100) + "   ";
        assertEquals(RequestResult.VALID,
            RequestValidator.validateRequest("Tuan", paddedButOk));
    }

    /**
     * Tests that a symbols-only title is NO_LETTERS_OR_DIGITS.
     */
    public void testValidateRequestSymbolsOnly() {
        assertEquals(RequestResult.NO_LETTERS_OR_DIGITS,
            RequestValidator.validateRequest("Tuan", "@@@###"));
        assertEquals(RequestResult.NO_LETTERS_OR_DIGITS,
            RequestValidator.validateRequest("Tuan", " !?. "));
    }
}
