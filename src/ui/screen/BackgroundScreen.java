package ui.screen;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import ui.Sizer;
import util.Colors;
import util.Resource;

/**
 * @author Sebastian Stern
 */
public class BackgroundScreen extends UIScreen {

    public BackgroundScreen() {
    }

    @Override
    public Node createUI() {
        BorderPane pane = new BorderPane();

        // background-color
        pane.setBackground(new javafx.scene.layout.Background(new BackgroundFill(Colors.BLUE, CornerRadii.EMPTY, Insets.EMPTY)));

        // logo in top right corner
        addImage(pane, Pos.TOP_RIGHT, Resource.LOGO);
        addImage(pane, Pos.BOTTOM_RIGHT, Resource.CLAIM);

		pane.setPadding(new Insets(0, 20, 0, 0));

        return pane;
    }

    private void addImage(BorderPane parent, Pos borderPosition, String resourceName) {
        try {
            ImageView image = new ImageView(new Image(getClass().getResourceAsStream(resourceName)));
            image.setPreserveRatio(true);

            image.fitWidthProperty().bind(scene.widthProperty().divide(Sizer.BG_RATIO));
            BorderPane.setAlignment(image, borderPosition);

            switch (borderPosition) {
                case TOP_RIGHT:
                    parent.setTop(image);
                    break;
                case BOTTOM_RIGHT:
                    parent.setBottom(image);
                    break;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
