package ui.screen;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;
import javafx.util.Pair;
import model.Category;
import model.GameState;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sebastian Stern
 */
public class CategoryChooserScreen extends UIScreen {

	private int unit;
	private int w;
	private int h;

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

		unit = (int) (scene.getWidth() * 0.022);
		w = unit * 8;
		h = unit * 2;

		Group group = new Group();
		group.getChildren().addAll(createBorders());
		group.getChildren().addAll(createCategories());

		String text = gs.getTeam(choosingTeam == 0).getName();
		Text choosing = new Text(text);
		choosing.setTextAlignment(TextAlignment.CENTER);
		choosing.setX(-w/2);
		choosing.setWrappingWidth(w);
		sizer.font(choosing);

		group.getChildren().add(choosing);

		pane.getChildren().add(group);
		return pane;
	}

	private Pair<Shape, Rectangle> createShape(boolean small, int rotation) {
		int addition = small ? 0 : unit;

		Circle circle = new Circle();
		circle.setRadius(w/2 + addition);
		circle.setCenterY(-(w/2 + unit + h));
		circle.setFill(Color.RED);

		Rectangle bottom = new Rectangle();
		bottom.setX(-circle.getRadius());
		bottom.setY(circle.getCenterY());
		bottom.setWidth(circle.getRadius() * 2);
		bottom.setHeight(h);

		Rectangle bounding = new Rectangle();
		bounding.setX(bottom.getX());
		bounding.setY(circle.getCenterY() - circle.getRadius());
		bounding.setWidth(bottom.getWidth());
		bounding.setHeight(circle.getRadius() + bottom.getHeight());

		Rotate r = new Rotate(90 * rotation);
		Shape result = Path.union(circle, bottom);
		result.getTransforms().addAll(r);
		bounding.getTransforms().add(r);

		result = Path.intersect(result, bounding);

		return new Pair<>(result, bounding);
	}

	private Shape createCenter() {
		Rectangle inner = new Rectangle();
		inner.setX(-w/2);
		inner.setY(inner.getX());
		inner.setWidth(w);
		inner.setHeight(inner.getWidth());

		Rectangle outer = new Rectangle();
		outer.setX(inner.getX() - unit);
		outer.setY(outer.getX());
		outer.setWidth(inner.getWidth() + unit * 2);
		outer.setHeight(outer.getWidth());

		return Path.subtract(outer, inner);
	}

	private List<Shape> createCategories() {
		List<Shape> shapes = new ArrayList<>();
		for (int i = 0; i < categories.length; ++i){
			final int cat = i;
			Pair<Shape, Rectangle> created = createShape(true, cat);

			Shape shape = created.getKey();
			shape.setOnMouseClicked(event ->
					controller.categorySelected(cat)
			);
			shape.setFill(categories[cat].getColor());
			shapes.add(shape);

			// category name as text
			Rectangle bounding = created.getValue();
			Text t = new Text(categories[cat].getTitle());
			t.setTextAlignment(TextAlignment.CENTER);
			t.setX(bounding.getX() + unit);
			t.setY(bounding.getY() + unit * 3);
			t.setWrappingWidth(bounding.getWidth() - unit * 2);
			sizer.font(t);
			t.getTransforms().add(new Rotate(i * 90));
			shapes.add(t);
		}

		return shapes;
	}

	private List<Shape> createBorders() {
		List<Shape> shapes = new ArrayList<>();

		Color borderColor = Color.WHEAT;

		for (int i = 0; i < categories.length; ++i) {
			Shape border = Path.subtract(createShape(false, i).getKey(), createShape(true, i).getKey());
			border.setFill(borderColor);
			shapes.add(border);
		}

		Shape center = createCenter();
		center.setFill(borderColor);
		shapes.add(center);

		return shapes;
	}
}
