package ui.screen;

import javafx.event.EventHandler;
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
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.TextAlignment;
import model.Answer;
import model.Question;
import ui.Sizer;
import util.Style;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * @author Sebastian Stern
 */
public class QuestionScreen extends UIScreen {

	// TODO allow key-bindings to be set by user
	private List<KeyCode> team1Keys = Arrays.asList(KeyCode.Q, KeyCode.E, KeyCode.Y, KeyCode.C);
	private List<KeyCode> team2Keys = Arrays.asList(KeyCode.NUMPAD7, KeyCode.NUMPAD9, KeyCode.NUMPAD1, KeyCode.NUMPAD3);

	private Question question;

	private Answer[] answers;

	private ProgressBar progressBar;

	private EventHandler<KeyEvent> inputHandler;

	public QuestionScreen(Question question, Answer[] answers) {
		this.question = question;
		this.answers = answers;
	}

	@Override
	protected Node createUI() {
		// grid basic layout
		GridPane pane = createGrid();

		// add the question
		Node questionLabel = createQuestion(question);
		GridPane.setConstraints(questionLabel, 0, 0);
		GridPane.setColumnSpan(questionLabel, 2);
		GridPane.setValignment(questionLabel, VPos.BOTTOM);
		GridPane.setHalignment(questionLabel, HPos.RIGHT);
		pane.getChildren().add(questionLabel);

		// add all answers
		for (int i = 0; i < answers.length; ++i) {
			int col = i % 2;
			int row = 1 + (i / 2);

			Node answerLabel = createAnswer(answers[i], i);
			GridPane.setConstraints(answerLabel, col, row);
			GridPane.setValignment(answerLabel, VPos.CENTER);
			GridPane.setHalignment(answerLabel, HPos.CENTER);
			pane.getChildren().add(answerLabel);
		}

		// add progress bar for remaining time
		progressBar = new ProgressBar();
		GridPane.setColumnSpan(progressBar, 2);
		GridPane.setConstraints(progressBar, 0, 3);
		GridPane.setValignment(progressBar, VPos.BOTTOM);
		GridPane.setHalignment(progressBar, HPos.CENTER);
		progressBar.prefWidthProperty().bind(pane.widthProperty().subtract(10));
		pane.getChildren().add(progressBar);

		// set event-handler for inputting the teams answers
		inputHandler = event -> {
				int index = team1Keys.indexOf(event.getCode());
				if (index != -1)
					controller.team1AnswerEntered(index);
				index = team2Keys.indexOf(event.getCode());
				if (index != -1)
					controller.team2AnswerEntered(index);
		};
		scene.addEventHandler(KeyEvent.KEY_PRESSED, inputHandler);

		return pane;
	}

	@Override
	public void unload() {
		scene.removeEventHandler(KeyEvent.KEY_PRESSED, inputHandler);
	}

	private GridPane createGrid() {
		GridPane pane = new GridPane();

		// set the width of the columns relative to the main screen width
		ColumnConstraints col1 = new ColumnConstraints();
		col1.percentWidthProperty().bind(scene.widthProperty().multiply(0.3));
		ColumnConstraints col2 = new ColumnConstraints();
		col2.percentWidthProperty().bind(scene.widthProperty().multiply(0.3));
		pane.getColumnConstraints().addAll(col1, col2);

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

		Label questionLabel = new Label(question.getQuestionText());
		questionLabel.setWrapText(true);
		questionLabel.setTextAlignment(TextAlignment.CENTER);
		sizer.font(questionLabel, Sizer.FONT_RATIO_GENERAL * 2);
		File imageFile = question.getQuestionImageFile();
		if (imageFile != null) {
			String fName = "/" +imageFile.getName();
			questionLabel.setGraphic(new ImageView(new Image(getClass().getResourceAsStream(fName))));
			questionLabel.setContentDisplay(ContentDisplay.TOP);
		}

		content.getChildren().add(questionLabel);
		content.getStyleClass().add("question");

		return content;
	}


	private Node createAnswer(Answer answer, final int index) {
		Pane content = new StackPane();

		Label answerLabel = new Label(answer.getText());
		answerLabel.setTextAlignment(TextAlignment.CENTER);
		answerLabel.setWrapText(true);
		sizer.font(answerLabel);

		content.getStyleClass().add(Style.ANSWER);
		content.getStyleClass().add(Style.ANSWER(index));
		content.getChildren().add(answerLabel);
		return content;
	}

	public void updateTime(long remaining, long total) {
		double percentage = (double) remaining / (double) total;
		progressBar.progressProperty().set(percentage);
	}
}
