package ui.screen;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import util.Text;

/**
 * @author Sebastian Stern
 */
public class TeamChooserScreen extends UIScreen {

    public TeamChooserScreen() {
    }

    @Override
    public Node createUI() {
        VBox pane = new VBox();
        TextField team1 = new TextField();
        TextField team2 = new TextField();

        Button nextButton = new Button(Text.NEXT);
        nextButton.setOnMouseClicked(event ->
                controller.teamNamesEntered(team1.getText(), team2.getText())
        );

        Button backButton = new Button(Text.BACK);
        backButton.setOnMouseClicked(event ->
                controller.cancelGame()
        );

        pane.getChildren().addAll(team1, team2, nextButton, backButton);

        return pane;
    }
}
