package ui.screen;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import model.Category;

/**
 * @author Sebastian Stern
 */
public class CategoryChooserScreen extends UIScreen {

	private Category[] categories;

	public CategoryChooserScreen(Category... categories) {
		this.categories = categories;
	}

	@Override
	protected Node createUI() {
		VBox pane = new VBox();

		pane.setAlignment(Pos.CENTER);
		pane.setSpacing(10);

		for (int i = 0; i < categories.length; ++i) {
			final int index = i;
			Category cat = categories[i];
			Button catButton = new Button(cat.getTitle());
			catButton.setBackground(new Background(new BackgroundFill(cat.getColor(), CornerRadii.EMPTY, Insets.EMPTY)));
			catButton.setOnMouseClicked(event ->
				controller.categorySelected(index)
			);
			sizer.font(catButton);
			pane.getChildren().add(catButton);
		}

		return pane;
	}
}
