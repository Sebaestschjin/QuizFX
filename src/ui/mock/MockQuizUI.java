package ui.mock;

import model.Answer;
import model.Category;
import model.GameState;
import model.HallOfFame;
import model.Question;
import model.Team;
import ui.ControllerCallback;
import ui.QuizUI;

public class MockQuizUI implements QuizUI {

	@Override
	public void setControllerCallback(ControllerCallback ccb) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showTitleScreen() {
		// TODO Auto-generated method stub

	}

	@Override
	public void promptForTeamNames() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showCategorySelector(Category... categories) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showRoundOverview(GameState gs, int totalRounds, int questionsPerRoundPerTeam) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showQuestion(Question q, Answer[] permutedAnswers) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showSolution(Question q, Answer[] permutedAnswers, int team1AnswerIndex, int team2AnswerIndex) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTimerDisplay(long millisRemaining, long millisTotal) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showWinner(GameState gs, int team1Points, int team2Points, int totalRounds,
			int questionsPerRoundPerTeam) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showHallOfFame(HallOfFame hof, Team team1, Team team2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void giveDoubleAnswerMessage(boolean team1) {
		// TODO Auto-generated method stub

	}

	public void start() {
		// TODO Auto-generated method stub
		
	}

}
