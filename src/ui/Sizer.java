package ui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import ui.screen.UIScreen;

/**
 * @author Sebastian Stern
 */
public class Sizer {

	public final static double FONT_RATIO_GENERAL = 0.015;

	public final static double BUTTON_WIDTH_GENERAL = 0.4;

	private UIScreen parent;

	public Sizer(UIScreen parent) {
		this.parent = parent;
	}

	public void font(Node n) {
		font(n, FONT_RATIO_GENERAL);
	}

	public void font(Node n, double ratio) {
		DoubleProperty binding = parent.getScreenRatioProperty(ratio);
		n.styleProperty().bind(Bindings.concat("-fx-font-size: ", binding.asString(), ";"));
	}

	public void width(Button b) {
		this.width(b, BUTTON_WIDTH_GENERAL);
	}

	public void width(Region r, double ratio) {
		r.prefWidthProperty().bind(parent.getScreenWidthProperty(ratio));
	}

	public void height(Region r, double ratio) {
		r.prefHeightProperty().bind(parent.getScreenHeightProperty(ratio));
	}

	public void size(Region r, double ratio) {
		r.prefHeightProperty().bind(parent.getScreenHeightProperty(ratio));
		r.prefWidthProperty().bind(r.prefHeightProperty());
	}


}
