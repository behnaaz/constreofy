package priority.primitives;

import java.util.Map;

import priority.common.Constants;
import priority.connector.ConstraintConnector;

public class FIFO extends Primitive implements Constants {

	private boolean full;
	private String p1;
	private String p2;
	private Map<String, Boolean> currentStatesValues;

	public FIFO(String p1, String p2, Boolean initState) {//TODO delete
		full = initState;
		this.p1 = p1;
		this.p2 = p2;
	}
	
	public FIFO(String p1, String p2, Map<String, Boolean> currentStatesValues) {
		this.currentStatesValues = currentStatesValues;
		this.p1 = p1;
		this.p2 = p2;
	}

	public ConstraintConnector constraint() {
		full = false;
		String m = memory(p1.toLowerCase(), p2.toLowerCase());
		if (currentStatesValues.containsKey(m))
			full = currentStatesValues.get(m);

		String fifo;
		if (full)
			fifo = String
					.format("( " + NOT + "%s )" + AND + " %s " + AND + "(%s" + RIGHTLEFTARROW + "( " + NOT + "%s )) "// +
																														// AND
																														// +
			// "(%s" + IMPLIES + "(%s" + ")) " + AND +
			// "(%s" + IMPLIES + "(%s" + AND + "("+ NOT +"%s))) " + AND +
			// (full ? "%s" : "%s")
							, flow(p1), memory(p1, p2), flow(p2), nextMemory(p1, p2)// ,
			// flow(p1), nextMem(p1, p2),
			// flow(p2), mem(p1, p2, full), nextMem(p1, p2),
			// mem(p1, p2, full), nextMem(p1, p2)
			);
		/*
		 * fifo = String.format( "(" + NOT + "(%s" + OR + "%s)" + IMPLIES +
		 * " (%s" + RIGHTLEFTARROW + "%s)) " + AND + "(%s" + IMPLIES + "(%s" +
		 * AND + "("+ NOT +"%s))) " + AND + "(%s" + IMPLIES + "(%s" + AND + "("+
		 * NOT +"%s))) " + AND + (full ? "%s" : "%s") , flow(p1), flow(p2),
		 * mem(p1, p2, full), nextMem(p1, p2), flow(p1), nextMem(p1, p2),
		 * mem(p1, p2, full), flow(p2), mem(p1, p2, full), nextMem(p1, p2),
		 * mem(p1, p2, full), nextMem(p1, p2) );
		 */
		else
			fifo = String
					.format("( " + NOT + "%s )" + AND + "( " + NOT + "%s )" + AND + "(%s" + RIGHTLEFTARROW + " %s) "// +
																													// AND
																													// +
			// "(%s" + IMPLIES + "(%s" + ")) " + AND +
			// "(%s" + IMPLIES + "(%s" + AND + "("+ NOT +"%s))) " + AND +
			// (full ? "%s" : "%s")
							, flow(p2), memory(p1, p2), flow(p1), nextMemory(p1, p2)// ,
			// flow(p1), nextMem(p1, p2),
			// flow(p2), mem(p1, p2, full), nextMem(p1, p2),
			// mem(p1, p2, full), nextMem(p1, p2)
			);
		return new ConstraintConnector(fifo, p1, p2, memory(p1, p2), nextMemory(p1, p2));
	}

	public String memory() {
		return memory(p1, p2);
	}

	public Boolean full() {
		return full;
	}
}
