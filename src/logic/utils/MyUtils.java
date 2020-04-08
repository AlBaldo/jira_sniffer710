package logic.utils;

import java.awt.Image;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class MyUtils {
    
    public static void fastAlert(String title, String message) {

		JTextArea msg = new JTextArea(message.length()/40, message.length()/5);
		msg.setEditable(false);
		msg.setLineWrap(true);
		msg.setWrapStyleWord(true);
		msg.setText(message);
		JOptionPane pane = new JOptionPane(msg, JOptionPane.ERROR_MESSAGE);;
		msg.setBackground(pane.getBackground());
		JDialog dialog = pane.createDialog(null, title);
		dialog.setVisible(true);
    }
    
    public static boolean ynAlert(String title, String message) {
    	return JOptionPane.showConfirmDialog(new JFrame(), message, title, JOptionPane.YES_NO_OPTION) == 0;
    }
    
    public static Icon resizeIcon(ImageIcon icon, int resizedWidth, int resizedHeight) {
        Image img = icon.getImage();  
        Image resizedImage = img.getScaledInstance(resizedWidth, resizedHeight,  java.awt.Image.SCALE_SMOOTH);  
        return new ImageIcon(resizedImage);
    }
    
    
    private MyUtils(){}

	public static void revertList(List<String> li) {
		int isize = li.size() - 1;
		int max = isize / 2;
		String tmp;
		
		for(int i = 0; i <= max; i++) {
			tmp = li.get(i);
			li.set(i, li.get(isize-i));
			li.set(isize-i, tmp);
		}
	}

	public static boolean isANumber(char c) {
		switch(c) {
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				return true;
			default:
				return false;
		}
	}

	public static String getMonthValueOf(int month) {
		switch (month) {
			case 1:
				return "Jan";
			case 2:
				return "Feb";
			case 3:
				return "Mar";
			case 4:
				return "Apr";
			case 5:
				return "May";
			case 6:
				return "Jun";
			case 7:
				return "Jul";
			case 8:
				return "Aug";
			case 9:
				return "Sep";
			case 10:
				return "Oct";
			case 11:
				return "Nov";
			case 12:
				return "Dec";
			default:
				return "";
		}
	}
}