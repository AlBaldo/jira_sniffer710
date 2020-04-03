package logic.utils;

import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;

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
    
    public static Icon resizeIcon(ImageIcon icon, int resizedWidth, int resizedHeight) {
        Image img = icon.getImage();  
        Image resizedImage = img.getScaledInstance(resizedWidth, resizedHeight,  java.awt.Image.SCALE_SMOOTH);  
        return new ImageIcon(resizedImage);
    }
    
    private MyUtils(){}
}
