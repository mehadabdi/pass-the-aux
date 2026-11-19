import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.BorderFactory;

/**
 * Thin view: shows text and reports clicks. Imports no model class.
 */
public class PartyWindow extends JFrame {

    private final JTextField nameField = new JTextField(10);
    private final JTextField titleField = new JTextField(16);
    private final JButton addButton = new JButton("Add");
    private final JLabel nowPlayingLabel = new JLabel("Nothing playing");
    private final JLabel countdownLabel = new JLabel(" ");
    private final JLabel messageLabel = new JLabel(" ");
    private final DefaultListModel<String> upNextModel =
        new DefaultListModel<String>();
    private final DefaultListModel<String> historyModel =
        new DefaultListModel<String>();
    private final JList<String> historyList =
        new JList<String>(historyModel);
    private final JButton playAgainButton = new JButton("Play Again");
    private final JButton removeButton = new JButton("Remove");
    private final JList<String> upNextList = new JList<String>(upNextModel);

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

        upNextList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane upNextPane = new JScrollPane(upNextList);
        upNextPane.setBorder(BorderFactory.createTitledBorder("Up Next"));

        JPanel upNextPanel = new JPanel(new BorderLayout());
        upNextPanel.add(upNextPane, BorderLayout.CENTER);
        upNextPanel.add(removeButton, BorderLayout.SOUTH);

        historyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane historyPane = new JScrollPane(historyList);
        historyPane.setBorder(BorderFactory.createTitledBorder(
            "Played (pick one, type your name, Play Again)"));
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.add(historyPane, BorderLayout.CENTER);
        historyPanel.add(playAgainButton, BorderLayout.SOUTH);

        JPanel lists = new JPanel(new GridLayout(1, 2));
        lists.add(upNextPanel);
        lists.add(historyPanel);

        add(input, BorderLayout.NORTH);
        add(lists, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);
        setSize(820, 420);
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

    /**
     * Shows the waiting songs in order; an empty list shows
     * "Queue is empty". Rows are replaced only when the text changes, so a
     * guest's pick survives the once-a-second refresh.
     *
     * @param lines the queue in order
     */
    public void showUpNext(List<String> lines) {
        List<String> rows = new ArrayList<String>(lines);
        if (rows.isEmpty()) {
            rows.add("Queue is empty");
        }
        replaceRows(upNextModel, upNextList, rows);
    }

    /**
     * Shows the songs that already played, oldest first. The rows are
     * replaced only when the text changes, so a guest's selection survives
     * the once-a-second refresh.
     *
     * @param lines the played songs in order; may be empty
     */
    public void showHistory(List<String> lines) {
        replaceRows(historyModel, historyList, lines);
    }

    /**
     * Replaces a list's rows only if the text changed. When the rows do
     * change, the old pick is cleared, because the same row number may now
     * point at a different song.
     *
     * @param model the list's rows
     * @param list  the list on screen
     * @param rows  the text to show
     */
    private void replaceRows(DefaultListModel<String> model,
        JList<String> list, List<String> rows) {
        boolean same = rows.size() == model.size();
        for (int i = 0; same && i < rows.size(); i++) {
            same = rows.get(i).equals(model.get(i));
        }
        if (same) {
            return;
        }
        list.clearSelection();
        model.clear();
        for (String row : rows) {
            model.addElement(row);
        }
    }

    /**
     * @return the row the guest picked in the Played list, or -1 if none
     */
    public int getSelectedHistoryIndex() {
        return historyList.getSelectedIndex();
    }

    /** @param seconds seconds until the next song */
    public void showCountdown(int seconds) {
        countdownLabel.setText("Next song in " + seconds + "s");
    }

    /** @param message feedback text for the guest */
    public void showMessage(String message) {
        messageLabel.setText(message);
    }

    /** @param listener called when Add is clicked or Enter is pressed */
    public void addAddListener(ActionListener listener) {
        addButton.addActionListener(listener);
        titleField.addActionListener(listener);
    }

    /** @param listener called when Play Again is clicked */
    public void addPlayAgainListener(ActionListener listener) {
        playAgainButton.addActionListener(listener);
    }
    /**
    * @return the selected row in the Up Next list, or -1 if none
    */
    public int getSelectedUpNextIndex() {
        return upNextList.getSelectedIndex();
    }

    /**
    * @param listener called when Remove is clicked
    */
    public void addRemoveListener(ActionListener listener) {
        removeButton.addActionListener(listener);
    }
}
