import java.io.IOException;
import java.util.prefs.BackingStoreException;

import controller.Base;
import views.MainView;

public class Warehouse {

	public static void main(String[] args) throws IOException, BackingStoreException {
		// TODO Auto-generated method stub
		Base.LoadBasics();
		//ExcelFile.CheckIfFileIsEdited();

		new MainView();

	}
}
