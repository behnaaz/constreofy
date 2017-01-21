package priority.semantics;

import java.util.ArrayList;

public class AbstractSemantics {
	protected ArrayList<Character> convert(Character... c) {
		ArrayList<Character> result = new ArrayList<Character>();
		for (int i = 0; i < c.length; i++) {
			result.add(c[i]);
		}
		return result;
	}
}
