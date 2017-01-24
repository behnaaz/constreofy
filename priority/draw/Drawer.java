package priority.draw;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
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

public class Drawer {
	private static final char SEPARATOR = ';';
	private static final String STATE_DELIMINATOR = ":";
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
    Map<Long, Integer> vertexState = new HashMap<>();
    List<Set<String>> states;
    Map<Integer, ArrayList<Integer>> links = new HashMap<>();
    Map<String, String> linkLabels = new HashMap<>();
    
	public Drawer(List<String> solutions) {
		this.content = solutions;
	}

	public void draw(){
        states = extractStates(content);
		links = extractLinks(content, states);
		
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
        			gb.addDirectedSimpleEdge(stateVertex.get(j), stateVertex.get(i));
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
        print(linkLabels);
    }

    private void print(Map<String, String> linkLabels) {
    	System.out.println("....................");
    	System.out.println(toJSON());
    	//linkLabels.forEach((k,v) -> System.out.println(toJSON(k,v) /*toString(k ,v, true)*/));
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
				jar.put(new JSONObject().put("from", source).put("to", target).put("text", v.replaceAll("tilde", "")));				
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
		return cleanUp(s.substring(begin + TARGET_START_SIGN.length()), OPEN_TAG_PARANTHESIS, CLOSE_TAG_PARANTHESIS);
	}

	private Set<String> getSourceStateName(String s) {
		int begin = s.indexOf(SOURCE_END_SIGN);
		return cleanUp(s.substring(0, begin), OPEN_TAG_BRACKET, CLOSE_TAG_BRACKET);
	}

	private String getEdgeLabel(String s) {
		int begin = s.indexOf("{") + 1;
		int end = s.indexOf("}");
		return s.substring(begin, end).trim();
	}
	
	private Set<String> cleanUp(String state, String openTag, String closeTag) {
		Set<String> res = new TreeSet<>();
		String temp = state.replace(openTag, STRING_EMPTY).replace(closeTag, STRING_EMPTY).trim();
		String[] variables = temp.split(STRING_SPACE);
		StringBuilder result = new StringBuilder();
		for (String v : variables) {
			v = v.replace("xring", "ring").replace("ring", "");
			if (!v.startsWith(PREFIX_NOT) && v.trim().length() > 0)
				result.append(v).append(STRING_COMMA);
		}
		if (result.length() > 0) {
			String[] vars = result.substring(0, result.length() - 1).toString().split(STRING_COMMA);
			for (String v : vars)
				res.add(v);
		}

		return res;
	}
}