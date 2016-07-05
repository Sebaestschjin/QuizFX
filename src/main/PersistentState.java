package main;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import json.JSOCodec;
import json.JSONRenderer;
import json.JSOWithPosition;
import json.parser.JSONParser;
import model.HallOfFame;
import model.Settings;
import model.Team;

public class PersistentState {
	public static HallOfFame hallOfFame;
	public static Settings settings;
	public static void loadState(File from) throws IOException{
		if(from==null)
			from=Paths.settingsFile();
		if(!from.exists()){
			resetState();
			return;
		}
		JSOWithPosition data = new JSONParser().parse(from);
		Map<String, JSOWithPosition> m=data.getObject();
		Integer idCounter=JSOCodec.std.decode(m.get("teamIdCounter"), Integer.class);
		if(idCounter==null)
			idCounter=0;
		HallOfFame hof=JSOCodec.std.decode(m.get("hallOfFame"), HallOfFame.class);
		if(hof==null)
			hof=new HallOfFame();
		Settings s=JSOCodec.std.decode(m.get("settings"), Settings.class);
		if(s==null)
			s=new Settings();
		hallOfFame=hof;
		settings=s;
		Team.setIdCounter(idCounter);
	}
	public static void saveState(File to) throws IOException{
		if(to==null)
			to=Paths.settingsFile();
		Map<String, Object> m=new HashMap<>();
		m.put("settings", JSOCodec.std.encode(settings));
		m.put("teamIdCounter", Team.getIdCounter());
		m.put("hallOfFame", JSOCodec.std.encode(hallOfFame));
		JSONRenderer.render(m, to, "\t");
	}
	public static void resetState() {
		hallOfFame=new HallOfFame();
		settings=new Settings();
		Team.setIdCounter(0);
	}
}
