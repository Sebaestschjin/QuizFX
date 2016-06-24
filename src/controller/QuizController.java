package controller;

import java.util.Arrays;
import java.util.List;

import model.Answer;
import model.Category;
import model.GameState;
import model.HallOfFame;
import model.Question;
import model.RoundState;
import model.Team;
import ui.ControllerCallback;
import ui.QuizUI;

public class QuizController implements ControllerCallback{

	enum State{
		SHOWING_TITLE_SCREEN,
		EXPECTING_TEAM_NAMES,
		SELECTING_CATEGORY,
		SHOWING_ROUND_OVERVIEW,
		SHOWING_QUESTION,
		SHOWING_SOLUTION,
		SHOWING_WINNER,
		SHOWING_HALL_OF_FAME
	}

	private static final int TOTAL_ROUNDS = 1;
	private static final int SIMULTANEOUS_CATEGORIES = 3;
	private static final int QPRPT = 1;
	private static final boolean REUSE_QUESTIONS = true;
	private static final long QUESTION_DURATION = 20*1000;

	State controllerState;
	GameState gameState;
	List<Category> categories;
	QuizUI ui;
	HallOfFame hof;


	Thread timer;
	int team1AnswerIndex, team2AnswerIndex;
	int[] selectedCategoriesIndices=new int[SIMULTANEOUS_CATEGORIES];
	Category[] selectedCategories=new Category[SIMULTANEOUS_CATEGORIES];
	Answer[] permutedAnswers;
	Question currentQuestion;

	public QuizController(List<Category> categories, HallOfFame hof, QuizUI ui){
		this.categories=categories;
		this.ui=ui;
		this.hof=hof;
		ui.setControllerCallback(this);
	}
	private void expectState(State s){
		if(controllerState!=s){
			throw new IllegalStateException("Contoller is in state "+controllerState+", but was expected to be in "+s);
		}
	}
	private void expectState(State... ss){
		for(State s: ss)
			if(controllerState==s)
				return;
		throw new IllegalStateException("Contoller is in state "+controllerState+", but was expected to be in one of "+Arrays.asList(ss));
	}
	@Override
	public void teamNamesEntered(String team1, String team2) {
		expectState(State.EXPECTING_TEAM_NAMES);
		gameState=new GameState(new Team(team1), new Team(team2), categories);
		controllerState=State.SHOWING_ROUND_OVERVIEW;
		ui.showRoundOverview(gameState, TOTAL_ROUNDS, QPRPT);
	}

	@Override
	public void titleScreenDismissed() {
		expectState(State.SHOWING_TITLE_SCREEN);
		controllerState=State.EXPECTING_TEAM_NAMES;
		ui.promptForTeamNames();
	}

	@Override
	public void categorySelected(int index) {
		expectState(State.SELECTING_CATEGORY);
		Category selected=selectedCategories[index];
		int selectedIndex=selectedCategoriesIndices[index];
		if(!REUSE_QUESTIONS)
			gameState.removeCategory(selectedIndex);
		gameState.beginNewRound(selected);
		showQuestion();
	}

	private void showQuestion() {
		expectState(State.SELECTING_CATEGORY, State.SHOWING_SOLUTION);
		controllerState=State.SHOWING_QUESTION;
		RoundState rs=gameState.getCurrentRound();
		int qi = rs.selectQuestionIndex();
		final Question q=rs.getQuestion(qi);
		currentQuestion=q;
		if(!REUSE_QUESTIONS)
			rs.removeQuestion(qi);
		ui.setTimerDisplay(QUESTION_DURATION, QUESTION_DURATION);
		List<Answer> ppa=q.getShuffledAnswers();
		permutedAnswers=(Answer[]) ppa.toArray(new Answer[ppa.size()]);
		ui.showQuestion(q, permutedAnswers);
		final long startTime=System.currentTimeMillis();
		final long endTime=startTime+QUESTION_DURATION;
		team1AnswerIndex=-1;
		team2AnswerIndex=-1;
		synchronized (this) {
			timer=new Thread(() ->{
				while(true){
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
					}
					long timeRemaining ;
					synchronized (QuizController.this) {
						if(timer!=Thread.currentThread())
							return;
						timeRemaining = endTime - System.currentTimeMillis();
						if(timeRemaining<=0){
							timer=null;
							endQuestion();
							return;
						}
						ui.setTimerDisplay(timeRemaining, QUESTION_DURATION);
					}

				}
			});
			timer.start();
		}
	}

	@Override
	public void cancelGame() {
		controllerState=State.SHOWING_TITLE_SCREEN;
		synchronized (this) {
			if(timer!=null){
				Thread t = timer;
				timer=null;
				if(t!=null)
					t.interrupt();
			}
		}

		ui.showTitleScreen();
	}


	@Override
	public void roundOverwiewDismissed() {
		expectState(State.SHOWING_ROUND_OVERVIEW);
		if(gameState.getRounds().size()<TOTAL_ROUNDS){
			controllerState=State.SELECTING_CATEGORY;
			gameState.selectRandomCategories(selectedCategoriesIndices);
			for(int i=0; i<SIMULTANEOUS_CATEGORIES; ++i)
				selectedCategories[i]=gameState.getCategory(selectedCategoriesIndices[i]);
			ui.showCategorySelector(selectedCategories);
		}else{
			assert(false);
			controllerState=State.SHOWING_WINNER;
			int team1Points=gameState.getTeamPoints(true);
			int team2Points=gameState.getTeamPoints(false);
			hof.addEntry(gameState.getTeam(true), team1Points);
			hof.addEntry(gameState.getTeam(false), team2Points);
			ui.showWinner(gameState, TOTAL_ROUNDS, QPRPT);
		}


	}

	@Override
	public void team1AnswerEntered(int index) {
		expectState(State.SHOWING_QUESTION);
		if(team1AnswerIndex>=0){
			ui.giveDoubleAnswerMessage(true);
		}else{
			team1AnswerIndex=index;
		}
		checkAnswers();
	}

	@Override
	public void team2AnswerEntered(int index) {
		expectState(State.SHOWING_QUESTION);
		if(team2AnswerIndex>=0){
			ui.giveDoubleAnswerMessage(false);
		}else{
			team2AnswerIndex=index;
		}
		checkAnswers();
	}

	private void checkAnswers() {
		expectState(State.SHOWING_QUESTION);
		if(team1AnswerIndex<0 || team2AnswerIndex<0)
			return;
		synchronized (this) {
			Thread t = timer;
			timer=null;
			if(t!=null)
				t.interrupt();
		}
		endQuestion();
	}
	private void endQuestion() {
		expectState(State.SHOWING_QUESTION);

		RoundState currentRound = gameState.getCurrentRound();
		currentRound.enterTeamAnswer(true,  team1AnswerIndex>=0 && permutedAnswers[team1AnswerIndex].isCorrect());
		currentRound.enterTeamAnswer(false, team2AnswerIndex>=0 && permutedAnswers[team2AnswerIndex].isCorrect());
		controllerState=State.SHOWING_SOLUTION;
		ui.showSolution(currentQuestion, permutedAnswers, team1AnswerIndex, team2AnswerIndex);
	}

	@Override
	public void solutionScreenDismissed() {
		expectState(State.SHOWING_SOLUTION);
		RoundState currentRound = gameState.getCurrentRound();
		int tac1=currentRound.getTeamAnswerCount(true);
		int tac2=currentRound.getTeamAnswerCount(false);
		assert(tac1==tac2);
		int tac=tac1;
		if(tac>=QPRPT){
			endRound();
		}else{
			showQuestion();
		}
	}

	private void endRound() {
		expectState(State.SHOWING_SOLUTION);
		if(gameState.getRounds().size()>=TOTAL_ROUNDS){
			controllerState=State.SHOWING_WINNER;
			int team1Points=gameState.getTeamPoints(true);
			int team2Points=gameState.getTeamPoints(false);
			hof.addEntry(gameState.getTeam(true), team1Points);
			hof.addEntry(gameState.getTeam(false), team2Points);

			ui.showWinner(
					gameState, 
					TOTAL_ROUNDS, 
					QPRPT
					);
		}else{
			controllerState=State.SHOWING_ROUND_OVERVIEW;
			ui.showRoundOverview(gameState, TOTAL_ROUNDS, QPRPT);
		}

	}
	@Override
	public void winnerScreenDismissed() {
		expectState(State.SHOWING_WINNER);
		controllerState=State.SHOWING_HALL_OF_FAME;
		ui.showHallOfFame(hof, gameState.getTeam(true), gameState.getTeam(false));
	}

	@Override
	public void hallOfFameDismissed() {
		expectState(State.SHOWING_HALL_OF_FAME);
		start();
	}
	public void start() {
		synchronized (this) {
			Thread t=timer;
			timer=null;
			if(t!=null)
				t.interrupt();
		}
		controllerState = State.SHOWING_TITLE_SCREEN;
		ui.showTitleScreen();
	}



}
