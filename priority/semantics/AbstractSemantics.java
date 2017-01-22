package priority.semantics;

import java.util.ArrayList;

import javax.swing.JFrame;

public class AbstractSemantics extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected ArrayList<Character> convert(Character... c) {
		ArrayList<Character> result = new ArrayList<Character>();
		for (int i = 0; i < c.length; i++) {
			result.add(c[i]);
		}
		return result;
	}
}
