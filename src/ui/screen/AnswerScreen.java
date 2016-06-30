package ui.screen;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import model.Question;
import util.Text;

/**
 * @author Sebastian Stern
 */
public class AnswerScreen extends UIScreen {

	private Question question;

	public AnswerScreen(Question question) {
		this.question = question;
	}

	@Override
	protected Node createUI() {
		Pane pane = new VBox();

		Label answer = new Label(question.getAnswerText());
		pane.getChildren().addAll(answer);

		Button next = new Button(Text.NEXT);
		next.setOnMouseClicked(event ->
			controller.solutionScreenDismissed()
		);
		pane.getChildren().add(next);

		return pane;
	}
}
