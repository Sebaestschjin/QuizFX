package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import json.JSOCodec;
import json.JSONRenderer;
import json.JSOWithPosition;
import json.parser.JSONParser;
import model.HallOfFame;
import model.Team;
import util.PrintWriterCharSink;

public class PersistentState {
	public static HallOfFame hallOfFame;
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
		hallOfFame=hof;
		Team.setIdCounter(idCounter);
	}
	public static void saveState(File to) throws IOException{
		if(to==null)
			to=Paths.settingsFile();
		Map<String, Object> m=new HashMap<>();
		m.put("teamIdCounter", Team.getIdCounter());
		m.put("hallOfFame", JSOCodec.std.encode(hallOfFame));
		try(PrintWriter pw=new PrintWriter(new FileWriter(to))){
			JSONRenderer.render(m, new PrintWriterCharSink(pw), "\t");
		}
	}
	public static void resetState() {
		hallOfFame=new HallOfFame();
		Team.setIdCounter(0);
	}
}
