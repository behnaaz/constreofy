package priority.primitives;

import priority.connector.ConstraintConnector;

import static priority.connector.AbstractConnector.*;

public class FIFO extends Primitive {

	private String port1;
	private String port2;
	private String stateless;

	public FIFO(final String port1, final String port2) {
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
	 * @return 
	 */
	public String generateStateConstraint(Boolean full) {
		String result =  memory(port1, port2);
		if (!full)
			result = String.format("%s %s", NOT, result);
		return result;
	}
	/**
	 * Generates constraintConnector for the FIFO
	 * 
	 * @return
	 */
	public ConstraintConnector generateConstraint() {
		String fifo = stateless;
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
}