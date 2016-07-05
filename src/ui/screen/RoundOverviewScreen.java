package ui.screen;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import model.GameState;
import util.Text;

/**
 * @author Sebastian Stern
 */
public class RoundOverviewScreen extends UIScreen {

	private GameState gameState;
	private int totalRounds;
	private int questionsPerRound;

	private int totalCols;

	public RoundOverviewScreen(GameState gameState, int totalRounds, int questionsPerRound) {
		this.gameState = gameState;
		this.totalRounds = totalRounds;
		this.questionsPerRound = questionsPerRound;

		this.totalCols = questionsPerRound * teams + (teams - 1);
	}

	@Override
	protected Node createUI() {
		GridPane layout = createLayout();

		// team labels
		for (int team = 0; team < teams; ++team) {
			createTeam(team, layout);
		}

		// TODO score overview

		// question results
		for (int round = 0; round < totalRounds; ++round) {
			createRound(round, layout);
		}

		// button to continue
		Button next = new Button(Text.NEXT);
		next.setOnMouseClicked(event ->
				controller.roundOverviewDismissed()
		);
		next.setOnKeyPressed(event -> {
				if (event.getCode() == KeyCode.ENTER)
					controller.roundOverviewDismissed();
			}
		);
		GridPane.setConstraints(next, 0, totalRounds + 1);
		GridPane.setColumnSpan(next, totalCols);
		next.setAlignment(Pos.CENTER);
		sizer.font(next);
		sizer.width(next, 0.5);
		layout.getChildren().add(next);

		return layout;
	}

	private GridPane createLayout() {
		GridPane layout = new GridPane();
		layout.setHgap(15);
		layout.setVgap(15);
		layout.setAlignment(Pos.CENTER);

		for (int col = 0; col < totalCols; col++) {
			ColumnConstraints columnConstraint = new ColumnConstraints();
			columnConstraint.prefWidthProperty().bind(getScreenWidthProperty(1.0/(totalCols + 2)));
			layout.getColumnConstraints().add(columnConstraint);
		}

		return layout;
	}

	private void createTeam(int team, Pane parent) {
		int gap = team * (questionsPerRound + 1);

		Label teamName = new Label(gameState.getTeam(team == 0).getName());
		sizer.font(teamName);
		GridPane.setConstraints(teamName, questionsPerRound / 2 + gap, 0);
		parent.getChildren().add(teamName);
	}

	private void createRound(int round, Pane parent) {

		for (int team = 0; team < teams; team++) {
			createSide(round, team, parent);
		}

		createCategory(round, parent);
	}

	private void createCategory(int round, Pane parent) {
		if (gameState.getRounds().size() > round && gameState.getRounds().get(round).isFinnised()) {
			Label category = new Label(gameState.getCategory(round).getTitle());
			sizer.font(category);
			GridPane.setConstraints(category, questionsPerRound, round + 1);
			parent.getChildren().add(category);
		}
	}

	private void createSide(int round, int team, Pane parent) {
		int gap = team * (questionsPerRound + 1);

		for (int question = 0; question < questionsPerRound; question++) {
			Region box = new Region();
			box.setPrefWidth(50);
			box.setPrefHeight(50);
			Color color;
			if (gameState.getRounds().size() <= round)
				color = Color.GRAY;
			else {
				color = gameState.getRounds().get(round).getTeamResults(team == 0, question) ?
						Color.GREEN : Color.RED;
			}

			box.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
			sizer.size(box, 0.1);

			GridPane.setConstraints(box, question + gap,  round + 1);
			parent.getChildren().add(box);
		}
	}
}
