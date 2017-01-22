package priority.init;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import priority.common.Constants;
import priority.draw.Drawer;
import priority.semantics.AbstractSemantics;
import priority.solving.Solver;

public class Starter extends AbstractSemantics implements Constants {
	public byte[] readFile(File file) {
		try {
			if (file.exists() && file.canRead()) {
				byte[] lines = Files.readAllBytes(file.toPath());
				return lines;
			}
		} catch (IOException e) {
			// e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) throws Exception {
		List<String> solutions = new Solver().solve(-1);
         new Drawer(solutions).draw();
	}

	
}