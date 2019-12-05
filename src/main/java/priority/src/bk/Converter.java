package priority.src.bk;

import java.util.ArrayList;

public class Converter {
	public static ArrayList<Character> convert(char... c) {
		ArrayList<Character> result = new ArrayList<Character>();
		for (int i = 0; i < c.length; i++) {
			result.add(c[i]);
		}
		return result;
	}

	public static ArrayList<String> convert(String... c) {
		ArrayList<String> result = new ArrayList<String>();
		for (int i = 0; i < c.length; i++) {
			result.add(c[i]);
		}
		return result;
	}
}
