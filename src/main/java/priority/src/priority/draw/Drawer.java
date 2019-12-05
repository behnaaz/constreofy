package priority.src.priority.draw;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.miv.graphstream.distributed.json.JSONArray;
import org.miv.graphstream.distributed.json.JSONException;
import org.miv.graphstream.distributed.json.JSONObject;

import grph.Grph;
import grph.in_memory.InMemoryGrph;
import grph.properties.Property;
import priority.common.Constants;
import priority.solving.IOAwareSolution;
import priority.src.priority.solving.IOAwareSolution;

public class Drawer {
	static final boolean USE_EQUAL_SET_ON = true;

	static final String REDUCE_PROGRAM = "/Users/behnaz.changizi/Desktop/reduce/trunk/bin/redpsl";
	static final String PREAMBLE = "set_bndstk_size 100000;load_package \"redlog\";rlset ibalp;";
	static final String WORD_BOUNDARY = "\\b";
	static final String FORMULA_NAME = "qaz";
	static final String FORMULA_NAME_EQUAL = "qaz :=";
	static final String SHUT = "shut";
	static final String TILDE = "tilde";
	static final String CIRC = "circ";
	static final String BULLET = "bullet";
	static final String CURRENT_MEMORY = "ring";
	static final String NEXT_MEMORY = "xring";
	static final String IMPLIES = " impl ";
	static final String RIGHTLEFTARROW = " equiv ";
	static final String NOT = " not ";
	static final String OR = " or ";
	static final String AND = " and ";
	static final String TRUE = " true ";
	static final String FALSE = " false ";
	static final char SEPARATOR = ';';
	static final String SPACE =  " ";
	static final String AMPER =  "&";//???TODO
	static final String STATE_DELIMINATOR = ":";
	static final String CLOSE_TAG_BRACKET = "]";
	static final String OPEN_TAG_BRACKET = "[";
	static final String CLOSE_TAG_PARANTHESIS = ")";
	static final String OPEN_TAG_PARANTHESIS = "(";
	static final String STRING_COMMA = ",";
	static final String PREFIX_NOT = "!";
	static final String STRING_EMPTY = "";
	static final String SOURCE_END_SIGN = "------";
	static final String TARGET_START_SIGN = "------->";
	static final String TXT =".txt";
	/**
	 * 
	 */
	List<String> content;
    Map<Long, Integer> vertexState = new HashMap<>();
    List<Set<String>> states;
    Map<Integer, ArrayList<Integer>> links = new HashMap<>();
    Map<String, String> linkLabels = new HashMap<>();
    
	public Drawer(Set<IOAwareSolution> solutions) {
		this.content = solutionsToList(solutions, false);
		init();
	}

	public Drawer(List<IOAwareSolution> solutions) {
		this.content = solutionsToList(solutions, false);
		init();
	}

	public void init() {
		states = extractStates(content);
		links = extractLinks(content, states);
	}
	
	List<String> solutionsToList(Set<IOAwareSolution> solutions, boolean withPriority) {
		List<String> list = new ArrayList<>();
		for (IOAwareSolution s : solutions) {
			list.add(s.getSolution().toString(withPriority));
		}
		return list;
	}

	List<String> solutionsToList(List<IOAwareSolution> solutions, boolean withPriority) {
		List<String> list = new ArrayList<>();
		for (IOAwareSolution s : solutions) {
			StringBuilder sb = new StringBuilder();
			sb.append(s.getSolution().toString(withPriority));
			sb.append(Arrays.toString(s.getPreIOs()));
			sb.append(Arrays.toString(s.getPostIOs()));
			list.add(sb.toString());
		}
		return list;
	}


	public void draw(){	
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
       // Creating a 4x4 grid and display it in a graphical view:
        Grph gb = new InMemoryGrph();
        Map<Integer, Long> stateVertex = new HashMap<>();
        for (int i = 0; i < states.size(); i++) {
        	Long v = gb.addVertex();
        	stateVertex.put(i, v);
        	vertexState.put(v, i);
        	gb.setVerticesLabel(new Property("verticesLabel"){

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public long getMemoryFootprintInBytes() {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public void toGrphBinary(ObjectOutput os) throws IOException {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void fromGrphBinary(ObjectInput is) throws IOException {
					// TODO Auto-generated method stub
					
				}

				@Override
				public boolean isSetted(long id) {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public void setValueAsText(long e, String value) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void unset(long id) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void unsetItAll() {
					// TODO Auto-generated method stub
					
				}

				@Override
				public String getValueAsString(long e) {
					Integer state = vertexState.get(e);//TODO ???
					if (state != null && state > -1)
						return states.get(state).toString();
					return "???";
				}

				@Override
				public boolean haveSameValues(long a, long b) {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public void cloneValuesTo(Property p) {
					// TODO Auto-generated method stub
					
				}});
        }
        for (int i = 0; i < states.size(); i++) {
        	List<Integer> targets = links.get(i);
        	if (targets != null) {
        		for (Integer j : targets) {
        			gb.addDirectedSimpleEdge(stateVertex.get(i), stateVertex.get(j));
        			gb.setEdgesLabel(new Property("edgesLabel") {
        				
        				/**
        				 * 
        				 */
        				private static final long serialVersionUID = 1L;

        				@Override
        				public long getMemoryFootprintInBytes() {
        					// TODO Auto-generated method stub
        					return 0;
        				}
        				
        				@Override
        				public void unsetItAll() {
        					// TODO Auto-generated method stub
        					
        				}
        				
        				@Override
        				public void unset(long id) {
        					// TODO Auto-generated method stub
        					
        				}
        				
        				@Override
        				public void toGrphBinary(ObjectOutput os) throws IOException {
        					// TODO Auto-generated method stub
        					
        				}
        				
        				@Override
        				public void setValueAsText(long e, String value) {
        					// TODO Auto-generated method stub
        					String g = value;
        					g = g + "";
        					
        				}
        				
        				@Override
        				public boolean isSetted(long id) {
        					// TODO Auto-generated method stub
        					return true;
        				}
        				
        				@Override
        				public boolean haveSameValues(long a, long b) {
        					// TODO Auto-generated method stub
        					return false;
        				}
        				
        				@Override
        				public String getValueAsString(long e) {
        					////Integer i = linkLabels.get(e);
        					//if (i != null && i > -1)
        						//return states.get(i);
        					return null;//e + "xx";
        				}
        				
        				@Override
        				public void fromGrphBinary(ObjectInput is) throws IOException {
        					// TODO Auto-generated method stub
        					
        				}
        				
        				@Override
        				public void cloneValuesTo(Property p) {
        					// TODO Auto-generated method stub
        					
        				}
        			});
        		}
        	}
        }
        gb.display();
    }
	
	 public String toGoJS() {
    	String result = toJSON().toString();
    	System.out.println("....................");
    	System.out.println(result);
     	//linkLabels.forEach((k,v) -> System.out.println(toJSON(k,v) /*toString(k ,v, true)*/));
    	return result;
	}

    private JSONObject toJSON() {
    	JSONObject res = new JSONObject();
    	try {
        	res.put("nodeKeyProperty", "id");
			res.put("nodeDataArray", statesTOJSONArray());
			res.put("linkDataArray", linksToJSONArray());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return res;
    }
   
	private JSONArray linksToJSONArray() {
		JSONArray jar = new JSONArray();
		linkLabels.forEach((k, v) -> {
			int indexOfStateDelim = k.indexOf(STATE_DELIMINATOR);
			String source = k.substring(0, indexOfStateDelim);
			String target = k.substring(indexOfStateDelim + 1);

			try {
				String[] labels = ((v.endsWith(",")) ? v.concat(" "):v).split(",");
				for (String label : labels)
					jar.put(new JSONObject().put("from", source).put("to", target).put("text", label.replaceAll("tilde", "")));				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		return jar;
	}

	private JSONArray statesTOJSONArray() {
		JSONArray jar = new JSONArray();
		vertexState.forEach((k, v) -> {
			try {
				jar.put(new JSONObject().put("id", k).put("text", states.get(v)));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		return jar;
	}

	private String toString(String k, String v, boolean csv) {
		StringBuilder sb = new StringBuilder();
		int indexOfStateDelim = k.indexOf(STATE_DELIMINATOR);
		String source = k.substring(0, indexOfStateDelim);
		sb.append(source);
		sb.append(SEPARATOR);
		sb.append(states.get(Integer.parseInt(source)));
		sb.append(SEPARATOR);
		sb.append(v);
		sb.append(SEPARATOR);
		String target = k.substring(indexOfStateDelim + 1);
		sb.append(target);
		sb.append(SEPARATOR);
		sb.append(states.get(Integer.parseInt(target)));
		return sb.toString();
	}

	private Map<Integer, ArrayList<Integer>> extractLinks(List<String> solutions, List<Set<String>> states2) {
    	Map<Integer, ArrayList<Integer>> res = new HashMap<>();
    	for (String s : solutions) {
    		Set<String> from = getSourceStateName(s);
    		int fromIndx = states2.indexOf(from);
    		Set<String> to = getTargetStateName(s);
    		int toIndx = states2.indexOf(to);
    		if (res.containsKey(fromIndx))
    			res.get(fromIndx).add(toIndx);
    		else {
    			ArrayList<Integer> al = new ArrayList<>();
    			al.add(toIndx);
    			res.put(fromIndx, al);
    		}
    		String lbl = getEdgeLabel(s);
    		if (linkLabels.containsKey(fromIndx + STATE_DELIMINATOR + toIndx))
    			lbl = linkLabels.get(fromIndx + STATE_DELIMINATOR + toIndx).concat(",").concat(lbl);
    		linkLabels.put(fromIndx + STATE_DELIMINATOR + toIndx, lbl);
    	}
    	return res;
	}

	private List<Set<String>> extractStates(List<String> solutions) {
    	List<Set<String>> states = new ArrayList<>();
    	for (String s : solutions) {
			Set<String> state = getSourceStateName(s);
    		if (!states.contains(state))
    			states.add(state);
    		
    		state = getTargetStateName(s);
    		if (!states.contains(state))
    			states.add(state);
    	}
		return states;
	}

	private Set<String> getTargetStateName(String s) {
		int begin = s.indexOf(TARGET_START_SIGN);
		String io = s.substring(s.lastIndexOf(OPEN_TAG_BRACKET)).replace(OPEN_TAG_BRACKET, "").replace(CLOSE_TAG_BRACKET, "");
		String res = s.substring(begin + TARGET_START_SIGN.length());
		res = res.substring(0, res.indexOf(CLOSE_TAG_PARANTHESIS));		
		res = res.concat(io);
		return cleanUp(res, OPEN_TAG_PARANTHESIS, CLOSE_TAG_PARANTHESIS);
	}

	private Set<String> getSourceStateName(String s) {
		int begin = s.indexOf(SOURCE_END_SIGN);
		String res = s.substring(0, begin);
		int idx = s.indexOf(CLOSE_TAG_PARANTHESIS) + 1;
		String temp = s.substring(idx);
		res = res.concat(temp.substring(temp.indexOf(OPEN_TAG_BRACKET), temp.indexOf(CLOSE_TAG_BRACKET)));
		return cleanUp(res, OPEN_TAG_BRACKET, CLOSE_TAG_BRACKET);
	}

	private String getEdgeLabel(String s) {
		int begin = s.indexOf("{") + 1;
		int end = s.indexOf("}");
		return s.substring(begin, end).trim();
	}
	
	private Set<String> cleanUp(String state, String openTag, String closeTag) {
		Set<String> res = new TreeSet<>();
		String temp = state.replace(openTag, STRING_EMPTY).replace(closeTag, STRING_EMPTY).trim();
		String[] variables = temp.split(SPACE);
		StringBuilder result = new StringBuilder();
		for (String v : variables) {
			v = v.replace(NEXT_MEMORY, CURRENT_MEMORY).replace(CURRENT_MEMORY, STRING_EMPTY);
			if (!v.startsWith(PREFIX_NOT) && v.trim().length() > 0)
				result.append(v).append(STRING_COMMA);
		}
		if (result.length() > 0) {
			String[] vars = result.substring(0, result.length() - 1).split(STRING_COMMA);
			for (String v : vars)
				res.add(v);
		}

		return res;
	}
}