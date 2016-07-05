package ui.screen;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import util.Style;
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

        pane.setAlignment(Pos.CENTER);
        pane.setSpacing(20);

		// Title
		Label heading = new Label(Text.APPNAME);
		heading.getStyleClass().add(Style.TITLE);
		sizer.font(heading, 0.05);

        Button start = new Button(Text.START);
        start.setOnMouseClicked(event ->
                controller.titleScreenDismissed()
        );
		sizer.font(start);
		sizer.width(start, 0.5);

        Button end = new Button(Text.EXIT);
        end.setOnMouseClicked(event ->
                scene.getWindow().hide()
        );
		sizer.font(end);
		sizer.width(end, 0.5);

        pane.getChildren().addAll(heading, start, end);

        return pane;
    }
}
