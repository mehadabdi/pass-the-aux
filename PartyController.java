import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;

/**
 * Mediates between PartyQueue (model) and PartyWindow (view). Every model
 * change starts here (a click or a timer tick); after each one the
 * controller pushes fresh Strings to the view.
 */
public class PartyController {
    private final PartyQueue model;
    private final PartyWindow view;
    private final Timer timer;
    private boolean started;

    /**
     * Wires the Add button and creates the 1-second timer.
     *
     * @param model the party model
     * @param view  the window
     * @throws IllegalArgumentException if either is null
     */
    public PartyController(PartyQueue model, PartyWindow view) {
        if (model == null || view == null) {
            throw new IllegalArgumentException("Model and view are required.");
        }
        this.model = model;
        this.view = view;
        this.timer = new Timer(1000, e -> handleTick());
        view.addAddListener(e -> handleAdd());
        view.addPlayAgainListener(e -> handlePlayAgain());
        view.addRemoveListener(e -> handleRemove());
        started = false;
    }

    /**
     * Starts the timer and shows the first screen. Calling it twice does
     * nothing.
     */
    public void start() {
        if (started) {
            return;
        }
        started = true;
        refresh();
        view.setVisible(true);
        timer.start();
    }

    /**
     * Reads the fields, asks the model to add the request, and shows the
     * result. Fields are cleared only on ADDED.
     */
    public void handleAdd() {
        RequestResult result =
            model.addRequest(view.getNameInput(), view.getTitleInput());
        view.showMessage(messageFor(result));
        if (result == RequestResult.ADDED) {
            view.clearInputs();
        }
        refresh();
    }

    /**
     * Re-requests the song the guest picked in the Played list, under the
     * name in the name field. Every rule is checked by the model.
     */
    public void handlePlayAgain() {
        RequestResult result = model.playAgain(view.getNameInput(),
            view.getSelectedHistoryIndex());
        if (result == RequestResult.NOT_FOUND) {
            view.showMessage("Pick a song from the Played list first.");
        }
        else if (result == RequestResult.BLANK_INPUT) {
            view.showMessage("Type your name first.");
        }
        else {
            view.showMessage(messageFor(result));
        }
        if (result == RequestResult.ADDED) {
            view.clearInputs();
        }
        refresh();
    }

    /**
     * Removes the song the guest picked in Up Next, if it is theirs. The
     * model decides whether the removal is allowed.
     */
    public void handleRemove() {
        boolean removed = model.removeRequest(view.getNameInput(),
            view.getSelectedUpNextIndex());
        if (removed) {
            view.showMessage("Removed your song.");
        }
        else {
            view.showMessage(
                "Type your name and pick one of your own songs in Up Next.");
        }
        refresh();
    }

    /**
     * Advances the model one second and redraws.
     */
    public void handleTick() {
        model.tick();
        refresh();
    }

    /**
     * Turns a model result into the text a guest sees.
     *
     * @param result the outcome from the model
     * @return the message to display
     */
    public static String messageFor(RequestResult result) {
        if (result == null) {
            return "Something went wrong.";
        }
        switch (result) {
            case ADDED:
                return "Added to the queue!";
            case BLANK_INPUT:
                return "Please enter a song and name.";
            case TITLE_TOO_LONG:
                return "Titles must be 100 characters or fewer.";
            case NO_LETTERS_OR_DIGITS:
                return "Titles must contain letters or numbers.";
            case DUPLICATE:
                return "That song is already in the queue.";
            case GUEST_LIMIT:
                return "You already have 2 songs in the queue.";
            case NOT_FOUND:
                return "Song not found.";
            default:
                return "Something went wrong.";
        }
    }

    /**
     * Reads the model's state and pushes it to the view as Strings.
     */
    private void refresh() {
        Song playing = model.getNowPlaying();
        view.showNowPlaying(playing == null ? null : playing.toString());
        List<String> lines = new ArrayList<String>();
        int position = 1;
        for (Song song : model.getUpNext()) {
            lines.add(position + ". " + song);
            position++;
        }
        view.showUpNext(lines);

        List<String> played = new ArrayList<String>();
        for (Song song : model.getHistory()) {
            played.add(song.toString());
        }
        view.showHistory(played);
        view.showCountdown(model.getSecondsUntilNext());
    }
}
