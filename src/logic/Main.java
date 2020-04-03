package logic;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import logic.boundary.GrapherController;
import logic.utils.Log;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;


public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		
		try {
			GrapherController gc = new GrapherController();
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/startscene.fxml"));
			loader.setController(gc);
			AnchorPane root = loader.load();


			Scene scene = new Scene(root);
			
			scene.getStylesheets().add(getClass().getResource("/resources/base.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			
		} catch (IOException e) {
			Log.getLog().debugMsg("startscene.fxml not found");
		}
	}
	
	public static void main(String[] args) {
		launch((String[])null);//do not need the args
	}
}
