package logic.boundary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.controlsfx.control.CheckComboBox;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import logic.control.SniffControl;
import logic.utils.MyConstants;
import logic.utils.MyUtils;

public class GrapherController {
	
	@FXML
	Button goboxesBtn, gourlBtn;
	
	@FXML
	CheckComboBox<String> resolutionCCB, statusCCB, typeCCB;
	
	@FXML
	TextField projnTF, urlTF;
	
	@FXML
	public void initialize() {
		resolutionCCB.getItems().addAll(FXCollections.observableList(Arrays.asList(MyConstants.baseresolution)));
		statusCCB.getItems().addAll(FXCollections.observableList(Arrays.asList(MyConstants.basestatus)));
		typeCCB.getItems().addAll(FXCollections.observableList(Arrays.asList(MyConstants.basetypes)));
		
		
		projnTF.setText("PARQUET");
		resolutionCCB.getCheckModel().check("Fixed");
		statusCCB.getCheckModel().check("Closed");
		statusCCB.getCheckModel().check("Resolved");
		typeCCB.getCheckModel().check("Bug");
		
		resolutionCCB.getItemBooleanProperty(0).addListener(e->{
			if(resolutionCCB.getCheckModel().isChecked(0)){
				int n = resolutionCCB.getCheckModel().getItemCount();
				
				for(int i = 1; i < n; i++) {
					resolutionCCB.getCheckModel().check(i);
				}
			}else {
				int n = resolutionCCB.getCheckModel().getItemCount();
				
				for(int i = 1; i < n; i++) {
					resolutionCCB.getCheckModel().clearCheck(i);
				}
			}
		});
	
		statusCCB.getItemBooleanProperty(0).addListener(e->{
			if(statusCCB.getCheckModel().isChecked(0)){
				int n = statusCCB.getCheckModel().getItemCount();
				
				for(int i = 1; i < n; i++) {
					statusCCB.getCheckModel().check(i);
				}
			}else {
				int n = statusCCB.getCheckModel().getItemCount();
				
				for(int i = 1; i < n; i++) {
					statusCCB.getCheckModel().clearCheck(i);
				}
			}
		});
		
		typeCCB.getItemBooleanProperty(0).addListener(e->{
			if(typeCCB.getCheckModel().isChecked(0)){
				int n = typeCCB.getCheckModel().getItemCount();
				
				for(int i = 1; i < n; i++) {
					typeCCB.getCheckModel().check(i);
				}
			}else {
				int n = typeCCB.getCheckModel().getItemCount();
				
				for(int i = 1; i < n; i++) {
					typeCCB.getCheckModel().clearCheck(i);
				}
			}
		});
		
	
		
		goboxesBtn.setPadding(new Insets(0, 3, 0, 3));
		goboxesBtn.setGraphic(new ImageView(new Image("/resources/images/right_blue_arrow_40x40.png")));

		gourlBtn.setPadding(new Insets(0, 3, 0, 3));
		gourlBtn.setGraphic(new ImageView(new Image("/resources/images/right_blue_arrow_40x40.png")));
		
		goboxesBtn.setOnMouseClicked(e -> {
			if(checkprojname()) {
				String name = projnTF.getText().trim();
				List<String> resol = new ArrayList<>(resolutionCCB.getCheckModel().getCheckedItems());
				List<String> stat = new ArrayList<>(statusCCB.getCheckModel().getCheckedItems());
				List<String> typ = new ArrayList<>(typeCCB.getCheckModel().getCheckedItems());
				
				SniffControl sc = new SniffControl();
				
				sc.snifJira(name, resol, stat, typ);
			}
		});
		
		gourlBtn.setOnMouseClicked(e->{
			if(checkurlbox()) {
				String url = urlTF.getText();
				
				SniffControl sc = new SniffControl();
				
				sc.snifJira(url);
			}
		});
	}

	private boolean checkurlbox() {
		if(urlTF.getText() == null || urlTF.getText().trim().isEmpty()) {
			MyUtils.fastAlert("Oops!", "You forgot to enter the url!");
			return false;
		}
		return true;
	}

	private boolean checkprojname() {
		if(projnTF.getText() == null || projnTF.getText().trim().isEmpty()) {
			MyUtils.fastAlert("Oops!", "You forgot to enter project name!");
			return false;
		}
		return true;
	}


}
