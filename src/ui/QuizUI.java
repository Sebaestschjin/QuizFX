package ui;

import model.Answer;
import model.Category;
import model.GameState;
import model.HallOfFame;
import model.Question;
import model.Settings;
import model.Team;

public interface QuizUI {
	void setControllerCallback(ControllerCallback ccb);
	void showTitleScreen();
	void promptForTeamNames();
	void showCategorySelector(boolean team1Selects, GameState gs, Category... categories);
	void showRoundOverview(GameState gs, int totalRounds, int questionsPerRoundPerTeam);
	void showQuestion(GameState gs, Question q, Answer[] permutedAnswers);
	void showSolution(GameState gs, Question q, Answer[] permutedAnswers, int team1AnswerIndex, int team2AnswerIndex);
	void setTimerDisplay(long millisRemaining, long millisTotal);
	void showWinner(GameState gs, int totalRounds, int questionsPerRoundPerTeam);
	void showHallOfFame(HallOfFame hof, Team team1, Team team2);
	void giveDoubleAnswerMessage(boolean team1);
	void showSettingsScreen(Settings s);
}
