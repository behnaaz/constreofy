package priority.init;

import java.util.Date;

public class FileUser {
	protected static final String OUTPUTFILE = "abc" + new Date().toString().replaceAll("\\ |\\:", "") + ".txt";
}
