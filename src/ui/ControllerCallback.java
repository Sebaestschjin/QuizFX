package ui;

public interface ControllerCallback {
	void teamNamesEntered(String team1, String team2);
	void titleScreenDismissed();
	void categorySelected(int index);
	void cancelGame();
	void roundOverwiewDismissed();
	void team1AnswerEntered(int index);
	void team2AnswerEntered(int index);
	void solutionScreenDismissed();
	void winnerScreenDismissed();
	void hallOfFameDismissed();
}
