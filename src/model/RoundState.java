package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import util.Util;

public class RoundState {
	Category category;
	List<Question> remainingQuestions;
	List<Question> askedQuestions=new ArrayList<>();
	List<Boolean> team1Results;
	List<Boolean> team2Results;
	int number;
	/**
	 * 
	 * @param number
	 * @param cat
	 * @param remainingQuestions This list is mutated if a Question is removed with {@link #removeQuestion(int)}
	 */
	public RoundState(int number, Category cat, List<Question> remainingQuestions){
		category=cat;
		this.number=number;    
		this.remainingQuestions=remainingQuestions;
		team1Results=new ArrayList<>();
		team2Results=new ArrayList<>();
	}
	public Category getCategory(){
		return category;
	}
	public Question selectAndRemoveQuestion(){
		int i = selectQuestionIndex();
		Question ret = getQuestion(i);
		removeQuestion(i);
		return ret;
	}
	public int selectQuestionIndex() {
		int i=Util.selectIndexWeighted(remainingQuestions);
		return i;
	}
	public Question getQuestion(int index){
		return remainingQuestions.get(index);
	}
	public void removeQuestion(int index){
		askedQuestions.add(remainingQuestions.get(index));
		remainingQuestions.remove(index);
		if(remainingQuestions.isEmpty()){
			remainingQuestions.addAll(category.getQuestions());
			remainingQuestions.removeAll(askedQuestions);
		}
	}
	
	public Boolean getTeamResults(boolean team1, int index){
		List<Boolean> res = getTeamData(team1);
		if(index>=res.size()) return null;
		return res.get(index);
	}
	private List<Boolean> getTeamData(boolean team1) {
		List<Boolean> res = team1?team1Results:team2Results;
		return res;
	}
	public int getTeamAnswerCount(boolean team1){
		List<Boolean> res = getTeamData(team1);
		return res.size();
	}
	public int getTeamPoints(boolean team1){
		List<Boolean> res = getTeamData(team1);
		return (int) res.stream().filter(b -> b==true).count();
	}
	public void enterTeamAnswer(boolean team1, boolean correct){
		List<Boolean> res = getTeamData(team1);
		res.add(correct);
	}
	public void finish(){
		team1Results=Collections.unmodifiableList(team1Results);
		team2Results=Collections.unmodifiableList(team2Results);
		remainingQuestions=null;
	}
	public boolean isFinnised(){
		return remainingQuestions==null;
	}
	public int getRoundNumber(){
		return number;
	}
}
