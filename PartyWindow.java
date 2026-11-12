import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 * Thin view: shows text and reports clicks. Imports no model class.
 */
public class PartyWindow extends JFrame {

    private final JTextField nameField = new JTextField(12);
    private final JTextField titleField = new JTextField(20);
    private final JButton addButton = new JButton("Add");
    private final JLabel nowPlayingLabel = new JLabel("Nothing playing");
    private final JLabel countdownLabel = new JLabel(" ");
    private final JLabel messageLabel = new JLabel(" ");
    private final DefaultListModel<String> upNextModel =
        new DefaultListModel<String>();

    /**
     * Builds the window. It is not shown until setVisible(true).
     */
    public PartyWindow() {
        super("Pass the Aux");
        JPanel input = new JPanel();
        input.add(new JLabel("Name:"));
        input.add(nameField);
        input.add(new JLabel("Song:"));
        input.add(titleField);
        input.add(addButton);

        JPanel status = new JPanel(new GridLayout(3, 1));
        status.add(nowPlayingLabel);
        status.add(countdownLabel);
        status.add(messageLabel);

        add(input, BorderLayout.NORTH);
        add(new JScrollPane(new JList<String>(upNextModel)),
            BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);
        setSize(560, 360);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /** @return the raw name text (may be empty; never validated here) */
    public String getNameInput() {
        return nameField.getText();
    }

    /** @return the raw title text (may be empty; never validated here) */
    public String getTitleInput() {
        return titleField.getText();
    }

    /** Empties both input fields. */
    public void clearInputs() {
        nameField.setText("");
        titleField.setText("");
    }

    /** @param line the song to show, or null for "Nothing playing" */
    public void showNowPlaying(String line) {
        nowPlayingLabel.setText(line == null
            ? "Nothing playing" : "Now Playing: " + line);
    }

    /** @param lines the queue in order; empty shows "Queue is empty" */
    public void showUpNext(List<String> lines) {
        upNextModel.clear();
        if (lines.isEmpty()) {
            upNextModel.addElement("Queue is empty");
        }
        for (String line : lines) {
            upNextModel.addElement(line);
        }
    }

    /** @param seconds seconds until the next song */
    public void showCountdown(int seconds) {
        countdownLabel.setText("Next song in " + seconds + "s");
    }

    /** @param message feedback text for the guest */
    public void showMessage(String message) {
        messageLabel.setText(message);
    }

    /** @param listener called when Add is clicked */
    public void addAddListener(ActionListener listener) {
        addButton.addActionListener(listener);
    }
}
