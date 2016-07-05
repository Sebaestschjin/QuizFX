package ui.screen;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import ui.ControllerCallback;
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
		pane.getChildren().add(heading);

		// Buttons showing the next screen
		addButton(pane, Text.START, ControllerCallback.TitleScreenOption.START_GAME);
		addButton(pane, Text.SETTINGS, ControllerCallback.TitleScreenOption.EDIT_SETTINGS);
		addButton(pane, Text.HALL_OF_FAME, ControllerCallback.TitleScreenOption.SHOW_HALL_OF_FAME);

        Button end = new Button(Text.EXIT);
        end.setOnMouseClicked(event ->
                scene.getWindow().hide()
        );
		sizer.font(end);
		sizer.width(end, 0.5);

        pane.getChildren().add(end);

        return pane;
    }

	private void addButton(Pane parent, String text, final ControllerCallback.TitleScreenOption dismissOption) {
		Button butt = new Button(text);
		butt.setOnMouseClicked(event ->
				controller.titleScreenDismissed(dismissOption)
		);
		sizer.font(butt);
		sizer.width(butt, 0.5);

		parent.getChildren().add(butt);
	}
}
