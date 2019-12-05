package priority.src.priority.init;

import java.util.Date;

import priority.common.Constants;

public class FileUser implements Constants {
	protected static final String OUTPUTFILE = "abc" + new Date().toString().replaceAll("\\ |\\:", STRING_EMPTY) + TXT;
}
