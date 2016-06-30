package ui.screen;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import util.Colors;
import util.Resource;

/**
 * @author Sebastian Stern
 */
public class BackgroundScreen extends UIScreen {

    final int sizeRatio = 7;

    public BackgroundScreen() {
    }

    @Override
    public Node createUI() {
        BorderPane pane = new BorderPane();

        // background-color
        pane.setBackground(new javafx.scene.layout.Background(new BackgroundFill(Colors.BLUE, CornerRadii.EMPTY, Insets.EMPTY)));

        // logo in top right corner
        ImageView logo = new ImageView(new Image(getClass().getResourceAsStream(Resource.LOGO)));
        logo.setPreserveRatio(true);

        // claim in bottom right corner
        ImageView claim = new ImageView(new Image(getClass().getResourceAsStream(Resource.CLAIM)));
        claim.setPreserveRatio(true);

        pane.setAlignment(logo, Pos.TOP_RIGHT);
        pane.setTop(logo);

        pane.setAlignment(claim, Pos.BOTTOM_RIGHT);
        pane.setBottom(claim);

        // layout listener to auto-resize logo
        pane.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            // new size
            double newWidth = newValue.getWidth() / sizeRatio;
            logo.setFitWidth(newWidth);
            claim.setFitWidth(newWidth);

            // adjust parent padding according so safe zone requirements
            double safeZone = logo.getFitWidth() / 10;
            pane.setPadding(new Insets(0, safeZone, 0, 0));
        });

        return pane;
    }
}
