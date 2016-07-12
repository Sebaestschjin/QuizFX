package ui.screen;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Translate;
import javafx.util.Duration;
import model.Answer;
import model.GameState;
import model.Question;
import ui.Sizer;
import util.Colors;
import util.Style;

import java.io.File;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Sebastian Stern
 */
public class QuestionScreen extends UIScreen {

	// TODO allow key-bindings to be set by user
	private List<KeyCode> team1Keys = Arrays.asList(KeyCode.Q, KeyCode.E, KeyCode.Y, KeyCode.C);
	private List<KeyCode> team2Keys = Arrays.asList(KeyCode.NUMPAD7, KeyCode.NUMPAD9, KeyCode.NUMPAD1, KeyCode.NUMPAD3);

	private GameState gs;

	private Question question;

	private Answer[] answers;

	private ProgressBar progressBar;

	private EventHandler<KeyEvent> inputHandler;

	private DoubleProperty maxWidth = new SimpleDoubleProperty();

	private Label questionLabel;

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
		inputHandler = event -> giveAnswer(event.getCode());
		scene.addEventHandler(KeyEvent.KEY_PRESSED, inputHandler);

		return grid;
	}

	@Override
	public void unload() {
		scene.removeEventHandler(KeyEvent.KEY_PRESSED, inputHandler);
	}

	private GridPane createGrid() {
		GridPane pane = new GridPane();
		//pane.setGridLinesVisible(true);

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
		row1.percentHeightProperty().bind(scene.heightProperty().multiply(0.5));
		RowConstraints row2 = new RowConstraints();
		row2.percentHeightProperty().bind(scene.heightProperty().multiply(0.4));
		RowConstraints row3 = new RowConstraints();
		row3.percentHeightProperty().bind(scene.heightProperty().multiply(0.1));
		pane.getRowConstraints().addAll(row1, row2, row2, row3);

		pane.setHgap(10);
		pane.setVgap(10);
		pane.setPadding(new Insets(10, 10, 10, 10));

		return pane;
	}

	private Node createQuestion(Question question) {
		Pane content = new StackPane();

		questionLabel = new Label(question.getQuestionText());
		questionLabel.setWrapText(true);
		questionLabel.setTextAlignment(TextAlignment.CENTER);
		sizer.font(questionLabel, Sizer.FONT_RATIO_GENERAL * 1.5);
		File imageFile = question.getQuestionImageFile();
		if (imageFile != null) {
			try {
				String fName = "/" + imageFile.getName();
				questionLabel.setGraphic(new ImageView(new Image(getClass().getResourceAsStream(fName))));
				questionLabel.setContentDisplay(ContentDisplay.TOP);
			} catch (Throwable e) {
				System.out.println("Couldn't load image " + imageFile.getName());
			}
		}

		content.getChildren().add(questionLabel);
		content.getStyleClass().add("question");
		setBackground(content, Colors.PETROL);
		questionLabel.setTextFill(Color.WHITE);

		GridPane.setColumnSpan(content, 2);

		return content;
	}

	private Region createAnswer(final int index) {
		Pane content = new StackPane();

		Answer answer = answers[index];
		Label answerLabel = new Label(answer.getText());
		answerLabel.setTextFill(Color.WHITE);
		answerLabel.setTextAlignment(TextAlignment.CENTER);
		answerLabel.setWrapText(true);
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
		sizer.font(teamLabel, Sizer.FONT_RATIO_GENERAL / 2);
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
		scene.removeEventHandler(KeyEvent.KEY_PRESSED, inputHandler);

		// show solution
		questionLabel.setText(question.getAnswerText());
		EventHandler<MouseEvent> handler = event -> controller.solutionScreenDismissed();
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
		teamNodes.get(team).setBackground(new Background(new BackgroundFill(Colors.team(team == 0), new CornerRadii(10), Insets.EMPTY)));
	}

	private void moveLabel(int team, int answer) {
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
