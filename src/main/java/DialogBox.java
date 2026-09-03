import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/** One chat bubble: a message label next to a speaker avatar, side by side in an HBox. */
public class DialogBox extends HBox {

    private Label text;
    private ImageView displayPicture;

    /** Builds a bubble showing {@code s} next to avatar {@code i}, aligned to the top-right. */
    public DialogBox(String s, Image i) {
        text = new Label(s);
        displayPicture = new ImageView(i);

        text.setWrapText(true);
        displayPicture.setFitWidth(100.0);
        displayPicture.setFitHeight(100.0);
        this.setAlignment(Pos.TOP_RIGHT);

        this.getChildren().addAll(text, displayPicture);
    }

    /** Mirrors this bubble to the top-left by reversing its children's order and alignment. */
    private void flip() {
        this.setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        FXCollections.reverse(tmp);
        this.getChildren().setAll(tmp);
    }

    /** Returns a bubble for a message the user typed, aligned to the top-right. */
    public static DialogBox getUserDialog(String s, Image i) {
        return new DialogBox(s, i);
    }

    /** Returns a bubble for a Maggi Goreng Ayam response, aligned to the top-left. */
    public static DialogBox getMaggiGorengAyamDialog(String s, Image i) {
        var db = new DialogBox(s, i);
        db.flip();
        return db;
    }
}
