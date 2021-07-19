package controller;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class UpcaseFilter extends DocumentFilter {
	
	private int limit;
	
	public UpcaseFilter(int limit) {
		super();
		this.limit = limit;
	}

	public void insertString(DocumentFilter.FilterBypass fb, int offset, String text, AttributeSet attr)
			throws BadLocationException {
		fb.insertString(offset, text.toUpperCase(), attr);
	}

	// no need to override remove(): inherited version allows all removals

	public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attr)
			throws BadLocationException {
		int currentLength = fb.getDocument().getLength();
		int overLimit = (currentLength + text.length()) - limit - length;
		if (overLimit > 0) {
			text = text.substring(0, text.length() - overLimit);
		}
		if (text.length() > 0) {
			super.replace(fb, offset, length, text.toUpperCase(), attr);
		}
		
		
		//fb.replace(offset, length, text.toUpperCase(), attr);
	}
}
