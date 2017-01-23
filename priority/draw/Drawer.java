package priority.draw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import grph.Grph;
import grph.in_memory.InMemoryGrph;

public class Drawer {
	private static final String CLOSE_TAG_BRACKET = "]";
	private static final String OPEN_TAG_BRACKET = "[";
	private static final String CLOSE_TAG_PARANTHESIS = ")";
	private static final String OPEN_TAG_PARANTHESIS = "(";
	private static final String STRING_COMMA = ",";
	private static final String PREFIX_NOT = "!";
	private static final String STRING_SPACE = " ";
	private static final String STRING_EMPTY = "";
	private static final String SOURCE_END_SIGN = "------";
	private static final String TARGET_START_SIGN = "------->";
	/**
	 * 
	 */
	List<String> content;

	public Drawer(List<String> solutions) {
		this.content = solutions;
	}

	public void draw(){
        // Circular Surface
		List<String> states = extractStates(content);
		Map<Integer, ArrayList<Integer>> links = extractLinks(content, states);
		
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
       // Creating a 4x4 grid and display it in a graphical view:
        Grph gb = new InMemoryGrph();
        Map<Integer, Long> stateVertex = new HashMap<>();
        for (int i = 0; i < states.size(); i++) {
        	Long v = gb.addVertex();
        	stateVertex.put(i, v);
        }
        for (int i = 0; i < states.size(); i++) {
        	List<Integer> targets = links.get(i);
        	if (targets != null) {
        		for (Integer j : targets) {
        			gb.addDirectedSimpleEdge(stateVertex.get(j), stateVertex.get(i));
        		}
        	}
        }
        gb.display();
    }

    private Map<Integer, ArrayList<Integer>> extractLinks(List<String> solutions, List<String> states) {
    	Map<Integer, ArrayList<Integer>> res = new HashMap<>();
    	for (String s : solutions) {
    		String from = getSourceStateName(s);
    		int fromIndx = states.indexOf(from);
    		String to = getTargetStateName(s);
    		int toIndx = states.indexOf(to);
    		String lbl = getLabel(s);
    		if (res.containsKey(fromIndx))
    			res.get(fromIndx).add(toIndx);
    		else {
    			ArrayList<Integer> al = new ArrayList<>();
    			al.add(toIndx);
    			res.put(fromIndx, al);
    		}
    	}
    	return res;
	}

	private List<String> extractStates(List<String> solutions) {
    	List <String> states = new ArrayList<>();
    	for (String s : solutions) {
			String state = getSourceStateName(s);
    		if (!states.contains(state))
    			states.add(state);
    		
    		state = getTargetStateName(s);
    		if (!states.contains(state))
    			states.add(state);
    	}
		return states;
	}

	private String getTargetStateName(String s) {
		int begin = s.indexOf(TARGET_START_SIGN);
		String state = cleanUp(s.substring(begin + TARGET_START_SIGN.length()), OPEN_TAG_PARANTHESIS, CLOSE_TAG_PARANTHESIS);
		return state;
	}

	private String getSourceStateName(String s) {
		int begin = s.indexOf(SOURCE_END_SIGN);
		String state = cleanUp(s.substring(0, begin), OPEN_TAG_BRACKET, CLOSE_TAG_BRACKET);
		return state;
	}

	private String getLabel(String s) {
		int begin = s.indexOf(CLOSE_TAG_BRACKET);
		int end = s.indexOf(OPEN_TAG_PARANTHESIS);
		return s.substring(begin, end).trim();
	}
	
	private String cleanUp(String state, String openTag, String closeTag) {
		String temp = state.replace(openTag, STRING_EMPTY).replace(closeTag, STRING_EMPTY).trim();
		String[] variables = temp.split(STRING_SPACE);
		StringBuilder result = new StringBuilder();
		for (String v : variables) {
			if (!v.startsWith(PREFIX_NOT) && v.trim().length() > 0)
				result.append(v).append(STRING_COMMA);
		}
		if (result.length() > 0)
			return result.substring(0, result.length() - 2).toString();

		return STRING_EMPTY;
	}
}