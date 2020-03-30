package logic.boundary;

import java.util.Arrays;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import logic.utils.MyConstants;

public class GrapherController {
	
	@FXML
	Button goboxesBtn, gourlBtn;
	
	@FXML
	ComboBox<String> resolutionCb, statusCb, typeCb;
	
	@FXML
	TextField projnTF;
	
	@FXML
	public void initialize() {
		resolutionCb.setItems(FXCollections.observableArrayList(Arrays.asList(MyConstants.baseresolution)));
		
	}

}
