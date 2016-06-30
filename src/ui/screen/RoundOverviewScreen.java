package ui.screen;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import model.GameState;
import util.Text;

/**
 * @author Sebastian Stern
 */
public class RoundOverviewScreen extends UIScreen {

	private GameState gs;
	private int totalRounds;
	private int questionsPerRoundPerTeam;

	public RoundOverviewScreen(GameState gs, int totalRounds, int questionsPerRoundPerTeam) {
		this.gs = gs;
		this.totalRounds = totalRounds;
		this.questionsPerRoundPerTeam = questionsPerRoundPerTeam;
	}

	@Override
	protected Node createUI() {
		Pane pane = new VBox();

		Button next = new Button(Text.NEXT);
		next.setOnMouseClicked(event ->
				controller.roundOverwiewDismissed()
		);

		pane.getChildren().add(next);

		return pane;
	}
}
