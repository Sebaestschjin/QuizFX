package ui.screen;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Material;
import model.HallOfFame;
import model.Team;
import ui.Sizer;
import util.Text;

/**
 * @author Sebastian Stern
 */
public class HallOfFameScreen extends UIScreen {

	private HallOfFame hallOfFame;

	private Team team1;

	private Team team2;

	private final int maxSize = 10;

	public HallOfFameScreen(HallOfFame hallOfFame, Team team1, Team team2) {
		this.hallOfFame = hallOfFame;
		this.team1 = team1;
		this.team2 = team2;
	}

	@Override
	public Node createUI() {
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(30));

		// title
		Label title = new Label(Text.HALL_OF_FAME);
		pane.setTop(title);
		BorderPane.setAlignment(title, Pos.CENTER);
		sizer.font(title, Sizer.FONT_RATIO_GENERAL * 2);

		// center list
		VBox center = new VBox();
		center.setAlignment(Pos.CENTER);
		pane.setCenter(center);

		int entryCount = 0;
		for (HallOfFame.Entry e : hallOfFame.getEntries()) {
			HBox entry = new HBox();
			entry.setSpacing(20);
			entry.setAlignment(Pos.CENTER);

			Label points = new Label(e.getPoints() + "");
			sizer.font(points);
			Label teamName = new Label(e.getTeam().getName());
			sizer.font(teamName);
			Label location = new Label(e.getLocation());
			sizer.font(location);

			entry.getChildren().addAll(points, teamName, location);
			center.getChildren().add(entry);

			if (++entryCount > maxSize)
				break;
		}

		// back button
		Button back = new Button(Text.BACK);
		back.setOnMouseClicked(event ->
			controller.hallOfFameDismissed()
		);
		sizer.font(back);
		sizer.width(back);
		BorderPane.setAlignment(back, Pos.BOTTOM_CENTER);
		pane.setBottom(back);

		return pane;
	}
}
