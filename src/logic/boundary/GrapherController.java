package logic.boundary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.controlsfx.control.CheckComboBox;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import logic.control.SniffControl;
import logic.utils.MyUtils;

public class GrapherController {
	private static final String RES_FS = "Resolved";
	
	private static final String[] basetypes = {"All", "Bug", "Improvement", "New Feature", "Task", "Test", "Wish"};
	
	private static final String[] basestatus = {"All", "Open", "In Progres", "Reopened", RES_FS, "Closed", "Patch Avaiable"};

	private static final String[] baseresolution = {"All", "Unresolved", "Fixed", "Won't fix", "Duplicate", "Invalid", "Incomplete", 
			"Cannot Reproduce", "Later", "Not A Problem", "Implemented", "Done", "Auto Closed", 
			"Pending Closed", "REMIND", RES_FS, "Not A Bug", "Workaround", "Staged", "Delivered", 
			"Information Provided", "Works For Me", "Feedback Received", "Won't Do", "Abandoned"};
	
	
	@FXML
	Button goboxesBtn;
	
	@FXML
	Button showUrlBtn;
	
	@FXML
	CheckComboBox<String> resolutionCCB;
	
	@FXML
	CheckComboBox<String> statusCCB;
	
	@FXML
	CheckComboBox<String> typeCCB;
	
	@FXML
	TextField projnTF;
	
	@FXML
	TextField gitUrlTF;
	
	@FXML
	public void initialize() {
		resolutionCCB.getItems().addAll(Arrays.asList(baseresolution));
		statusCCB.getItems().addAll(Arrays.asList(basestatus));
		typeCCB.getItems().addAll(Arrays.asList(basetypes));
		
		
		gitUrlTF.setText("https://github.com/apache/parquet-mr.git");
		projnTF.setText("PARQUET");
		resolutionCCB.getCheckModel().check("Fixed");
		statusCCB.getCheckModel().check("Closed");
		statusCCB.getCheckModel().check(RES_FS);
		typeCCB.getCheckModel().check("Bug");
		
		resolutionCCB.getItemBooleanProperty(0).addListener(e-> setupAllListener(resolutionCCB) );
	
		statusCCB.getItemBooleanProperty(0).addListener(e-> setupAllListener(statusCCB) );
		
		typeCCB.getItemBooleanProperty(0).addListener(e-> setupAllListener(typeCCB));
		
	
		
		goboxesBtn.setPadding(new Insets(0, 3, 0, 3));
		goboxesBtn.setGraphic(new ImageView(new Image("/resources/images/right_blue_arrow_40x40.png")));
		
		showUrlBtn.setOnMouseClicked(e-> {
			if(checkprojname()) {
				String name = projnTF.getText();
				
				List<List<String>> params = getResolStatNType();

				SniffControl sc = new SniffControl();
				sc.showUrl(name, params.get(0), params.get(1), params.get(2));
			}
		});
		
		goboxesBtn.setOnMouseClicked(e -> {
			if(checkprojname() && checkurlbox()) {
				String name = projnTF.getText();
				String url = gitUrlTF.getText();

				List<List<String>> params = getResolStatNType();

				SniffControl sc = new SniffControl();
				sc.snifJira(name, url, params.get(0), params.get(1), params.get(2));
			}
		});
	}
	
	

	private List<List<String>> getResolStatNType() {
		List<List<String>> p = new ArrayList<>();
		
		List<String> resol = new ArrayList<>(resolutionCCB.getCheckModel().getCheckedItems());
		List<String> stat = new ArrayList<>(statusCCB.getCheckModel().getCheckedItems());
		List<String> typ = new ArrayList<>(typeCCB.getCheckModel().getCheckedItems());
		
		for(int i = 0; i < resol.size(); i++) {
			resol.set(i, removeWhiteSpaces(resol.get(i)));
		}
		
		for(int i = 0; i < stat.size(); i++) {
			stat.set(i, removeWhiteSpaces(stat.get(i)));
		}
		
		for(int i = 0; i < typ.size(); i++) {
			typ.set(i, removeWhiteSpaces(typ.get(i)));
		}
		
		p.add(resol);
		p.add(stat);
		p.add(typ);
		
		return p;
	}
	

	private String removeWhiteSpaces(String s) {
		StringBuilder sb = new StringBuilder();
		
		int i = 0;
		int j = 0;
		
		while(i < s.length()){
			if(s.charAt(i) == ' ') {
				sb.append(s.substring(j, i) + "%20");
				j = i+1;
			}
			
			i++;
		}
		sb.append(s.substring(j));
		
		return sb.toString();
	}

	private void setupAllListener(CheckComboBox<String> ccombox) {
		if(ccombox.getCheckModel().isChecked(0)){
			int n = ccombox.getCheckModel().getItemCount();
			
			for(int i = 1; i < n; i++) {
				ccombox.getCheckModel().check(i);
			}
		}else {
			int n = ccombox.getCheckModel().getItemCount();
			
			for(int i = 1; i < n; i++) {
				ccombox.getCheckModel().clearCheck(i);
			}
		}
	}

	private boolean checkurlbox() {
		if(gitUrlTF.getText() == null || gitUrlTF.getText().trim().isEmpty()) {
			MyUtils.fastAlert("Oops!", "You forgot to enter the url!");
			return false;
		}
		return true;
	}

	private boolean checkprojname() {
		if(projnTF.getText() == null || projnTF.getText().trim().isEmpty()) {
			MyUtils.fastAlert("Oops!", "You forgot to enter github project url!");
			return false;
		}
		return true;
	}


}
