package purpleguy;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private PurpleGuy afton;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/user_fredbear.jpg"));
    private Image aftonImage = new Image(this.getClass().getResourceAsStream("/images/purpleguy.png"));

    /**
     * Initialises the app with Afton's starting dialogues
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        dialogContainer.getChildren().add(
            DialogBox.getAftonDialog(("You look... familiar. Have we met at the Pizzeria?\n"
            + "I'm William. But I suppose you have your own names for me.\n"
            + "Tell me... why are you really here?\n"), aftonImage));
    }

    /** Injects the Afton instance */
    public void setAfton(PurpleGuy ppg) {
        afton = ppg;
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Afton's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = afton.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getAftonDialog(response, aftonImage)
        );
        if (input.toLowerCase().equals("bye")) {
            Platform.exit();
        }
        userInput.clear();
    }
}
