package ui.screen;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import ui.ControllerCallback;
import ui.Sizer;
import util.Text;

/**
 * @author Sebastian Stern
 */
public abstract class UIScreen {

	// TODO generalize
	final int teams = 2;

	protected Scene scene;

	Sizer sizer;

	protected ControllerCallback controller;

	protected Node ui;

	public void setController(ControllerCallback controller) {
		this.controller = controller;
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}

	public Node getUI() {
		if (ui == null) {
			sizer = new Sizer(this);
			ui = createUI();
		}

		return ui;
	}

	public void unload() {

	}

	protected abstract Node createUI();

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

}
