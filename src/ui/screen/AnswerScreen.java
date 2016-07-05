package ui.screen;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import model.Answer;
import model.Question;
import ui.control.HTMLLabel;
import util.Text;

/**
 * @author Sebastian Stern
 */
public class AnswerScreen extends UIScreen {

	private Question question;

	private Answer[] answers;

	private int[] teamAnswers = new int[teams];

	public AnswerScreen(Question question, Answer[] answers, int team1Entered, int team2Entered) {
		this.question = question;
		this.answers = answers;
		this.teamAnswers[0] = team1Entered;
		this.teamAnswers[1] = team2Entered;
	}

	@Override
	protected Node createUI() {
		Pane layout = createLayout();

		//
		HTMLLabel answer = new HTMLLabel(question.getAnswerText());
		answer.setWrapText(true);
		answer.setTextAlignment(TextAlignment.CENTER);
		sizer.font(answer);

		//
		if (question.getAnswerSource() != null && question.getAnswerSource().isEmpty()) {
			HTMLLabel source = new HTMLLabel(question.getAnswerSource());
			source.setWrapText(true);
			source.setTextAlignment(TextAlignment.CENTER);
		}

		// Bottom line with answers and next
		HBox bottom = new HBox();
		bottom.setSpacing(30);
		bottom.setAlignment(Pos.CENTER);

		bottom.getChildren().add(getTeamLabel(0));
		Button next = new Button(Text.NEXT);
		next.setOnMouseClicked(event ->
				controller.solutionScreenDismissed()
		);
		next.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER)
				controller.solutionScreenDismissed();
		});
		sizer.font(next);

		bottom.getChildren().add(next);
		bottom.getChildren().add(getTeamLabel(1));


		layout.getChildren().addAll(answer, bottom);
		return layout;
	}

	private Pane createLayout() {
		//GridPane layout = new GridPane();
		VBox layout = new VBox();
		layout.setAlignment(Pos.CENTER);
		layout.setSpacing(30);

		return layout;
	}

	private Node getTeamLabel(int team) {
		// TODO request game state to get correct team names
		Label teamLabel = new Label("Team " + (team + 1));

		Color color;
		int teamAnswer = teamAnswers[team];
		if (teamAnswer < 0 || teamAnswer >= answers.length)
			color = Color.GRAY;
		else
			color = answers[teamAnswer].isCorrect() ? Color.GREEN : Color.RED;
		teamLabel.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
		sizer.font(teamLabel);

		return teamLabel;
	}
}
