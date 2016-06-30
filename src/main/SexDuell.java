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
import ui.ScreenStack;
import util.Resource;
import util.Text;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 *
 */
public class SexDuell extends Application {

    /** The shown stage. */
    private Stage primaryStage;

    /** The handler that loads screens. */
    private  ScreenStack screenLoader;

	@Override
	public void start(Stage stage) throws Exception {
		primaryStage = stage;

		// default settings
		stage.setTitle(Text.TITLE);
		stage.initStyle(StageStyle.UTILITY);
		stage.setMinWidth(600);
		stage.setMinHeight(480);

		// SexDuell scene window
		screenLoader = new ScreenStack();
		final Scene scene = new Scene(screenLoader, 600, 500);
		scene.getStylesheets().add(getClass().getResource(Resource.STYLE).toExternalForm());
		stage.setScene(scene);

		// enable full screen without exit possibility
		stage.setFullScreen(true);
		stage.setFullScreenExitHint("");
		stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

		// let's go!
		stage.show();

		loadFile("testb.json");
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
