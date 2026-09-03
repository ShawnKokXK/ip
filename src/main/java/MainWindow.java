import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/** Controller for the main chat window, wired up from {@code view/MainWindow.fxml}. */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private MaggiGorengAyamBot bot;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/brother.png"));
    private Image maggigorengayamImage = new Image(this.getClass()
            .getResourceAsStream("/images/maggigorengayam.png"));

    /** Called by the FXML loader once the view's fields are injected; keeps the view scrolled to the bottom. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the bot instance and shows its startup message as the first chat bubble. */
    public void setBot(MaggiGorengAyamBot bot) {
        this.bot = bot;
        dialogContainer.getChildren().addAll(
                DialogBox.getMaggiGorengAyamDialog(bot.getStartupMessage(), maggigorengayamImage));
    }

    /**
     * Creates dialog boxes echoing the user's input and the bot's reply, appends them to the
     * dialog container, and clears the input field. Exits the application if the bot signals
     * that the last command (e.g. {@code bye}) should end the session.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }
        String response = bot.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getMaggiGorengAyamDialog(response, maggigorengayamImage)
        );
        userInput.clear();
        if (bot.isExit()) {
            Platform.exit();
        }
    }
}
