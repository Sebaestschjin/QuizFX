package ui.screen;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import model.Answer;
import model.GameState;
import model.Question;
import ui.JavaFXUI;
import ui.Sizer;
import ui.control.HTMLLabel;
import util.Colors;
import util.Style;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Sebastian Stern
 */
public class QuestionScreen extends UIScreen {

	// TODO allow key-bindings to be set by user
	private List<KeyCode> team1Keys = Arrays.asList(KeyCode.Q, KeyCode.E, KeyCode.Y, KeyCode.C);
	//private List<KeyCode> team2Keys = Arrays.asList(KeyCode.NUMPAD7, KeyCode.NUMPAD9, KeyCode.NUMPAD1, KeyCode.NUMPAD3);
	private List<KeyCode> team2Keys = Arrays.asList(KeyCode.U, KeyCode.O, KeyCode.M, KeyCode.PERIOD);

	private GameState gs;

	private Question question;

	private Answer[] answers;

	private ProgressBar progressBar;

	private EventHandler<KeyEvent> inputHandler;

	private DoubleProperty maxWidth = new SimpleDoubleProperty();

	private DoubleProperty questionHeight = new SimpleDoubleProperty();

	private DoubleProperty answerHeight = new SimpleDoubleProperty();

	private HTMLLabel questionLabel;

	private List<Region> answerNodes = new ArrayList<>();

	private List<Region> teamNodes = new ArrayList<>();

	public QuestionScreen(GameState gs, Question question, Answer[] answers) {
		this.gs = gs;
		this.question = question;
		this.answers = answers;
	}

	@Override
	protected Node createUI() {
		maxWidth.bind(scene.widthProperty().subtract(scene.widthProperty().divide(Sizer.BG_RATIO - 1)));
		questionHeight.bind(scene.heightProperty().multiply(0.4));
		answerHeight.bind(scene.heightProperty().multiply(0.28));

		// grid basic layout
		GridPane grid = createGrid();

		// add the question
		Node questionNode = createQuestion(question);
		grid.add(questionNode, 0, 0);

		// add all answers
		for (int i = 0; i < answers.length; ++i) {
			Region answerNode = createAnswer(i);
			answerNodes.add(answerNode);
			grid.add(answerNode, i % 2, 1 + i / 2);
		}

		createTeams(grid);

		// add bottom part
		Node bottom = createBottom();
		grid.add(bottom, 0, 3);

		// set event-handler for inputting the teams answers
		if (!JavaFXUI.DEMO_MODE) {
			inputHandler = event -> giveAnswer(event.getCode());
			scene.addEventHandler(KeyEvent.KEY_PRESSED, inputHandler);
		}

		return grid;
	}

	@Override
	public void unload() {
		if (inputHandler != null) {
			scene.removeEventHandler(KeyEvent.KEY_PRESSED, inputHandler);
		}
	}

	private GridPane createGrid() {
		GridPane pane = new GridPane();

		// set the width of the columns relative to the main screen width
		ColumnConstraints col1 = new ColumnConstraints();
		col1.prefWidthProperty().bind(maxWidth.divide(2));
		ColumnConstraints col2 = new ColumnConstraints();
		col2.prefWidthProperty().bind(maxWidth.divide(2));
		ColumnConstraints col3 = new ColumnConstraints();
		col3.prefWidthProperty().bind(scene.widthProperty().subtract(maxWidth));
		pane.getColumnConstraints().addAll(col1, col2, col3);

		// set the height of the rows relative to the main screen height
		RowConstraints row1 = new RowConstraints();
		row1.maxHeightProperty().bind(questionHeight);
		row1.prefHeightProperty().bind(row1.maxHeightProperty());
		RowConstraints row2 = new RowConstraints();
		row2.maxHeightProperty().bind(answerHeight);
		row2.prefHeightProperty().bind(row2.maxHeightProperty());
		RowConstraints row3 = new RowConstraints();
		row3.maxHeightProperty().bind(scene.heightProperty().multiply(0.05));
		row3.prefHeightProperty().bind(row3.maxHeightProperty());
		pane.getRowConstraints().addAll(row1, row2, row2, row3);

		pane.setHgap(10);
		pane.setVgap(10);
		pane.setPadding(new Insets(10, 10, 10, 10));

		return pane;
	}

	private Node createQuestion(Question question) {
		VBox content = new VBox();
		content.setAlignment(Pos.CENTER);

		questionLabel = new HTMLLabel(question.getQuestionText());
		questionLabel.setTextAlignment(TextAlignment.CENTER);
		questionLabel.setMaxHeight(questionHeight);
		questionLabel.setImage(question.getQuestionImageFile());
		sizer.font(questionLabel, Sizer.FONT_RATIO_GENERAL * 1.5);

		content.getChildren().add(questionLabel);
		content.getStyleClass().add("question");
		setBackground(content, Colors.CURRY);
		questionLabel.setTextFill(Color.WHITE);

		if (JavaFXUI.DEMO_MODE) {
			content.setOnMouseClicked(event -> showAnswer(-1, -1));
		}

		GridPane.setColumnSpan(content, 2);

		return content;
	}

	private Region createAnswer(final int index) {
		VBox content = new VBox();
		content.setAlignment(Pos.CENTER);

		Answer answer = answers[index];
		HTMLLabel answerLabel = new HTMLLabel(answer.getText());
		answerLabel.setTextFill(Color.WHITE);
		answerLabel.setTextAlignment(TextAlignment.CENTER);
		answerLabel.setMaxHeight(answerHeight);
		sizer.font(answerLabel);

		setBackground(content, Colors.PETROL);

		content.getStyleClass().add(Style.ANSWER);
		content.getChildren().add(answerLabel);
		return content;
	}

	private void createTeams(GridPane grid) {
		createTeam(grid, 0);
		createTeam(grid, 1);
	}

	private void createTeam(GridPane grid, int team) {
		boolean team1 = team == 0;

		Pane content = new StackPane();
		Label teamLabel = new Label(gs == null ? "Horst Schmorst" : gs.getTeam(team1).getName());
		teamLabel.setTextFill(Color.WHITE);

		GridPane.setHalignment(teamLabel, HPos.RIGHT);
		sizer.font(teamLabel, Sizer.FONT_RATIO_GENERAL * 0.7);
		content.getStyleClass().add(Style.EMPTY_TEAM);
		content.maxHeightProperty().bind(answerNodes.get(0).heightProperty().multiply(0.15));
		// specific settings per team
		if (team1) {
			GridPane.setValignment(content, VPos.BOTTOM);
			grid.add(content, 2, 1);
		} else {
			GridPane.setValignment(content, VPos.TOP);
			grid.add(content, 2, 2);
		}

		content.getChildren().add(teamLabel);
		teamNodes.add(content);
	}

	private Node createBottom() {
		StackPane pane = new StackPane();

		// add progress bar for remaining time
		progressBar = new ProgressBar();
		progressBar.prefWidthProperty().bind(pane.widthProperty());
		progressBar.prefHeightProperty().bind(pane.heightProperty().divide(2));
		setBackground(progressBar, Colors.PETROL);
		pane.getChildren().add(progressBar);

		GridPane.setColumnSpan(pane, 2);

		return pane;
	}

	public void updateTime(long remaining, long total) {
		double percentage = (double) remaining / (double) total;
		progressBar.progressProperty().set(percentage);
	}

	public void showAnswer(int team1Answer, int team2Answer) {
		// remove key listener
		if (inputHandler != null) {
			scene.removeEventHandler(KeyEvent.KEY_PRESSED, inputHandler);
		}

		// show solution
		questionLabel.setText(question.getAnswerText());
		questionLabel.setImage(question.getAnswerImageFile());
		EventHandler<MouseEvent> handler = event -> showSolution();
		questionLabel.getParent().setOnMouseClicked(handler);

		// show correct answer
		for (int i = 0; i < answers.length; ++i) {
			if (answers[i].isCorrect()) {
				setBackground(answerNodes.get(i), Colors.GREEN);
				break;
			}
		}

		// move labels to chosen answers
		moveLabel(0, team1Answer);
		moveLabel(1, team2Answer);
	}

	private void showSolution() {
		if (!JavaFXUI.DEMO_MODE) {
			controller.solutionScreenDismissed();
		}
	}

	private void giveAnswer(KeyCode code) {
		int index = team1Keys.indexOf(code);
		if (index != -1) {
			giveAnswer(0);
			controller.team1AnswerEntered(index);
		}
		index = team2Keys.indexOf(code);
		if (index != -1) {
			giveAnswer(1);
			controller.team2AnswerEntered(index);
		}
	}

	private void giveAnswer(int team){
		setBackground(teamNodes.get(team), Colors.team(team == 0));
	}

	private void moveLabel(int team, int answer) {
		if (answer < 0)
			return;

		final int offset = 10;

		// calculate current bounds
		Region have = teamNodes.get(team);
		Bounds haveBounds = have.localToScene(have.getBoundsInLocal());
		Node want = answerNodes.get(answer);
		Bounds wantBounds = want.localToScene(want.getBoundsInLocal());

		// x depends on the team, whether we want to move to the left or right side
		double diffX;
		if (team == 0) {
			diffX = haveBounds.getMinX() - wantBounds.getMinX() - offset;
		} else {
			diffX = haveBounds.getMaxX() - wantBounds.getMaxX() + offset;
		}

		// y diff is always the same
		double diffY = haveBounds.getMinY() - wantBounds.getMinY();
		diffY -= offset;

		final Timeline timeline = new Timeline();
		final KeyValue xValue = new KeyValue(have.translateXProperty(), -diffX);
		final KeyValue yValue = new KeyValue(have.translateYProperty(), -diffY);
		final KeyFrame kf = new KeyFrame(Duration.millis(500), xValue, yValue);
		timeline.getKeyFrames().add(kf);
		timeline.play();
	}
}
