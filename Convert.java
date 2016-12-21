import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Convert {
	private static final String PATH = "/Users/behnaz.changizi/Desktop/Dropbox/tz/calculation/";

	public static void main(String[] a) throws IOException {
		//ls();
		//cat("rep.txt");
		//cat("router.txt");
		//cat("sync.txt");
		//cat("fifo.txt");
		//cat("syncprio.txt");
		//cat("prioritysync.txt");
		//cat("priorityfifo.txt");
		cattt("b.txt", 8);
	}

	private static void cattt(String file, int no) throws IOException {
		try (Stream<String> stream = Files.lines(Paths.get(PATH + file))) {
		    String joined = stream
		        .map(String::valueOf)
		        .filter(e -> e.trim().lastIndexOf('\t') > no && e.trim().endsWith("T"))
		      //  .map(e -> e.replaceAll(" \\| ", ","))
		     //   .sorted()
		        .collect(Collectors.joining("\n "));
		    System.out.println("List: " + joined);
		}		
	}

	private static void ls() throws IOException {
		try (Stream<Path> stream = Files.list(Paths.get(PATH))) {
		    String joined = stream
		        .map(String::valueOf)
		        .filter(path -> !path.startsWith("."))
		        .sorted()
		        .collect(Collectors.joining("; "));
		    System.out.println("List: " + joined);
		}
	}

	private static void cat(String file) throws IOException {
		try (Stream<String> stream = Files.lines(Paths.get(PATH+file))) {
		    String joined = stream
		        .map(String::valueOf)
		        .filter(e -> !e.trim().endsWith("F"))
		      //  .map(e -> e.replaceAll(" \\| ", ","))
		     //   .sorted()
		        .collect(Collectors.joining("\n "));
		    System.out.println("List: " + joined);
		}
	}
}
//