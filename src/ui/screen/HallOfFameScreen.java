package ui.screen;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import main.PersistentState;
import model.HallOfFame;
import model.Team;
import ui.Sizer;
import util.Colors;
import util.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Sebastian Stern
 */
public class HallOfFameScreen extends UIScreen {

	private HallOfFame hallOfFame;

	private Team team1;

	private Team team2;

	private final int maxSize = 10;

	private boolean fromMain;

	private Pagination pages;

	private int entryCount;

	private List<String> locations = new ArrayList<>();

	public HallOfFameScreen(HallOfFame hallOfFame, Team team1, Team team2) {
		this.hallOfFame = hallOfFame;
		this.team1 = team1;
		this.team2 = team2;
		fromMain = team1 == null && team2 == null;

		hallOfFame.getEntries().stream().filter(e -> locations.indexOf(e.getLocation()) == -1).forEach(e -> locations.add(e.getLocation()));
		Collections.sort(locations);
	}

	@Override
	public Pane createUI() {
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(30));

		// title
		Label title = new Label(Text.HALL_OF_FAME);
		pane.setTop(title);
		BorderPane.setAlignment(title, Pos.CENTER);
		sizer.font(title, Sizer.FONT_RATIO_GENERAL * 2);

		// page control
		pages = new Pagination(locations.size() + 1, 0);
		pages.setPageFactory(param -> createHalloOfFame(param == 0 ? null : locations.get(param - 1)));
		sizer.font(pages, Sizer.FONT_RATIO_GENERAL * 0.5);
		pane.setCenter(pages);

		int initialPage = fromMain ? 0 : locations.indexOf(PersistentState.settings.getLocation()) + 1;
		pages.setCurrentPageIndex(initialPage);

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

	/**
	 *
	 * @param location	if {@code null} global hall of fame is created
	 * @return
	 */
	private Node createHalloOfFame(String location) {
		// center list
		GridPane center = new GridPane();
		center.setHgap(20);
		center.setVgap(10);
		center.setAlignment(Pos.CENTER);

		// add name of the location or global
		javafx.scene.text.Text thisLocation = new javafx.scene.text.Text(location == null ? Text.GLOBAL : location);
		sizer.font(thisLocation);
		GridPane.setHalignment(thisLocation, HPos.CENTER);
		center.add(thisLocation, 0, 0, 3, 1);

		entryCount = 1;
		hallOfFame.getEntries().stream().filter(e -> location == null || e.getLocation().equals(location)).limit(maxSize).forEach(entry -> {
			int teamID = entry.getTeam().getId();
			boolean isTeam1 = team1 != null && team1.getId() == teamID;
			boolean isTeam2 = team2 != null && team2.getId() == teamID;

			int col = 0;

			// rank
			Label rank = new Label(entryCount + ".");
			sizer.font(rank);
			center.add(rank, col++, entryCount);

			// achieved points
			Label points = new Label(entry.getPoints() + "");
			sizer.font(points);
			center.add(points, col++, entryCount);

			// name of the team
			Label teamName = new Label(entry.getTeam().getName());
			center.add(teamName, col++, entryCount);
			sizer.font(teamName);

			CornerRadii corners = new CornerRadii(10);
			Insets insets = new Insets(-2, -6, -2, -6);

			if (isTeam1) {
				setBackground(teamName, Colors.team(true), corners, insets);
				teamName.setTextFill(Color.WHITE);
			} else if (isTeam2) {
				setBackground(teamName, Colors.team(false), corners, insets);
				teamName.setTextFill(Color.WHITE);
			}

			// optional location if not global hof
			if (location == null) {
				Label locationLabel = new Label(entry.getLocation());
				sizer.font(locationLabel);
				center.add(locationLabel, col++, entryCount);
			}

			entryCount += 1;
		});

		return center;
	}

}
