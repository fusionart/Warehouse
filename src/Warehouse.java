import java.io.IOException;
import java.util.prefs.BackingStoreException;

import javax.swing.JOptionPane;

import Controller.Base;
import Controller.ExcelFile;
import View.MainView;

public class Warehouse {

	public static void main(String[] args) throws IOException, BackingStoreException {
		// TODO Auto-generated method stub
		Base.LoadBasics();
		ExcelFile.CheckIfFileIsEdited();
		
		new MainView();
	}

}
