import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import student.TestCase;

/**
 * Tests for the PartyWindow class.
 */
public class PartyWindowTest extends TestCase
{

    private PartyWindow window;
    private JPanel input;

    /**
     * Creates a brand new window for each test method. The input panel is
     * the first thing added to the content pane.
     */
    public void setUp()
    {
        window = new PartyWindow();
        input = (JPanel)window.getContentPane().getComponent(0);
    }


    /**
     * Both input fields start out empty.
     */
    public void testInputsStartEmpty()
    {
        assertEquals("", window.getNameInput());
        assertEquals("", window.getTitleInput());
    }


    /**
     * The getters return the raw text, and clearInputs empties both fields.
     */
    public void testGettersAndClear()
    {
        ((JTextField)input.getComponent(1)).setText(" Sam ");
        ((JTextField)input.getComponent(3)).setText("Song");
        assertEquals(" Sam ", window.getNameInput());
        assertEquals("Song", window.getTitleInput());

        window.clearInputs();
        assertEquals("", window.getNameInput());
        assertEquals("", window.getTitleInput());
    }


    /**
     * The show methods run without crashing.
     */
    public void testShowMethods()
    {
        window.showNowPlaying("Song - Guest");
        window.showNowPlaying(null);
        window.showUpNext(new ArrayList<String>());
        ArrayList<String> lines = new ArrayList<String>();
        lines.add("Song - Guest");
        window.showUpNext(lines);
        window.showCountdown(10);
        window.showMessage("Refused");
        assertEquals("", window.getNameInput());
    }


    /**
     * Clicking the Add button calls the listener.
     */
    public void testAddListener()
    {
        final int[] clicks = { 0 };
        window.addAddListener(e -> clicks[0]++);
        ((JButton)input.getComponent(4)).doClick();
        assertEquals(1, clicks[0]);
    }
}