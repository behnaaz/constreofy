package priority.primitives;

import java.util.Optional;

import priority.common.Constants;
import priority.connector.ConstraintConnector;
import priority.states.StateValue;

public class FIFO extends Primitive implements Constants {

	private Optional<Boolean> full;
	private String port1;
	private String port2;
	private StateValue currentStatesValues = new StateValue();
	private String stateless;

	public FIFO(final String port1, final String port2, final Optional<Boolean> initState) {
		full = initState;
		this.port1 = port1;
		this.port2 = port2;
		this.stateless = getStatelessConstraint();
	}

	private String getStatelessConstraint() {
		return String.format("(%s %s (%s %s %s %s)) %s (%s %s (%s %s %s %s)) %s ((%s (%s %s %s)) %s (%s %s %s)) %s (%s (%s %s %s))", 
				flow(port1), RIGHTLEFTARROW, nextMemory(port1, port2), AND, NOT, memory(port1, port2),
				AND,
				flow(port2), RIGHTLEFTARROW, memory(port1, port2), AND, NOT, nextMemory(port1, port2),
				AND,
				NOT, flow(port1), OR, flow(port2), IMPLIES, memory(port1, port2), RIGHTLEFTARROW, nextMemory(port1, port2),
				AND,
				NOT, flow(port1), AND, flow(port2));
	}

	/**
	 * Sets the full state of the FIFO
	 * 
	 * @param full
	 */
	public void setState(Boolean full) {
		this.full = Optional.of(full); 
	}
	/**
	 * Generates constraintConnector for the FIFO
	 * 
	 * @return
	 */
	public ConstraintConnector generateConstraint() {
		String fifo = stateless;
		if (full.isPresent()) {
			String memory = memory(port1, port2);
			if (!full.get()) {
				memory = String.format("(%s %s)", NOT, memory);
			}
			fifo = String.format("%s %s %s", stateless, AND, memory);
		}
		return new ConstraintConnector(fifo, port1, port2, memory(port1, port2), nextMemory(port1, port2));
	}
	private ConstraintConnector generateConstraint2() {
		String m = memory(port1.toLowerCase(), port2.toLowerCase());
		if (currentStatesValues.containsKey(m))
			full = currentStatesValues.getValue(m);

		String fifo = null;
		if (full.isPresent()) {
			if (full.get()) {
				fifo = String.format(
						"( " + NOT + "%s )" + AND + " %s " + AND + "(%s" + RIGHTLEFTARROW + "( " + NOT + "%s )) "// AND
																													// +
						// "(%s" + IMPLIES + "(%s" + ")) " + AND +
						// "(%s" + IMPLIES + "(%s" + AND + "("+ NOT +"%s))) " +
						// AND +
						// (full ? "%s" : "%s")
						, flow(port1), memory(port1, port2), flow(port2), nextMemory(port1, port2)// ,
				// flow(p1), nextMem(p1, p2),
				// flow(p2), mem(p1, p2, full), nextMem(p1, p2),
				// mem(p1, p2, full), nextMem(p1, p2)
				);
				/*
				 * fifo = String.format( "(" + NOT + "(%s" + OR + "%s)" +
				 * IMPLIES + " (%s" + RIGHTLEFTARROW + "%s)) " + AND + "(%s" +
				 * IMPLIES + "(%s" + AND + "("+ NOT +"%s))) " + AND + "(%s" +
				 * IMPLIES + "(%s" + AND + "("+ NOT +"%s))) " + AND + (full ?
				 * "%s" : "%s") , flow(p1), flow(p2), mem(p1, p2, full),
				 * nextMem(p1, p2), flow(p1), nextMem(p1, p2), mem(p1, p2,
				 * full), flow(p2), mem(p1, p2, full), nextMem(p1, p2), mem(p1,
				 * p2, full), nextMem(p1, p2) );
				 */
			} else {
				fifo = String.format("( %s %s ) %s ( %s %s ) %s (%s %s %s) "// +
																			// AND
																			// +
				// "(%s" + IMPLIES + "(%s" + ")) " + AND +
				// "(%s" + IMPLIES + "(%s" + AND + "("+ NOT +"%s))) " + AND +
				// (full ? "%s" : "%s")
						, NOT, flow(port2), AND, NOT, memory(port1, port2), AND, flow(port1), RIGHTLEFTARROW,
						nextMemory(port1, port2)// ,
				// flow(p1), nextMem(p1, p2),
				// flow(p2), mem(p1, p2, full), nextMem(p1, p2),
				// mem(p1, p2, full), nextMem(p1, p2)
				);
			}
		} else {
		}
		return new ConstraintConnector(fifo, port1, port2, memory(port1, port2), nextMemory(port1, port2));
	}

	/**
	 * Getter
	 * 
	 * @return
	 */
	public String getMemory() {
		return memory(port1, port2);
	}

	/**
	 * Getter
	 * 
	 * @return
	 */
	public Optional<Boolean> isFull() {
		return full;
	}
}