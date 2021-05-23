package controller;

import java.awt.print.*;
import java.util.Iterator;
import java.util.List;

import model.PalletModel;

import java.awt.*;

public class PrintOutcome implements Printable {

	private List<PalletModel> pmList;

	public PrintOutcome(List<PalletModel> pmList) {
		// TODO Auto-generated constructor stub
		this.pmList = pmList;
	}

	public int print(Graphics g, PageFormat pf, int page) throws PrinterException {

		// We have only one page, and 'page'
		// is zero-based
		if (page > 0) {
			return NO_SUCH_PAGE;
		}

		// User (0,0) is typically outside the
		// imageable area, so we must translate
		// by the X and Y values in the PageFormat
		// to avoid clipping.
		Graphics2D g2d = (Graphics2D) g;
		g2d.translate(pf.getImageableX(), pf.getImageableY());

		// Now we perform our rendering
		g.drawString("Изписано количество:", 50, 80);
		g.drawString("Складово място", 50, 100);
		g.drawString("Тип Батерия", 200, 100);
		g.drawString("Количество", 350, 100);

		int y = 115;
		
		Iterator itr = pmList.iterator();
		while (itr.hasNext()) {
			PalletModel pm = (PalletModel) itr.next();
			
			g.drawString(pm.getPalletName(), 50, y);
			g.drawString(pm.getBatteryType(), 200, y);
			g.drawString(String.valueOf(pm.getQuantityReal()), 350, y);
			
			y+=13;
		}

		// tell the caller that this page is part
		// of the printed document
		return PAGE_EXISTS;
	}
}