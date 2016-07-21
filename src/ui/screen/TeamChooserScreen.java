package ui.screen;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import ui.ID;
import util.Text;

/**
 * @author Sebastian Stern
 */
public class TeamChooserScreen extends UIScreen {

	private String[] teamNames = new String[teams];

    public TeamChooserScreen() {
    }

    @Override
    public Pane createUI() {
        VBox pane = new VBox();
        pane.setAlignment(Pos.CENTER);
		pane.setSpacing(30);

		// input
		for (int i = 0; i < teams; i++) {
			Pane teamBox = createTeamInput(i);
			sizer.width(teamBox, 0.5);
			pane.getChildren().add(teamBox);
		}

		// continue
        Button nextButton = new Button(Text.NEXT);
        nextButton.setOnMouseClicked(event ->
				verifyInput()
        );
		sizer.font(nextButton);
		sizer.width(nextButton);

		// cancel the game
        Button backButton = new Button(Text.BACK);
        backButton.setOnMouseClicked(event ->
                controller.cancelGame()
        );
		sizer.font(backButton);
		sizer.width(backButton);

        pane.getChildren().addAll(nextButton, backButton);
        return pane;
    }


	private Pane createTeamInput(int teamIndex) {
		HBox teamBox = new HBox();
		teamBox.setSpacing(30);
		teamBox.setAlignment(Pos.CENTER);

		// Label in front of edit
		Label teamLabel = new Label("Team " + (teamIndex + 1) + ":");
		sizer.font(teamLabel);

		// the edit
		TextField teamEdit = new TextField();
		teamEdit.textProperty().addListener((observable, oldValue, newValue) ->
				teamNames[teamIndex] = newValue
		);
		sizer.font(teamEdit);
		teamEdit.setOnKeyPressed(event -> {
					if (event.getCode() == KeyCode.ENTER)
						verifyInput();
				}
		);
		teamEdit.setId(ID.TEAM_INPUT(teamIndex));


		// finish
		teamBox.getChildren().addAll(teamLabel, teamEdit);
		return teamBox;
	}


	private void verifyInput() {
		for (int i = 0; i < teams; ++i) {
			if (teamNames[i] == null || teamNames[i].length() < 5) {
				ui.lookup("#" + ID.TEAM_INPUT(i)).requestFocus();
				return;
			}
		}

		controller.teamNamesEntered(teamNames[0], teamNames[1]);
	}
}
