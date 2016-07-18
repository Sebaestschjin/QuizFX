package ui.screen;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import model.Settings;
import ui.JavaFXUI;
import util.Text;

/**
 * @author Sebastian Stern
 */
public class SettingsScreen extends UIScreen {

	private  int row = 0;

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

		// location
		TextField location = new TextField(settings.getLocation());
		add(pane, Text.LOCATION, location);

		// rounds
		Spinner<Integer> rounds = new Spinner<>(3, 8, settings.getRounds());
		add(pane, Text.ROUNDS, rounds);

		// questions per round
		Spinner<Integer> questions = new Spinner<>(2, 6, settings.getQuestionsPerRound());
		add(pane, Text.QUESTIONS_PER_ROUND, questions);

		// time
		Spinner<Integer> time = new Spinner<>(20, 120, settings.getTimeoutMs() / 1000);
		add(pane, Text.TIME, time);

		// strict time
		CheckBox strict = new CheckBox();
		strict.setSelected(settings.isStrictTimeout());
		add(pane, Text.STRICT_TIME, strict);

		// consume questions
		CheckBox consume = new CheckBox();
		consume.setSelected(settings.isConsumeQuestions());
		add(pane, Text.CONSUME, consume);

		// demo mode
		CheckBox demo = new CheckBox();
		demo.setSelected(JavaFXUI.DEMO_MODE);
		add(pane, Text.DEMO, demo);

		// buttons
		Button cancel = new Button(Text.CANCEL);
		cancel.setOnMouseClicked(event ->
				controller.cancelGame()
		);
		sizer.font(cancel);
		sizer.width(cancel, 0.3);
		pane.add(cancel, 0, row);

		Button save = new Button(Text.SAVE);
		save.setOnMouseClicked(event -> {
			settings.setLocation(location.getText());
			settings.setTimeoutMs(time.getValue() * 1000);
			settings.setStrictTimeout(strict.isSelected());
			settings.setQuestionsPerRound(questions.getValue());
			settings.setConsumeQuestions(consume.isSelected());
			settings.setRounds(rounds.getValue());
			controller.settingsScreenDismissed(settings);
			JavaFXUI.DEMO_MODE = demo.isSelected();
		}
		);
		sizer.font(save);
		sizer.width(save, 0.3);
		pane.add(save, 1, row);

		return pane;
	}

	private void add(GridPane grid, String labelText, Node setting) {
		Label text = new Label(labelText);
		sizer.font(text);
		sizer.font(setting);

		grid.add(text, 0, row);
		grid.add(setting, 1, row);

		++row;
	}
}
