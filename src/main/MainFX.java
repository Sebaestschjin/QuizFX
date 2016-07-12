package main;

import controller.QuizController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import json.JSOCodec;
import json.JSOWithPosition;
import json.parser.JSONParser;
import model.*;
import ui.JavaFXUI;
import util.Resource;
import util.Text;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 *
 */
public class MainFX extends Application {

    /** The shown stage. */
    public static Stage primaryStage;

    /** The handler that loads the screens. */
    private JavaFXUI screenLoader;

	@Override
	public void start(Stage stage) throws Exception {
		primaryStage = stage;

		// default settings
		stage.setTitle(Text.APPNAME);
		stage.setMinWidth(600);
		stage.setMinHeight(480);

		// MainFX scene window
		screenLoader = new JavaFXUI();
		final Scene scene = new Scene(screenLoader, 600, 500);
		scene.getStylesheets().add(getClass().getResource(Resource.STYLE).toExternalForm());
		stage.setScene(scene);
		screenLoader.initialize();

		// enable full screen without exit possibility
		stage.setFullScreen(true);
		stage.setFullScreenExitHint("");
		stage.setFullScreenExitKeyCombination(KeyCombination.valueOf("F11"));

		// let's go!
		stage.show();

		loadFile("questions.json");
	}

	public static void main(String[] args) throws IOException {
		launch(args);
	}

	private void loadFile(String fileName) throws IOException {
		File quizDataFile = new File(fileName);
		JSOWithPosition quizDataJSO = new JSONParser().parse(Paths.relative(Paths.resourcesDir, quizDataFile));
		List<Category> quizData= JSOCodec.std.decodeList(quizDataJSO, Category.class);
		PersistentState.loadState(null);
		QuizController qc=new QuizController(quizData, PersistentState.hallOfFame, screenLoader);
		Runtime.getRuntime().addShutdownHook(new Thread(()  -> {try{
			PersistentState.saveState(null);
		}catch(IOException x){
			x.printStackTrace();
		}}));
		qc.start();
	}

}
