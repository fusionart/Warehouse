import java.io.IOException;
import java.util.prefs.BackingStoreException;

import Controller.Base;
import View.MainView;

public class Warehouse {

	public static void main(String[] args) throws IOException, BackingStoreException {
		// TODO Auto-generated method stub
		Base.LoadBasics();
		
		new MainView();
	}

}
