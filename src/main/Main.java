package main;

import java.io.File;
import java.io.IOException;
import java.util.List;

import controller.QuizController;
import json.JSOCodec;
import json.JSOWithPosition;
import json.parser.JSONParser;
import model.Category;
import ui.QuizUI;
import ui.mock.MockQuizUI;

public class Main {
	public static void run(File quizDataFile, QuizUI ui) throws IOException{
		JSOWithPosition quizDataJSO=new JSONParser().parse(Paths.relative(Paths.resourcesDir, quizDataFile));
		List<Category> quizData=JSOCodec.std.decodeList(quizDataJSO, Category.class);
		PersistentState.loadState(null);
		QuizController qc=new QuizController(quizData, PersistentState.hallOfFame, ui);
		Runtime.getRuntime().addShutdownHook(new Thread(()  -> {try{
			PersistentState.saveState(null);	
		}catch(IOException x){
			x.printStackTrace();
		}}));
		qc.start();
	}

	public static void main(String[] args) throws IOException {
		runMock();
	}

	private static void runMock() throws IOException {
		QuizUI ui = new MockQuizUI();
		run(new File("testb.json"), ui);
	}
}
