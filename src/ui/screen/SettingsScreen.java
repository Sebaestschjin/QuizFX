package ui.screen;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import model.Settings;
import util.Text;

/**
 * @author Sebastian Stern
 */
public class SettingsScreen extends UIScreen {

	private Settings settings;

	public SettingsScreen(Settings settings) {
		this.settings = settings;
	}

	@Override
	protected Node createUI() {
		VBox pane = new VBox();

		Button back = new Button(Text.BACK);
		back.setOnMouseClicked(event ->
			controller.cancelGame()
		);

		pane.getChildren().add(back);

		return pane;
	}
}
