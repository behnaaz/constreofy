package priority;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

public class Constraint extends AbstractSemantics {
	private static final String SHUT = "shut";
	static final String TILDE = "tilde";
	static final String CIRC = "circ";
	static final String BULLET = "bullet";
	static final String CURRENT_MEMORY = "ring";
	static final String NEXT_MEMORY = "xring";
	
	static final String OUTPUTFILE = "abc" + new Date().toString().replaceAll("\\ |\\:", "") + ".txt";
	static final String CNFFILE = "/Users/behnaz.changizi/reoworkspace/priority/src/Users/behnaz.changizi/Desktop/Dropbox/sol.txt";
	static final String IMPLIES = " impl ";
	static final String RIGHTLEFTARROW = " equiv ";
    static final String NOT = " not ";
    static final String OR = " or ";
	static final String AND = " and ";
    static final String TRUE = " true ";
    static final String FALSE = " false ";

	private static final String REDUCE_PROGRAM = "/Users/behnaz.changizi/Desktop/reduce/trunk/bin/redpsl";

	byte[] readFile(File file) {
		try {
			if (file.exists() && file.canRead()) {
				byte[] lines = Files.readAllBytes(file.toPath());
				return lines;
							}
		} catch (IOException e) {
			//e.printStackTrace();
		}
		return null;	
	}

	public static void main(String[] args) throws Exception {
		List<Map<String, Boolean>> visited = new ArrayList<Map<String, Boolean>>();
		List<Map<String, Boolean>> explorableStates = new ArrayList<Map<String, Boolean>>();
		Map<String, Boolean> currentStatesValues = ImmutableMap.of(new ConnectorFactory().mem("ab1", "ab2"), false);
		do {
		File file = createFile(OUTPUTFILE);
		ConstraintConnector cc = new ExampleMaker(3, new OutputStreamWriter(new FileOutputStream(file))).getExample(currentStatesValues);
		System.out.println("Constraint is: " + cc.constraint);
		System.out.println("State is: " + cc.states().toString());
		System.out.println("Xstate is: " + cc.nextStates());

		visited.add(currentStatesValues);

		writeToFile(CNFFILE, getReduceOutput(file, new Constraint()));

		DNF dnf = new DNF(CNFFILE, Lists.newArrayList(cc.variables()), Lists.newArrayList(cc.states()), Lists.newArrayList(cc.nextStates()));
		dnf.prepareForSat4j(new FileWriter(OUTPUTFILE));
		dnf.reportVars();

		dnf.reportSolutions();
		dnf.printFlows();
		dnf.printFlowsNPriority();
		List<Map<String, Boolean>> nexts = dnf.stateValues();		
		for (Map<String, Boolean> i : nexts) {
			Map<String, Boolean> temp = find(i, visited);
			if (temp == null)
				explorableStates.add(i);
		}

		currentStatesValues = (explorableStates.size() > 0) ?makeItCurrent(explorableStates.remove(0)) : null;
		
		} while (currentStatesValues != null);
	}

	public static Map<String, Boolean> makeItCurrent(Map<String, Boolean> list) {
		Map<String, Boolean> temp = new HashMap<String, Boolean>();
		for (String s : list.keySet()) {
			temp.put(s.toLowerCase().replace("xring", "ring"), list.get(s));
		}
		return temp;
	}

	private static Map<String, Boolean> find(Map<String, Boolean> elem, List<Map<String, Boolean>> list) {
		Map<String, Boolean> result = null;
		for (Map<String, Boolean> t : list) {
			if (result == null)
				result = exists(elem, t);
		}
		return result;
	}

	private static Map<String, Boolean> exists(Map<String, Boolean> elem, Map<String, Boolean> t) {
		for (String key : elem.keySet()) {
			if (!t.containsKey(key) || t.get(key) != elem.get(key)) {
				return null;
			}
		}
		return elem;
	}

	private static List<String> getReduceOutput(File file, Constraint constraint) throws IOException {
		Process process = Runtime.getRuntime().exec(REDUCE_PROGRAM);
		OutputStream stdin = process.getOutputStream();
		stdin.write(constraint.readFile(file));
		stdin.flush();
		stdin.close();
		
		List<String> output = new ArrayList<String>();
		BufferedReader out = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line;
		while ((line = out.readLine()) != null)
			output.add(line);
		return output;
	}

	private static File createFile(String fileName) throws IOException {
		File file = new File(fileName);
		if (file.exists())
			file.delete();
		if (!file.exists())
			file.createNewFile();
		return file;
	}

	private static void writeToFile(String cnffile, List<String> content) throws IOException {
		System.out.println("Going to write into " + cnffile);
		File output = createFile(cnffile);
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(output));
		System.out.println("Going to extract solutions from " + content);
		osw.write(extractSolution(content));
		osw.flush();
		osw.close();
	}

	private static String extractSolution(List<String> content) {
		int start = findResultStart(content);
		if (start > -1) {
			StringBuilder sb = new StringBuilder();
			for (int i = start; i < content.size() - 1 && !content.get(i).contains(SHUT); i++) {
				if (content.get(i).trim().length() > 0)
					sb.append(content.get(i));
			}
			return sb.toString();
		}
		return null;
	}

	private static int findResultStart(List<String> content) {
		for (int i = content.lastIndexOf(SHUT)-1; i > -1; i--) {
			if (content.get(i-1).trim().length() == 0 && content.get(i-2).trim().length() == 0) {
				if (content.get(i).trim().endsWith(":") && Integer.parseInt(content.get(i).replaceAll(":", "").trim()) > 0)
					i++;
				return i;
			}
		}

		return -1;
	}
}