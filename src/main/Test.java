package main;

import java.io.File;
import java.io.IOException;
import java.util.List;

import json.JSOCodec;
import json.JSONRenderer;
import json.JSOWithPosition;
import json.parser.JSONParser;
import model.Category;
import model.Team;
import util.PrintWriterCharSink;

public class Test {
	public static void main(String[] args) throws IOException {
		PersistentState.loadState(null);
		changeHallOfFame2(null);
		testJSON();
		PersistentState.saveState(null);
	}
	public static void changeHallOfFame1(String file){
		
		Team t1=new Team("Dicke Dödel");
		Team t2=new Team("Kleine Klöten");
		PersistentState.hallOfFame.addEntry(t1, 1000);
		PersistentState.hallOfFame.addEntry(t2, 1);
	}
	public static void changeHallOfFame2(String file){
		Team t1=new Team("Flinke Fotzen");
		Team t2=new Team("Lahme Labia");
		PersistentState.hallOfFame.addEntry(t1, 1000);
		PersistentState.hallOfFame.addEntry(t2, 1);
	}

	private static void testJSON() throws IOException {
		JSOWithPosition o=new JSONParser().parse(Paths.relative(Paths.resourcesDir, new File("testb.json")));
		JSONRenderer.render(o, PrintWriterCharSink.out, "\t");
		PrintWriterCharSink.out.flush();
		List<Category> cats=JSOCodec.std.decodeList(o, Category.class);
		JSOWithPosition o2=JSOCodec.std.encode(cats);
		System.out.println(o2);
		System.out.println(JSOCodec.std.encode(PersistentState.hallOfFame));
		
	}
}
