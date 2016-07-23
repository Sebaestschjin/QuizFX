package ui.screen;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import model.GameState;
import ui.Sizer;
import util.Colors;
import util.Text;

/**
 * @author Sebastian Stern
 */
public class RoundOverviewScreen extends UIScreen {

	private GameState gameState;
	private int totalRounds;
	private int questionsPerRound;

	private int totalCols;
	private boolean handleAsWinnerScreen;

	private DoubleProperty maxWidth = new SimpleDoubleProperty();

	public RoundOverviewScreen(GameState gameState, int totalRounds, int questionsPerRound) {
		this.gameState = gameState;
		this.totalRounds = totalRounds;
		this.questionsPerRound = questionsPerRound;

		this.totalCols = questionsPerRound * teams + (teams - 1);

		handleAsWinnerScreen = gameState.getRounds().size() == totalRounds;
	}

	@Override
	protected Pane createUI() {
		maxWidth.bind(scene.widthProperty().subtract(scene.widthProperty().divide(Sizer.BG_RATIO - 1)));

		BorderPane p = new BorderPane();

		GridPane layout = createLayout();

		// team labels
		for (int team = 0; team < teams; ++team) {
			createTeam(team, layout);
		}

		// question results
		for (int round = 0; round < totalRounds; ++round) {
			createRound(round, layout);
		}

		// button to continue
		Button next = new Button(Text.NEXT);
		next.setOnMouseClicked(event ->
				finish()
		);
		next.setOnKeyPressed(event -> {
				if (event.getCode() == KeyCode.ENTER)
					finish();
			}
		);

		GridPane.setConstraints(next, 0, totalRounds + 1);
		GridPane.setColumnSpan(next, totalCols);
		GridPane.setHalignment(next, HPos.CENTER);
		sizer.font(next);
		sizer.width(next, 0.2);
		layout.getChildren().add(next);

		p.setLeft(layout);
		return p;
	}


	private void finish() {
		if (handleAsWinnerScreen)
			controller.winnerScreenDismissed();
		else
			controller.roundOverviewDismissed();
	}

	private GridPane createLayout() {
		GridPane layout = new GridPane();
		layout.setHgap(15);
		layout.setVgap(15);
		layout.setPadding(new Insets(20, 0, 20, 20));

		for (int col = 0; col < totalCols; col++) {
			ColumnConstraints columnConstraint = new ColumnConstraints();

			if (col == questionsPerRound)
				columnConstraint.prefWidthProperty().bind(maxWidth.divide(totalCols).multiply(2));
			else
				columnConstraint.prefWidthProperty().bind(maxWidth.divide(totalCols));

			layout.getColumnConstraints().add(columnConstraint);
		}

		layout.prefWidthProperty().bind(maxWidth);
		layout.setAlignment(Pos.CENTER);

		return layout;
	}

	private void createTeam(int team, Pane parent) {
		boolean team1 = team == 0;

		Label teamName = new Label(gameState.getTeam(team1).getName());
		teamName.setTextFill(Color.WHITE);
		sizer.font(teamName);

		StackPane pane = new StackPane();
		pane.setPadding(new Insets(10));
		GridPane.setConstraints(pane, team1 ? 0 : questionsPerRound + 2, 0);
		pane.setAlignment(Pos.CENTER);
		GridPane.setColumnSpan(pane, questionsPerRound - 1);
		setBackground(pane, Colors.team(team == 0), new CornerRadii(10));
		pane.getChildren().addAll(teamName);

		Label teamScore = new Label("" + gameState.getTeamPoints(team1));
		sizer.font(teamScore, Sizer.FONT_RATIO_GENERAL * 1.60);
		GridPane.setHalignment(teamScore, team1 ? HPos.RIGHT : HPos.LEFT);
		GridPane.setConstraints(teamScore, team1 ? questionsPerRound - 1 : questionsPerRound + 1, 0);


		parent.getChildren().addAll(pane, teamScore);
	}

	private void createRound(int round, Pane parent) {

		for (int team = 0; team < teams; team++) {
			createSide(round, team, parent);
		}

		createCategory(round, parent);
	}

	private void createCategory(int round, Pane parent) {
		if (gameState.getRounds().size() > round) {
			String title = gameState.getRounds().get(round).getCategory().getTitle();
			Label category = new Label(title);
			category.setWrapText(true);
			category.setTextAlignment(TextAlignment.CENTER);
			sizer.font(category, Sizer.FONT_RATIO_GENERAL * 0.7);
			GridPane.setValignment(category, VPos.CENTER);
			GridPane.setHalignment(category, HPos.CENTER);
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
						Colors.GREEN : Colors.RED;
			}

			box.setBackground(new Background(new BackgroundFill(color, new CornerRadii(10), Insets.EMPTY)));
			sizer.size(box, 0.1);

			GridPane.setConstraints(box, question + gap,  round + 1);
			parent.getChildren().add(box);
		}
	}
}
