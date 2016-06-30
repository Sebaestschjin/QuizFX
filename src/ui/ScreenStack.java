package ui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import model.*;
import ui.screen.*;

import java.io.File;

/**
 * @author Sebastian Stern
 */
public class ScreenStack extends StackPane implements QuizUI {

	private final Duration FADE_OUT = new Duration(800);

	private final Duration FADE_IN = new Duration(800);

    private ControllerCallback controller;

	private QuestionScreen currentQuestion;

    public ScreenStack() {
        addScreen(new BackgroundScreen());
    }

    public void loadScreen(UIScreen screen) {
        if (!isLoaded())
            addScreen(screen);
        else
            transition(getLoadedScreen(), screen);
    }

    private boolean isLoaded() {
        return getChildren().size() > 1;
    }

    private Node getLoadedScreen() {
        return getChildren().get(1);
    }

    private void transition(Node from, UIScreen to) {
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
        ui.setController(controller);
        getChildren().add(ui.getUI());
    }

	@Override
	public void setControllerCallback(ControllerCallback ccb) {
		controller = ccb;
	}

	@Override
	public void showTitleScreen() {
		Answer a = new Answer("Antwort 1", 1);
		Answer b = new Answer("Antwort 2", 1);
		Answer c = new Answer("Antwort 3", 1);
		Answer d = new Answer("Antwort 4", 1);
		Question q = new Question("Wie lautet die Antwort?", null, "Das stimmt!", null, null, 1, a, b, c, d);
		loadScreen(new QuestionScreen(q, new Answer[] {a, b, c, d}));

		//loadScreen(new MainMenuScreen());
	}

	@Override
	public void promptForTeamNames() {
		loadScreen(new TeamChooserScreen());
	}

	@Override
	public void showCategorySelector(Category... categories) {
		loadScreen(new CategoryChooserScreen(categories));
	}

	@Override
	public void showRoundOverview(GameState gs, int totalRounds, int questionsPerRoundPerTeam) {
		loadScreen(new RoundOverviewScreen(gs, totalRounds, questionsPerRoundPerTeam));
	}

	@Override
	public void showQuestion(Question q, Answer[] permutedAnswers) {
		currentQuestion = new QuestionScreen(q, permutedAnswers);
		loadScreen(currentQuestion);
	}

	@Override
	public void showSolution(Question q, Answer[] permutedAnswers, int team1AnswerIndex, int team2AnswerIndex) {
		loadScreen(new AnswerScreen(q));
	}

	@Override
	public void setTimerDisplay(long millisRemaining, long millisTotal) {
		if (currentQuestion != null)
			currentQuestion.updateTime(millisRemaining, millisTotal);
	}

	@Override
	public void showWinner(GameState gs, int team1Points, int team2Points, int totalRounds, int questionsPerRoundPerTeam) {
		loadScreen(new WinnerScreen(gs, team1Points, team2Points));
	}

	@Override
	public void showHallOfFame(HallOfFame hof, Team team1, Team team2) {
		loadScreen(new HallOfFameScreen(hof, team1, team2));
	}

	@Override
	public void giveDoubleAnswerMessage(boolean team1) {
		// TODO implement
	}
}