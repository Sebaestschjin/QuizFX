package ui.screen;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import ui.ControllerCallback;
import ui.Sizer;

/**
 * @author Sebastian Stern
 */
public abstract class UIScreen {

	// TODO generalize
	final int teams = 2;

	protected Scene scene;

	Sizer sizer;

	protected ControllerCallback controller;

	protected Pane ui;

	public void setController(ControllerCallback controller) {
		this.controller = controller;
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}

	public Pane getUI() {
		if (ui == null) {
			sizer = new Sizer(this);
			ui = createUI();
		}

		return ui;
	}

	public void unload() {

	}

	protected abstract Pane createUI();

	public DoubleProperty getScreenRatioProperty(double ratio) {
		DoubleProperty prop = new SimpleDoubleProperty();
		prop.bind(scene.heightProperty().add(scene.widthProperty()).multiply(ratio));
		return prop;
	}

	public DoubleProperty getScreenHeightProperty(double ratio) {
		DoubleProperty prop = new SimpleDoubleProperty();
		prop.bind(scene.heightProperty().multiply(ratio));
		return prop;
	}

	public DoubleProperty getScreenWidthProperty(double ratio) {
		DoubleProperty prop = new SimpleDoubleProperty();
		prop.bind(scene.widthProperty().multiply(ratio));
		return prop;
	}

	protected void setBackground(Region region, Color color) {
		setBackground(region, color, new CornerRadii(10));
	}

	protected void setBackground(Region region, Color color, CornerRadii corner) {
		setBackground(region, color, corner, Insets.EMPTY);
	}

	protected void setBackground(Region region, Color color, CornerRadii corner, Insets insets) {
		region.setBackground(new Background(new BackgroundFill(color, corner, insets)));
	}

}
