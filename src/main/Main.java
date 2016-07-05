package main;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import controller.QuizController;
import json.JSOCodec;
import json.JSOWithPosition;
import json.parser.JSONParser;
import model.Category;
import model.Question;
import ui.QuizUI;
import ui.mock.MockQuizUI;

public class Main {
	static boolean doImageFileCheck=true;
	public static void run(File quizDataFile, QuizUI ui) throws IOException{
		JSOWithPosition quizDataJSO=new JSONParser().parse(Paths.relative(Paths.resourcesDir, quizDataFile));
		List<Category> quizData=JSOCodec.std.decodeList(quizDataJSO, Category.class);
		new Thread(()->checkImageFiles(quizData)).start();;
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
		MockQuizUI ui = new MockQuizUI();
		run(new File("testb.json"), ui);
		ui.start();
	}
	public static void checkImageFiles(List<Category> data){
		System.err.println("Überprüfe Bilddateien...");
		for(Category cat: data){
			checkImageFile(cat.getImageFile());
			for(Question q: cat.getQuestions()){
				checkImageFile(q.getQuestionImageFile());
				checkImageFile(q.getAnswerImageFile());
			}
		}
		System.err.println("Fertig mit Überprüfen der Bilddateien.");
	}
	private static void checkImageFile(File imageFile) {
		if(imageFile==null)
			return;
		File abs = Paths.relative(Paths.resourcesDir, imageFile);
		if(!abs.exists()){
			System.err.println("Bilddatei'"+imageFile+"' nicht gefunden. (Relativ zu Pfad '"+Paths.resourcesDir+"')");
			return;
		}
		if(!abs.canRead()){
			System.err.println("Bilddatei '"+abs+"' kann nicht gelesen werden.");
			return;
		}
		try{
			ImageIO.read(abs);
		}catch(IOException x){
			System.err.println("Fehler beim Öffnen der Bilddatei '"+abs+"':");
			x.printStackTrace();			
		}
		
	}
	
	
}
