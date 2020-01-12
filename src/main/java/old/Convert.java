package old;

import java.io.IOException;

public class Convert {
	//private static final String PATH = "/Users/behnaz.changizi/Desktop/Dropbox/tz/calculation/";

	public static void main(String[] a) throws IOException {
		//ls();
		//cat("rep.txt");
		//cat("router.txt");
		//cat("sync.txt");
		//cat("fifo.txt");
		//cat("syncprio.txt");
		//cat("prioritysync.txt");
		//cat("priorityfifo.txt");
	//	cat("b.txt", 8);
	}
/*
	private static void cat(String file, int no) throws IOException {
		try (Stream<String> stream = Files.lines(Paths.get(PATH + file))) {
		    String joined = stream
		        .map(String::valueOf)
		        .filter(e -> e.trim().lastIndexOf('\t') > no && e.trim().endsWith("T"))
		      //  .map(e -> e.replaceAll(" \\| ", ","))
		     //   .sorted()
		        .collect(Collectors.joining("\n "));
		    Starter.log("List: " + joined);
		}		
	}

	private static void ls() throws IOException {
		try (Stream<Path> stream = Files.list(Paths.get(PATH))) {
		    String joined = stream
		        .map(String::valueOf)
		        .filter(path -> !path.startsWith("."))
		        .sorted()
		        .collect(Collectors.joining("; "));
		    Starter.log("List: " + joined);
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
		    Starter.log("List: " + joined);
		}
	}*/
}
