package priority.src.priority.init;

import java.util.Date;

public class FileUser {
	public static final String STRING_EMPTY = "";
	public 	static final String TXT =".txt";
	protected static final String OUTPUTFILE = "abc" + new Date().toString().replaceAll("\\ |\\:", STRING_EMPTY) + TXT;
}
