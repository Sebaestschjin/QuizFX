package model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import json.DataFormatException;
import json.JSOCodec;
import json.JSOEncodable;
import json.JSOWithPosition;

public class HallOfFame implements JSOEncodable{
	public static class Entry implements Comparable<Entry>,JSOEncodable{
		Team team;
		int points;
		
		public Entry(Team t, int points){
			team=t;
			this.points=points;
		}
		/**
		 * Better Teams are less in this ordering, so they come first in the sorted list.
		 * If teams are equally good, teams with higher id are less, so newer winners can always replace old ones.
		 */
		@Override
		public int compareTo(Entry o) {
			
			int pDiff=o.points - points;
			if(pDiff!=0)
				return pDiff;
			
			int idDiff=o.team.getId()- team.getId();
			if(idDiff!=0)
				return idDiff;
			
			return 0;
		}
		public Team getTeam(){
			return team;
		}
		public int getPoints(){
			return points;
		}
		@Override
		public JSOWithPosition encode(JSOCodec c) {
			Map<String, JSOWithPosition> ret=new HashMap<>();
			ret.put("team", c.encode(team));
			ret.put("score", c.encode(points));
			return new JSOWithPosition(ret);
		}
		public static Entry decodeJSO(JSOWithPosition jso, JSOCodec codec){
			Map<String, JSOWithPosition> m=jso.getObjectNonNull();
			Team team=codec.decode(m.get("team"), Team.class);
			if(team==null)
				throw new DataFormatException("\"team\" is required", jso.getPosition());
			Integer points=codec.decode(m.get("score"), Integer.class);
			if(points==null)
				throw new DataFormatException("\"score\" is required", jso.getPosition());
			return new Entry(team, points);
		}
	}
	
	SortedSet<Entry> entries=new TreeSet<>();
	public void addEntry(Team team, int points){
		entries.add(new Entry(team, points));
	}
	public SortedSet<Entry> getEntries(){
		return Collections.unmodifiableSortedSet(entries);
	}
	public void keepEntries(int maxEntries){
		int i=0;
		for(Iterator<Entry> it=entries.iterator(); it.hasNext(); i++){
			it.next();
			if(i>=maxEntries)
				it.remove();
		}
	}
	public HallOfFame() {
	}
	public HallOfFame(Collection<Entry> entries) {
		this.entries.addAll(entries);
	}
	@Override
	public JSOWithPosition encode(JSOCodec c) {
		return c.encodeAsList(entries);
	}
	public static HallOfFame decodeJSO(JSOWithPosition jso, JSOCodec codec){
		List<Entry> entries=codec.decodeList(jso, Entry.class);
		if(entries==null)
			return new HallOfFame();
		return new HallOfFame(entries);
		
	}
	
}
