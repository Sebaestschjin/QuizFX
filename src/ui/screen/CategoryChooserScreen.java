package ui.screen;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import javafx.util.Pair;
import main.Paths;
import model.Category;
import model.GameState;
import util.Colors;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

	private Group graphics;

	public CategoryChooserScreen(boolean team1Chooses, GameState gs, Category... categories) {
		this.categories = categories;
		this.choosingTeam = team1Chooses ? 0 : 1;
		this.gs = gs;
	}

	@Override
	protected Pane createUI() {
		VBox pane = new VBox();
		pane.setAlignment(Pos.CENTER);

		unit = (int) (scene.getWidth() * 0.022);
		w = unit * 8;
		h = unit * 2;

		graphics = new Group();
		graphics.getChildren().addAll(createBorders());
		graphics.getChildren().addAll(createCategories());

		String text = gs.getTeam(choosingTeam == 0).getName();
		Text choosing = new Text(text);
		choosing.setTextAlignment(TextAlignment.CENTER);
		choosing.setX(-w/2);
		choosing.setWrappingWidth(w);
		sizer.font(choosing);

		graphics.getChildren().add(choosing);

		pane.getChildren().add(graphics);
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

			EventHandler<MouseEvent> clickHandler = event -> chooseCategory(cat);

			Shape shape = created.getKey();
			shape.setOnMouseClicked(clickHandler);
			shape.setFill(categories[cat].getColor());
			shapes.add(shape);

			// category name as text
			Rectangle bounding = created.getValue();
			Text t = new Text(categories[cat].getTitle());
			t.setOnMouseClicked(clickHandler);
			t.setTextAlignment(TextAlignment.CENTER);
			t.setX(bounding.getX() + unit);
			t.setY(bounding.getY() + unit * 3);
			t.setWrappingWidth(bounding.getWidth() - unit * 2);
			t.setFill(Colors.getReadable(categories[cat].getColor()));
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

	private void chooseCategory(int category) {
		Node from = graphics;
		ImageView to;

		try {
			File imFile = Paths.asRelativeTo(Paths.resourcesDir, categories[category].getImageFile());
			to = new ImageView(new Image(new BufferedInputStream(new FileInputStream(imFile))));
			to.setPreserveRatio(true);
			to.fitHeightProperty().bind(scene.heightProperty().subtract(scene.heightProperty().divide(10)));
		} catch (IOException e) {
			controller.categorySelected(category);
			e.printStackTrace();
			return;
		}

		to.setOpacity(0.0);
		to.setOnMouseClicked(event -> controller.categorySelected(category));
		Timeline fade = new Timeline(new KeyFrame(Duration.ZERO,
				new KeyValue(from.opacityProperty(), 1.0)),
				new KeyFrame(new Duration(500),
						t -> {
							// remove current screen and add new one
							getUI().getChildren().remove(from);
							getUI().getChildren().add(to);
							Timeline fadeIn = new Timeline(
									new KeyFrame(Duration.ZERO, new KeyValue(to.opacityProperty(), 0.0)),
									new KeyFrame(new Duration(500), new KeyValue(to.opacityProperty(), 1.0)));
							fadeIn.play();
						}, new KeyValue(from.opacityProperty(), 0.0)));
		fade.play();
	}
}
