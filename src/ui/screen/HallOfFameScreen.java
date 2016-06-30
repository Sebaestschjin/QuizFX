package ui.screen;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import model.HallOfFame;
import model.Team;
import util.Text;

/**
 * @author Sebastian Stern
 */
public class HallOfFameScreen extends UIScreen {

	private HallOfFame hallOfFame;

	private Team team1;

	private Team team2;

	public HallOfFameScreen(HallOfFame hallOfFame, Team team1, Team team2) {
		this.hallOfFame = hallOfFame;
		this.team1 = team1;
		this.team2 = team2;
	}

	@Override
	public Node createUI() {
		VBox pane = new VBox();

		Button next = new Button(Text.NEXT);
		next.setOnMouseClicked(event ->
			controller.hallOfFameDismissed()
		);
		pane.getChildren().addAll(next);

		return pane;
	}
}
