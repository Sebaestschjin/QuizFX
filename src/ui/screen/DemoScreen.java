package ui.screen;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import model.Answer;
import model.Category;
import model.GameState;
import model.Question;
import ui.JavaFXUI;

import java.util.List;

/**
 * @author Sebastian Stern
 */
public class DemoScreen extends UIScreen {

	private JavaFXUI ui;

	private GameState gs;

	private List<Question> demoQuestions;

	private int curQuestion = 0;

	public DemoScreen(JavaFXUI ui, GameState gs) {
		super();
		this.gs = gs;
		this.ui = ui;
	}

	@Override
	protected Pane createUI() {
		TilePane content = new TilePane();
		content.setHgap(20);
		content.setVgap(20);
		content.setAlignment(Pos.CENTER);
		content.setPrefColumns(4);

		for (int i = 0; i < gs.getCategoryCount(); ++i) {
			Category cat = gs.getCategory(i);

			Button test = new Button(cat.getTitle());
			content.getChildren().add(test);
			sizer.font(test);
			int finalI = i;
			test.setOnMouseClicked(event -> startDemo(finalI));
		}

		return content;
	}

	private void startDemo(int category) {
		demoQuestions = gs.getCategory(category).getQuestions();
		curQuestion = 0;

		showQuestion();
	}

	private void showQuestion() {
		if (curQuestion >= demoQuestions.size()) {
			ui.loadScreen(this);
			return;
		}

		Question q = demoQuestions.get(curQuestion);
		ui.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.SECONDARY)
				showQuestion();
		});
		ui.showQuestion(gs, q, q.getAnswers().toArray(new Answer[] {}));
		curQuestion++;
	}
}
