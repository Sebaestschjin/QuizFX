package ui.screen;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import model.Category;

/**
 * @author Sebastian Stern
 */
public class CategoryChooserScreen extends UIScreen {

	Category[] categories;

	public CategoryChooserScreen(Category... categories) {
		this.categories = categories;
	}

	@Override
	protected Node createUI() {
		Pane pane = new VBox();

		for (int i = 0; i < categories.length; ++i) {
			final int index = i;
			Category cat = categories[i];
			Button catButton = new Button(cat.getTitle());
			catButton.setBackground(new Background(new BackgroundFill(cat.getColor(), CornerRadii.EMPTY, Insets.EMPTY)));
			catButton.setOnMouseClicked(event ->
				controller.categorySelected(index)
			);
			pane.getChildren().add(catButton);
		}

		return pane;
	}
}
