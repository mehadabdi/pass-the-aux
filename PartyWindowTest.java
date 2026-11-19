
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Field;
import javax.swing.JButton;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTextField;
import student.TestCase;

/**
 * Tests for the PartyWindow class.
 */
public class PartyWindowTest extends TestCase {

    private PartyWindow window;

    /**
     * Creates a new PartyWindow before each test.
     */
    public void setUp() {
        window = new PartyWindow();
    }

    /**
     * Retrieves a private field from the PartyWindow for testing.
     *
     * @param fieldName the name of the field
     * @return the value of the requested field
     */
    private Object getPrivateField(String fieldName) {
        try {
            Field field = PartyWindow.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(window);
        }
        catch (Exception e) {
            fail("Could not access field: " + fieldName);
            return null;
        }
    }

    /**
     * Tests that both input fields start empty and return entered text.
     */
    public void testGetInputs() {
        JTextField nameField =
            (JTextField)getPrivateField("nameField");
        JTextField titleField =
            (JTextField)getPrivateField("titleField");

        assertEquals("", window.getNameInput());
        assertEquals("", window.getTitleInput());

        nameField.setText("Alex");
        titleField.setText("Song A");

        assertEquals("Alex", window.getNameInput());
        assertEquals("Song A", window.getTitleInput());
    }

    /**
     * Tests that clearInputs empties both input fields.
     */
    public void testClearInputs() {
        JTextField nameField =
            (JTextField)getPrivateField("nameField");
        JTextField titleField =
            (JTextField)getPrivateField("titleField");

        nameField.setText("Alex");
        titleField.setText("Song A");

        window.clearInputs();

        assertEquals("", window.getNameInput());
        assertEquals("", window.getTitleInput());
    }

    /**
     * Tests that showNowPlaying accepts normal text and null.
     */
    public void testShowNowPlaying() {
        window.showNowPlaying("Song A - Alex");

        assertEquals("Now Playing: Song A - Alex",
            ((javax.swing.JLabel)getPrivateField("nowPlayingLabel"))
                .getText());

        window.showNowPlaying(null);

        assertEquals("Nothing playing",
            ((javax.swing.JLabel)getPrivateField("nowPlayingLabel"))
                .getText());
    }

    /**
     * Tests that showUpNext displays an empty message and song entries.
     */
    public void testShowUpNext() {
        DefaultListModel<String> model =
            (DefaultListModel<String>)getPrivateField("upNextModel");

        window.showUpNext(new ArrayList<String>());

        assertEquals(1, model.size());
        assertEquals("Queue is empty", model.get(0));

        ArrayList<String> songs = new ArrayList<String>();
        songs.add("Song A - Alex");
        songs.add("Song B - Sam");

        window.showUpNext(songs);

        assertEquals(2, model.size());
        assertEquals("Song A - Alex", model.get(0));
        assertEquals("Song B - Sam", model.get(1));
    }

    /**
     * Tests that showHistory displays songs and preserves selection
     * when the displayed text has not changed.
     */
    public void testShowHistory() {
        DefaultListModel<String> model =
            (DefaultListModel<String>)getPrivateField("historyModel");
        JList<String> list =
            (JList<String>)getPrivateField("historyList");

        ArrayList<String> songs = new ArrayList<String>();
        songs.add("Song A - Alex");
        songs.add("Song B - Sam");

        window.showHistory(songs);

        assertEquals(2, model.size());
        assertEquals("Song A - Alex", model.get(0));
        assertEquals("Song B - Sam", model.get(1));

        list.setSelectedIndex(1);

        // Same contents should preserve the selection.
        window.showHistory(songs);
        assertEquals(1, list.getSelectedIndex());

        // Changed contents should update the list.
        songs.add("Song C - Taylor");
        window.showHistory(songs);

        assertEquals(3, model.size());
        assertEquals("Song C - Taylor", model.get(2));
    }

    /**
     * Tests that getSelectedHistoryIndex returns the selected row
     * or -1 when no row is selected.
     */
    public void testGetSelectedHistoryIndex() {
        JList<String> list =
            (JList<String>)getPrivateField("historyList");

        assertEquals(-1, window.getSelectedHistoryIndex());

        ArrayList<String> songs = new ArrayList<String>();
        songs.add("Song A");
        songs.add("Song B");

        window.showHistory(songs);

        list.setSelectedIndex(1);
        assertEquals(1, window.getSelectedHistoryIndex());

        list.clearSelection();
        assertEquals(-1, window.getSelectedHistoryIndex());
    }

    /**
     * Tests that getSelectedUpNextIndex returns the selected row
     * or -1 when no row is selected.
     */
    public void testGetSelectedUpNextIndex() {
        JList<String> list =
            (JList<String>)getPrivateField("upNextList");

        assertEquals(-1, window.getSelectedUpNextIndex());

        ArrayList<String> songs = new ArrayList<String>();
        songs.add("Song A");
        songs.add("Song B");

        window.showUpNext(songs);

        list.setSelectedIndex(1);
        assertEquals(1, window.getSelectedUpNextIndex());

        list.clearSelection();
        assertEquals(-1, window.getSelectedUpNextIndex());
    }

    /**
     * Tests that showCountdown displays the correct countdown.
     */
    public void testShowCountdown() {
        window.showCountdown(10);

        assertEquals("Next song in 10s",
            ((javax.swing.JLabel)getPrivateField("countdownLabel"))
                .getText());

        window.showCountdown(0);

        assertEquals("Next song in 0s",
            ((javax.swing.JLabel)getPrivateField("countdownLabel"))
                .getText());
    }

    /**
     * Tests that showMessage displays the provided message.
     */
    public void testShowMessage() {
        window.showMessage("Song added");

        assertEquals("Song added",
            ((javax.swing.JLabel)getPrivateField("messageLabel"))
                .getText());

        window.showMessage(null);

        assertNull(
            ((javax.swing.JLabel)getPrivateField("messageLabel"))
                .getText());
    }

    /**
     * Tests that the Add button and title field trigger the Add listener.
     */
    public void testAddAddListener() {
        final int[] clicks = { 0 };

        window.addAddListener(e -> clicks[0]++);

        JButton addButton =
            (JButton)getPrivateField("addButton");
        JTextField titleField =
            (JTextField)getPrivateField("titleField");

        addButton.doClick();
        assertEquals(1, clicks[0]);

        titleField.postActionEvent();
        assertEquals(2, clicks[0]);
    }

    /**
     * Tests that clicking Play Again triggers its listener.
     */
    public void testAddPlayAgainListener() {
        final int[] clicks = { 0 };

        window.addPlayAgainListener(e -> clicks[0]++);

        JButton button =
            (JButton)getPrivateField("playAgainButton");

        button.doClick();

        assertEquals(1, clicks[0]);
    }

    /**
     * Tests that clicking Remove triggers its listener.
     */
    public void testAddRemoveListener() {
        final int[] clicks = { 0 };

        window.addRemoveListener(e -> clicks[0]++);

        JButton button =
            (JButton)getPrivateField("removeButton");

        button.doClick();

        assertEquals(1, clicks[0]);
    }
}
