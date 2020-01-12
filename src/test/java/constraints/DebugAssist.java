package constraints;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class DebugAssist {
	public static void main(String[] args) throws IOException {
		Files.lines(Paths.get("/Users/behnaz.changizi/reoworkspace/priority/src/log.txt")).filter(s -> 
			//s.startsWith("Step")
		//s.contains("explorable states")
		s.contains("visit states")
		).filter(DebugAssist::redundant).forEach(System.out::println);
	}

	private static boolean redundant(String s) {
		Set<String> buffer = new HashSet<>();
		String[] parts = s.replace("]",", ").split(", ");
		for (String p : parts) {
			if (buffer.contains(p))
				System.err.println("REDUNDANT " + p);
			buffer.add(p);
		}
		return buffer.size() < parts.length;
	}
}
