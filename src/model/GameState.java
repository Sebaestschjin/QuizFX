package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import util.Util;

public class GameState {
	Team team1;
	Team team2;
	List<RoundState> rounds;
	List<Category> remainingCategories;
	
	public GameState(Team t1, Team t2, List<Category> categories){
		team1=t1;
		team2=t2;
		remainingCategories=new ArrayList<>(categories);
		rounds=new ArrayList<>();
	}
	public Team getTeam(boolean getTeam1){
		return getTeam1?team1:team2;
	}
	public List<RoundState> getRounds(){
		return Collections.unmodifiableList(rounds);
	}
	public RoundState getCurrentRound(){
		return rounds.get(rounds.size()-1);
	}
	public void beginNewRound(Category cat, List<Question> remainingQuestions){
		RoundState rs;
		rs = new RoundState(rounds.size(), cat, remainingQuestions);
		rounds.add(rs);
	}
	public void selectRandomCategories(int[] outIndices){
		List<Category> cats=new ArrayList<>(remainingCategories);
		for(int i=0; i<outIndices.length; ++i){
			int index=Util.selectIndexWeighted(cats);
			outIndices[i]=index;
			cats.remove(index);
		}
		for(int i=outIndices.length-1; i>0; --i){
			int h=outIndices[i];
			for(int j=i-1; j>=0; --j){
				if(outIndices[j]<=h)
					h++;
			}
			outIndices[i]=h;
		}
	}
	public void removeCategory(int index){
		remainingCategories.remove(index);
	}
	public Category getCategory(int index){
		return remainingCategories.get(index);
	}
	public int getCategoryCount() { return remainingCategories.size(); }
	public int getTeamPoints(boolean team1) {
		int ret=0;
		for(RoundState rs: rounds)
			ret +=rs.getTeamPoints(team1);
		return ret;
	}
}
