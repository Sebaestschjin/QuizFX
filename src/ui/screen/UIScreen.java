package ui.screen;

import javafx.scene.Node;
import ui.ControllerCallback;

/**
 * @author Sebastian Stern
 */
public abstract class UIScreen {

	protected ControllerCallback controller;

	protected Node ui;

	public void setController(ControllerCallback controller) {
		this.controller = controller;
	}

	public Node getUI() {
		if (ui == null)
			ui = createUI();

		return ui;
	}

	protected abstract Node createUI();

}
