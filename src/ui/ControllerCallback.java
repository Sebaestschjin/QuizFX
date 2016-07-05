package ui;

import model.Settings;

public interface ControllerCallback {
	enum TitleScreenOption{
		START_GAME,
		SHOW_HALL_OF_FAME,
		EDIT_SETTINGS
	}
	void teamNamesEntered(String team1, String team2);
	void titleScreenDismissed(TitleScreenOption action);
	void categorySelected(int index);
	void cancelGame();
	void roundOverviewDismissed();
	void team1AnswerEntered(int index);
	void team2AnswerEntered(int index);
	void solutionScreenDismissed();
	void winnerScreenDismissed();
	void hallOfFameDismissed();
	void settingsScreenDismissed(Settings newSettings);
}
