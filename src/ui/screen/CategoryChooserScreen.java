package ui.screen;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import model.Category;
import model.GameState;
import util.Colors;
import util.Text;

/**
 * @author Sebastian Stern
 */
public class CategoryChooserScreen extends UIScreen {

	private Category[] categories;
	private int choosingTeam;
	private GameState gs;

	public CategoryChooserScreen(boolean team1Chooses, GameState gs, Category... categories) {
		this.categories = categories;
		this.choosingTeam = team1Chooses ? 0 : 1;
		this.gs = gs;
	}

	@Override
	protected Node createUI() {
		VBox pane = new VBox();

		pane.setAlignment(Pos.CENTER);
		pane.setSpacing(10);

		Label choosing = new Label();
		choosing.setText(Text.TEAM_CHOOSING(gs.getTeam(choosingTeam == 0).getName()));
		sizer.font(choosing);
		pane.getChildren().add(choosing);

		for (int i = 0; i < categories.length; ++i) {
			final int index = i;
			Category cat = categories[i];
			Color catColor = cat.getColor();
			Button catButton = new Button(cat.getTitle());
			catButton.setBackground(new Background(new BackgroundFill(catColor, CornerRadii.EMPTY, Insets.EMPTY)));
			catButton.setTextFill(Colors.getReadable(catColor));
			catButton.setOnMouseClicked(event ->
				controller.categorySelected(index)
			);
			sizer.font(catButton);
			pane.getChildren().add(catButton);
		}

		return pane;
	}
}
