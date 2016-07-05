package ui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import model.*;
import ui.screen.*;

/**
 * @author Sebastian Stern
 */
public class JavaFXUI extends StackPane implements QuizUI {

	private final Duration FADE_OUT = new Duration(500);

	private final Duration FADE_IN = new Duration(500);

	private UIScreen currentScreen;

    private ControllerCallback controller;

	private QuestionScreen currentQuestion;

    public JavaFXUI() {
        addScreen(new BackgroundScreen());
    }

    private void loadScreen(UIScreen screen) {
		// set the correct callbacks
		screen.setController(controller);
		screen.setScene(getScene());

		// load screen or transition between screens
        if (!isLoaded())
            addScreen(screen);
        else
            transition(getLoadedScreen(), screen);

		screen.getUI().requestFocus();
    }

    private boolean isLoaded() {
        return getChildren().size() > 1;
    }

    private Node getLoadedScreen() {
        return currentScreen.getUI();
    }

    private void transition(Node from, UIScreen to) {
		currentScreen.unload();
        to.getUI().setOpacity(0.0);
        Timeline fade = new Timeline(new KeyFrame(Duration.ZERO,
                new KeyValue(from.opacityProperty(), 1.0)),
                new KeyFrame(FADE_OUT,
                        t -> {
                            // remove current screen and add new one
                            getChildren().remove(1);
                            addScreen(to);
                            Timeline fadeIn = new Timeline(
                                    new KeyFrame(Duration.ZERO, new KeyValue(to.getUI().opacityProperty(), 0.0)),
                                    new KeyFrame(FADE_IN, new KeyValue(to.getUI().opacityProperty(), 1.0)));
                            fadeIn.play();
                        }, new KeyValue(from.opacityProperty(), 0.0)));
        fade.play();
    }

    private void addScreen(UIScreen ui) {
		currentScreen = ui;
        getChildren().add(ui.getUI());
    }

	@Override
	public void setControllerCallback(ControllerCallback ccb) {
		controller = ccb;

		// add general handler to cancel the game from anywhere
		getScene().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.F4)
				controller.cancelGame();
		});
	}

	@Override
	public void showTitleScreen() {
		loadScreen(new MainMenuScreen());
	}

	@Override
	public void promptForTeamNames() {
		loadScreen(new TeamChooserScreen());
	}

	@Override
	public void showCategorySelector(boolean team1Selects, GameState gs, Category... categories) {
		loadScreen(new CategoryChooserScreen(team1Selects, gs, categories));
	}

	@Override
	public void showRoundOverview(GameState gs, int totalRounds, int questionsPerRoundPerTeam) {
		loadScreen(new RoundOverviewScreen(gs, totalRounds, questionsPerRoundPerTeam));
	}

	@Override
	public void showQuestion(GameState gs, Question q, Answer[] permutedAnswers) {
		currentQuestion = new QuestionScreen(q, permutedAnswers);
		loadScreen(currentQuestion);
	}

	@Override
	public void showSolution(GameState gs, Question q, Answer[] permutedAnswers, int team1AnswerIndex, int team2AnswerIndex) {
		loadScreen(new AnswerScreen(gs, q, permutedAnswers, team1AnswerIndex, team2AnswerIndex));
	}

	@Override
	public void setTimerDisplay(long millisRemaining, long millisTotal) {
		if (currentQuestion != null)
			currentQuestion.updateTime(millisRemaining, millisTotal);
	}

	@Override
	public void showWinner(GameState gs, int totalRounds, int questionsPerRoundPerTeam) {
		loadScreen(new WinnerScreen(gs, gs.getTeamPoints(true), gs.getTeamPoints(false)));
	}

	@Override
	public void showHallOfFame(HallOfFame hof, Team team1, Team team2) {
		loadScreen(new HallOfFameScreen(hof, team1, team2));
	}

	@Override
	public void giveDoubleAnswerMessage(boolean team1) {
		// TODO implement
		System.out.println("Method \"" + new Object(){}.getClass().getEnclosingMethod().getName() + "\" not yet implemented");
	}

	@Override
	public void showSettingsScreen(Settings s) {
		loadScreen(new SettingsScreen(s));
	}
}