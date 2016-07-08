package ui.screen;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
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
		GridPane pane = new GridPane();
		pane.setAlignment(Pos.CENTER);
		pane.setHgap(10);
		pane.setVgap(30);

		int row = 0;

		// location
		Label locationLabel = new Label(Text.LOCATION);
		sizer.font(locationLabel);
		GridPane.setConstraints(locationLabel, 0, row);

		TextField locationEdit = new TextField(settings.getLocation());
		GridPane.setConstraints(locationEdit, 1, row);
		sizer.font(locationEdit);

		++row;
		pane.getChildren().addAll(locationLabel, locationEdit);

		// time
		Label timeLabel = new Label(Text.TIME);
		sizer.font(timeLabel);
		GridPane.setConstraints(timeLabel, 0, row);

		Spinner<Integer> timeSpinner = new Spinner<>(20, 120, settings.getTimeoutMs() / 1000);
		sizer.font(timeSpinner);
		GridPane.setConstraints(timeSpinner, 1, row);

		++row;
		pane.getChildren().addAll(timeLabel, timeSpinner);

		// strict time
		Label strictTimeLabel = new Label(Text.STRICT_TIME);
		sizer.font(strictTimeLabel);
		GridPane.setConstraints(strictTimeLabel, 0, row);

		CheckBox strictTimeCheck = new CheckBox();
		sizer.font(strictTimeCheck);
		GridPane.setConstraints(strictTimeCheck, 1, row);

		++row;
		pane.getChildren().addAll(strictTimeLabel, strictTimeCheck);

		// buttons
		Button cancel = new Button(Text.CANCEL);
		cancel.setOnMouseClicked(event ->
				controller.cancelGame()
		);
		sizer.font(cancel);
		sizer.width(cancel, 0.3);
		GridPane.setConstraints(cancel, 0, row);

		Button save = new Button(Text.SAVE);
		save.setOnMouseClicked(event -> {
			settings.setLocation(locationEdit.getText());
			settings.setTimeoutMs(timeSpinner.getValue() * 1000);
			settings.setStrictTimeout(strictTimeCheck.isSelected());
			controller.settingsScreenDismissed(settings);
		}
		);
		sizer.font(save);
		sizer.width(save, 0.3);
		GridPane.setConstraints(save, 1, row);

		++row;
		pane.getChildren().addAll(save, cancel);

		return pane;
	}
}
