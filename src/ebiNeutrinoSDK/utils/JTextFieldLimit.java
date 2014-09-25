package ebiNeutrinoSDK.utils;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * JTextFieldLimit format for a JTextField
 * (Experimental code)
 */

public class JTextFieldLimit extends PlainDocument
{
		private int limit;
		
		// optional uppercase conversion
		private boolean toUppercase = false;
		
		public JTextFieldLimit(){
			super();
		}
		
		public JTextFieldLimit(int limit){
			super();
			this.limit = limit;
		}
		public JTextFieldLimit(int limit, boolean upper){
			super();
			this.limit = limit;
			toUppercase = upper;
		}
		public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException{
				if (str == null){
					return;
				}
			
		if ((getLength() + str.length()) <= limit){
			if (toUppercase){
				str = str.toUpperCase();
			}
		 super.insertString(offset, str, attr);
		}
	}
}

