package logic.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class MyUtils {
    
    public static void fastAlert(String title, String message) {
    	Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setResizable(true);
		alert.setHeaderText(null);
		alert.setContentText(message);

		alert.showAndWait();
    }
    
    private MyUtils(){}
}
