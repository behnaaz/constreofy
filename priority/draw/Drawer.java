package priority.draw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.asu.emit.qyan.alg.model.Vertex;
import grph.Grph;
import grph.in_memory.InMemoryGrph;

public class Drawer {
	/**
	 * 
	 */
	String[] content = {
			" [!de1de2ring !el1el2ring !on1on2ring ab1ab2ring jk1jk2ring !cd1cd2ring !fg2fg1ring !lm1lm2ring !gh1gh2ring !ij1ij2ring ] ------ {  } -------> (!cd1cd2xring !el1el2xring !de1de2xring !gh1gh2xring  jk1jk2xring !on1on2xring  ab1ab2xring !ij1ij2xring !fg2fg1xring !lm1lm2xring )  ",

			" [!de1de2ring !el1el2ring !on1on2ring ab1ab2ring jk1jk2ring !cd1cd2ring !fg2fg1ring !lm1lm2ring !gh1gh2ring !ij1ij2ring ] ------ {  jk2tilde k2tilde } -------> (!cd1cd2xring !el1el2xring !de1de2xring !gh1gh2xring !jk1jk2xring !on1on2xring  ab1ab2xring !ij1ij2xring !fg2fg1xring !lm1lm2xring )  ",

			 "[!de1de2ring !el1el2ring !on1on2ring ab1ab2ring jk1jk2ring !cd1cd2ring !fg2fg1ring !lm1lm2ring !gh1gh2ring !ij1ij2ring ] ------ {  k3tilde jk2tilde } -------> (!cd1cd2xring !el1el2xring !de1de2xring !gh1gh2xring !jk1jk2xring !on1on2xring  ab1ab2xring !ij1ij2xring !fg2fg1xring !lm1lm2xring )  ",

			 "[!de1de2ring !el1el2ring !on1on2ring ab1ab2ring jk1jk2ring !cd1cd2ring !fg2fg1ring !lm1lm2ring !gh1gh2ring !ij1ij2ring ] ------ {  b2tilde cd1tilde ab2tilde c1tilde i1tilde i2tilde b3tilde ij1tilde b1tilde c2tilde } -------> ( ij1ij2xring  cd1cd2xring !el1el2xring !ab1ab2xring !de1de2xring !gh1gh2xring  jk1jk2xring !on1on2xring !fg2fg1xring !lm1lm2xring )  ",

		"	 [!de1de2ring !el1el2ring !on1on2ring ab1ab2ring jk1jk2ring !cd1cd2ring !fg2fg1ring !lm1lm2ring !gh1gh2ring !ij1ij2ring ] ------ {  b2tilde cd1tilde ab2tilde c1tilde i1tilde i2tilde b3tilde ij1tilde jk2tilde b1tilde k2tilde c2tilde } -------> ( ij1ij2xring  cd1cd2xring !el1el2xring !ab1ab2xring !de1de2xring !gh1gh2xring !jk1jk2xring !on1on2xring !fg2fg1xring !lm1lm2xring ) ", 

		"	 [!de1de2ring !el1el2ring !on1on2ring ab1ab2ring jk1jk2ring !cd1cd2ring !fg2fg1ring !lm1lm2ring !gh1gh2ring !ij1ij2ring ] ------ {  b2tilde cd1tilde ab2tilde c1tilde i1tilde i2tilde b3tilde ij1tilde k3tilde jk2tilde b1tilde c2tilde } -------> ( ij1ij2xring  cd1cd2xring !el1el2xring !ab1ab2xring !de1de2xring !gh1gh2xring !jk1jk2xring !on1on2xring !fg2fg1xring !lm1lm2xring )  ",

		"	 [!de1de2ring !el1el2ring !on1on2ring ab1ab2ring jk1jk2ring !cd1cd2ring !fg2fg1ring !lm1lm2ring !gh1gh2ring !ij1ij2ring ] ------ {  b2tilde cd1tilde ab2tilde c1tilde i1tilde i3tilde b3tilde b1tilde c2tilde } -------> ( cd1cd2xring !el1el2xring !ab1ab2xring !de1de2xring !gh1gh2xring  jk1jk2xring !on1on2xring !ij1ij2xring !fg2fg1xring !lm1lm2xring )  ",

		"	 [!de1de2ring !el1el2ring !on1on2ring ab1ab2ring jk1jk2ring !cd1cd2ring !fg2fg1ring !lm1lm2ring !gh1gh2ring !ij1ij2ring ] ------ {  b2tilde cd1tilde ab2tilde c1tilde i1tilde i3tilde b3tilde jk2tilde b1tilde k2tilde c2tilde } -------> ( cd1cd2xring !el1el2xring !ab1ab2xring !de1de2xring !gh1gh2xring !jk1jk2xring !on1on2xring !ij1ij2xring !fg2fg1xring !lm1lm2xring )  ",

		"	 [!de1de2ring !el1el2ring !on1on2ring ab1ab2ring jk1jk2ring !cd1cd2ring !fg2fg1ring !lm1lm2ring !gh1gh2ring !ij1ij2ring ] ------ {  b2tilde cd1tilde ab2tilde c1tilde i1tilde i3tilde b3tilde k3tilde jk2tilde b1tilde c2tilde } -------> ( cd1cd2xring !el1el2xring !ab1ab2xring !de1de2xring !gh1gh2xring !jk1jk2xring !on1on2xring !ij1ij2xring !fg2fg1xring !lm1lm2xring )  ",
		"	 [!de1de2ring !el1el2ring !on1on2ring !cd1cd2ring !fg2fg1ring !lm1lm2ring !ab1ab2ring !jk1jk2ring !gh1gh2ring !ij1ij2ring ] ------ {  j2tilde jk1tilde ab1tilde } -------> (!cd1cd2xring !el1el2xring !de1de2xring !gh1gh2xring  jk1jk2xring !on1on2xring  ab1ab2xring !ij1ij2xring !fg2fg1xring !lm1lm2xring )   ",

		"	 [!de1de2ring !el1el2ring !on1on2ring !cd1cd2ring !fg2fg1ring !lm1lm2ring !ab1ab2ring !jk1jk2ring !gh1gh2ring !ij1ij2ring ] ------ {  ab1tilde } -------> (!cd1cd2xring !el1el2xring !de1de2xring !gh1gh2xring !jk1jk2xring !on1on2xring  ab1ab2xring !ij1ij2xring !fg2fg1xring !lm1lm2xring )   ",

		"	[!de1de2ring !el1el2ring !on1on2ring !cd1cd2ring !fg2fg1ring !lm1lm2ring !ab1ab2ring !jk1jk2ring !gh1gh2ring !ij1ij2ring ] ------ {  j2tilde jk1tilde } -------> (!cd1cd2xring !el1el2xring !ab1ab2xring !de1de2xring !gh1gh2xring  jk1jk2xring !on1on2xring !ij1ij2xring !fg2fg1xring !lm1lm2xring ) ",  

		"	 [!de1de2ring !el1el2ring !on1on2ring !cd1cd2ring !fg2fg1ring !lm1lm2ring !ab1ab2ring !jk1jk2ring !gh1gh2ring !ij1ij2ring ] ------ {  } -------> (!cd1cd2xring !el1el2xring !ab1ab2xring !de1de2xring !gh1gh2xring !jk1jk2xring !on1on2xring !ij1ij2xring !fg2fg1xring !lm1lm2xring )   "

	};	

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

    private Map<Integer, ArrayList<Integer>> extractLinks(String[] content, List<String> states) {
    	Map<Integer, ArrayList<Integer>> res = new HashMap<>();
    	for (String s : content) {
    		int begin = s.indexOf("------");
    		String from = s.substring(0, begin).replace("[", "").replace("]", "").trim();
    		int fromIndx = states.indexOf(from);
    		begin = s.indexOf("------->");
    		String to = s.substring(0, begin).replace("(", "").replace(")", "").trim();
    		int toIndx = states.indexOf(to);
    		String lbl;
    		if (res.containsKey(fromIndx)) {
    			res.get(fromIndx).add(toIndx);
    		//	res.put(fromIndx, );
    		}
    		else {
    			ArrayList<Integer> al = new ArrayList<>();
    			al.add(toIndx);
    			res.put(fromIndx, al);
    		}
    	}
    	return res;
	}

	private List<String> extractStates(String[] content) {
    	List <String> states = new ArrayList<>();
    	for (String s : content) {
    		int begin = s.indexOf("------");
    		String state = s.substring(0, begin).replace("[", "").replace("]", "").trim();
    		if (!states.contains(state))
    			states.add(state);
    		
    		begin = s.indexOf("------->");
    		state = s.substring(0, begin).replace("(", "").replace(")", "").trim();
    		if (!states.contains(state))
    			states.add(state);
    	}
		return states;
	}
}
