package controller;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class UpcaseFilter extends DocumentFilter {
	public void insertString(DocumentFilter.FilterBypass fb, int offset, String text, AttributeSet attr)
			throws BadLocationException {
		fb.insertString(offset, text.toUpperCase(), attr);
	}

	// no need to override remove(): inherited version allows all removals

	public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attr)
			throws BadLocationException {
		fb.replace(offset, length, text.toUpperCase(), attr);
	}
}
