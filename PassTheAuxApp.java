import javax.swing.SwingUtilities;

/**
 * Entry point: builds the model, view, and controller and starts the app.
 */
public class PassTheAuxApp {

    /**
     * Launches Pass the Aux on the Swing event thread.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PartyController controller =
                new PartyController(new PartyQueue(), new PartyWindow());
            controller.start();
        });
    }
}
