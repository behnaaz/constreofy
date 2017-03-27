package priority.connector;

import priority.common.Constants;
import priority.primitives.FIFO;
import priority.primitives.Primitive;

public class ConnectorFactory extends Primitive implements Constants {
	public ConstraintConnector merger(String p1, String p2, String p3) {
		String merger = String.format(
				"(%s" + RIGHTLEFTARROW + "(%s" + OR + "%s))" + AND + "(" + NOT + "(%s" + AND + "%s))" + AND + "((" + NOT
						+ "%s )" + IMPLIES + "((" + NOT + "%sk )" + AND + "%sk" + AND + "%sk)" + OR + "(%sc" + AND + ""
						+ NOT + " %sk " + AND + " %sk)" + OR + "(%sc" + "" + AND + "" + " %sk" + AND + NOT + "%sk))"
						+ AND + "(" + NOT + " (%s" + AND + "%s))",
				flow(p3), flow(p1), flow(p2), flow(p1), flow(p2), flow(p3), p3, p1, p2, p3, p1, p2, p3, p1, p2, p1, p2);
		return new ConstraintConnector(merger, p1, p2, p3);
	}

	public ConstraintConnector sync(String p1, String p2) {
		String sync = String.format("(%s %s %s)"/* + AND + NOT + "(%sc" + AND + "%sk)"*/, 
				flow(p1), RIGHTLEFTARROW, flow(p2));//, p1, p2);
		return new ConstraintConnector(sync, p1, p2);
	}

	/**
	 * Returns the constraints of a new instance of FIFO initialized or uninitialized depending on the third parameter
	 * @param source
	 * @param sink
	 * @param full
	 * @return
	 */
	public ConstraintConnector getFIFOConstraint(final String source, final String sink) {
		return new FIFO(source, sink).generateConstraint();
	}

	public ConstraintConnector router(String c, String k1, String k2) {
		String router = String.format(
				"(%s %s ( %s %s %s )) %s (%s (%s %s %s))", 
				flow(c), RIGHTLEFTARROW, flow(k1),	OR, flow(k2), AND, NOT, flow(k1), AND, flow(k2));
		return new ConstraintConnector(router, c, k1, k2);
	}

	public ConstraintConnector lossyDrain(String p1, String p2) {
		String lossyDrain = String.format("(%s %s %s)", flow(p2), IMPLIES, flow(p1));
		return new ConstraintConnector(lossyDrain, p1, p2);
	}

	public ConstraintConnector syncDrain(String p1, String p2) {
		String syncDrain = String.format("(%s %s %s)", flow(p1), RIGHTLEFTARROW, flow(p2));
		return new ConstraintConnector(syncDrain, p1, p2);
	}

	public ConstraintConnector prioritySync(String p1, String p2) {
		String prioritySync = String.format("(%s" + RIGHTLEFTARROW + "%s)" + AND + NOT + "(%sc" + AND + "%sk)" + AND
				+ "%sbullet" + AND + "%sbullet", flow(p1), flow(p2), p1, p2, p1, p2);
		return new ConstraintConnector(prioritySync, p1, p2);
	}

	public ConstraintConnector replicator(String c, String k1, String k2) {
		String replicator = String.format("(%s %s %s) %s (%s %s %s)",
				// + NOT + "(%sc" + AND + "%sk)" + AND + "%sbullet" + AND +
				// "%sbullet",
				flow(c), RIGHTLEFTARROW, flow(k1), AND, flow(c), RIGHTLEFTARROW, flow(k2));
		return new ConstraintConnector(replicator, c, k1, k2);
	}

	public ConstraintConnector replicator(String c, String k) {
		String replicator = String.format("(%s %s %s)",
				// + NOT + "(%sc" + AND + "%sk)" + AND + "%sbullet" + AND +
				// "%sbullet",
				flow(c), RIGHTLEFTARROW, flow(k));
		return new ConstraintConnector(replicator, c, k);
	}

	public ConstraintConnector join(String c1, String c2, String k) {
		/*
		 * String replicator = String.format( "(%s" + RIGHTLEFTARROW + "%s)"+
		 * AND + "(%s" + RIGHTLEFTARROW + "%s)", //+ NOT + "(%sc" + AND + "%sk)"
		 * + AND + "%sbullet" + AND + "%sbullet", flow(c), flow(k1), flow(c),
		 * flow(k2) ); return new ConstraintConnector(replicator, c, k1, k2);
		 */
		return replicator(k, c1, c2);
	}

	public ConstraintConnector writer(String source, int capacity) {
		String writer;
		if (capacity > 0)
			writer = String.format("(%s %s %s %s)", flow(source), OR, NOT, flow(source));
		else
			writer = String.format("( %s %s)", NOT, flow(source));
		return new ConstraintConnector(writer, source);
	}
}