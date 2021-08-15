package controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        if(!containsLatinCharsAndNumbers(text)) return;
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
			if(!containsLatinCharsAndNumbers(text)) return;
			super.replace(fb, offset, length, text.toUpperCase(), attr);
		}
		
		if (text.length() == 0) {
			fb.replace(offset, length, text, attr);
		}
		
		//fb.replace(offset, length, text.toUpperCase(), attr);
	}
	
	public boolean containsLatinCharsAndNumbers(String text)
    {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9 _.-]*$");
        //"^[a-zA-Z]+$"
        Matcher matcher = pattern.matcher(text);
        boolean isMatch = matcher.matches();
        return isMatch;
    }
}
