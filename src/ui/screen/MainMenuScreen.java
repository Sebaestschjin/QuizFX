package ui.screen;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import util.Text;

/**
 * @author Sebastian Stern
 */
public class MainMenuScreen extends UIScreen {

    public MainMenuScreen() {
    }

    @Override
    public Node createUI() {
        VBox pane = new VBox();

        pane.setAlignment(Pos.BASELINE_CENTER);
        pane.setSpacing(20);

        final Button start = new Button(Text.START);
        start.setOnMouseClicked(event ->
                controller.titleScreenDismissed()
        );

        final Button hallOfFame = new Button(Text.HALL_OF_FAME);
        hallOfFame.setOnMouseClicked(event ->
                controller.winnerScreenDismissed()
        );

        /*
        final Button end = new Button(Text.EXIT);
        end.setOnMouseClicked(event ->
                controller.gameEnded()
        );
        */

        pane.getChildren().addAll(start, hallOfFame);

        // --- Test ---
        pane.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
                    final int MARGIN = 20;
                    hallOfFame.setPrefWidth(newValue.getWidth() - MARGIN);
                    hallOfFame.setPrefHeight(newValue.getHeight() / 10 - MARGIN);
                }
        );

        return pane;
    }
}
