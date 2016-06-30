package ui.screen;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import model.Answer;
import model.Question;
import util.Resource;

import java.io.File;

/**
 * @author Sebastian Stern
 */
public class QuestionScreen extends UIScreen {

	private Question question;

	private Answer[] answers;

	private ProgressBar progressBar;

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

		return pane;
	}

	private GridPane createGrid() {
		GridPane pane = new GridPane();
		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(50);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPercentWidth(50);
		pane.getColumnConstraints().addAll(col1, col2);

		RowConstraints row1 = new RowConstraints();
		row1.setPercentHeight(40);
		RowConstraints row2 = new RowConstraints();
		row2.setPercentHeight(25);
		RowConstraints row3 = new RowConstraints();
		row3.setPercentHeight(2);
		pane.getRowConstraints().addAll(row1, row2, row2, row3);

		pane.setHgap(10);
		pane.setVgap(10);
		pane.setPadding(new Insets(10, 10, 10, 10));

		return pane;
	}

	private Node createQuestion(Question question) {
		Pane content = new StackPane();

		Label questionLabel = new Label(question.getQuestionText());
		File imageFile = question.getQuestionImageFile();
		if (imageFile != null) {
			String fName = "/" +imageFile.getName();
			questionLabel.setGraphic(new ImageView(new Image(getClass().getResourceAsStream(fName))));
			questionLabel.setContentDisplay(ContentDisplay.TOP);
		}
		questionLabel.setAlignment(Pos.CENTER);

		content.getChildren().add(questionLabel);
		content.getStyleClass().add("question");

		return content;
	}


	private Node createAnswer(Answer answer, final int index) {
		Pane content = new StackPane();

		Label answerLabel = new Label(answer.getText());
		answerLabel.setAlignment(Pos.CENTER);

		answerLabel.setOnMouseClicked(event -> {
					controller.team1AnswerEntered(index);
					controller.team2AnswerEntered(index);
				}
		);

		content.getStyleClass().add("answer_" + index);
		content.getChildren().add(answerLabel);
		return content;
	}

	public void updateTime(long remaining, long total) {
		double percentage = (double) remaining / (double) total;
		progressBar.progressProperty().set(percentage);
	}
}
