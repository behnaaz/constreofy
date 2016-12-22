package priority;

import static priority.Constraint.AND;
import static priority.Constraint.NOT;
import static priority.Constraint.TILDE;
import static priority.Constraint.IMPLIES;
import static priority.Constraint.OR;
import static priority.Constraint.RIGHTLEFTARROW;
import static priority.Constraint.CURRENT_MEMORY;
import static priority.Constraint.NEXT_MEMORY;

public class ConnectorFactory {
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
		String sync = String.format("(%s" + RIGHTLEFTARROW + "%s)" + AND + NOT + "(%sc" + AND + "%sk)", flow(p1),
				flow(p2), p1, p2);
		return new ConstraintConnector(sync, p1, p2);
	}
	/*
	 * public ConstraintConnector fullFifo(String p1, String p2) { String
	 * fullFifo = String.format( "(" + NOT + "%s)" + AND + "%s" + AND + "(%s" +
	 * IMPLIES + "("+ NOT +"%s))", flow(p1), mem(p1, p2), flow(p2), nextMem(p1,
	 * p2) ); return new ConstraintConnector(fullFifo, p1, p2, mem(p1, p2),
	 * nextMem(p1, p2)); }
	 */

	public ConstraintConnector fifo(String p1, String p2) {
		return fifo(p1, p2, false);
	}

	public ConstraintConnector fifo(String p1, String p2, Boolean full) {
		String fifo;
		if (full)
			fifo = String
					.format("( " + NOT + "%s )" + AND + " %s " + AND + "(%s" + RIGHTLEFTARROW + "( " + NOT + "%s )) "// +
																														// AND
																														// +
			// "(%s" + IMPLIES + "(%s" + ")) " + AND +
			// "(%s" + IMPLIES + "(%s" + AND + "("+ NOT +"%s))) " + AND +
			// (full ? "%s" : "%s")
							, flow(p1), mem(p1, p2), flow(p2), nextMem(p1, p2)// ,
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
							, flow(p2), mem(p1, p2), flow(p1), nextMem(p1, p2)// ,
			// flow(p1), nextMem(p1, p2),
			// flow(p2), mem(p1, p2, full), nextMem(p1, p2),
			// mem(p1, p2, full), nextMem(p1, p2)
			);
		return new ConstraintConnector(fifo, p1, p2, mem(p1, p2), nextMem(p1, p2));
	}

	public ConstraintConnector router(String c, String k1, String k2) {
		String router = String.format(
				"(%s" + RIGHTLEFTARROW + "%s" + OR + "%s)" + AND + "(" + NOT + "(%s" + AND + "%s))", 
				flow(c), flow(k1),	flow(k2), 
				flow(k1), flow(k2));
		return new ConstraintConnector(router, c, k1, k2);
	}

	public ConstraintConnector lossyDrain(String p1, String p2) {
		String lossyDrain = String.format("(%s" + IMPLIES + "%s)", flow(p2), flow(p1));
		return new ConstraintConnector(lossyDrain, p1, p2);
	}

	public ConstraintConnector syncDrain(String p1, String p2) {
		String syncDrain = String.format("(" + NOT + "(%s" + AND + "%s))", flow(p1), flow(p2));
		return new ConstraintConnector(syncDrain, p1, p2);
	}

	public ConstraintConnector prioritySync(String p1, String p2) {
		String prioritySync = String.format("(%s" + RIGHTLEFTARROW + "%s)" + AND + NOT + "(%sc" + AND + "%sk)" + AND
				+ "%sbullet" + AND + "%sbullet", flow(p1), flow(p2), p1, p2, p1, p2);
		return new ConstraintConnector(prioritySync, p1, p2);
	}

	public ConstraintConnector replicator(String c, String k1, String k2) {
		String replicator = String.format("(%s" + RIGHTLEFTARROW + "%s)" + AND + "(%s" + RIGHTLEFTARROW + "%s)",
				// + NOT + "(%sc" + AND + "%sk)" + AND + "%sbullet" + AND +
				// "%sbullet",
				flow(c), flow(k1), flow(c), flow(k2));
		return new ConstraintConnector(replicator, c, k1, k2);
	}

	public ConstraintConnector replicator(String c, String k) {
		String replicator = String.format("(%s" + RIGHTLEFTARROW + "%s)",
				// + NOT + "(%sc" + AND + "%sk)" + AND + "%sbullet" + AND +
				// "%sbullet",
				flow(c), flow(k));
		return new ConstraintConnector(replicator, c, k);
	}

	String flow(String node) {
		return new StringBuilder().append(node).append(TILDE).toString();
	}

	String mem(String p1, String p2) {
		return new StringBuilder().append(p1.trim()).append(p2.trim()).append(CURRENT_MEMORY).toString();
	}

	String nextMem(String p1, String p2) {
		return new StringBuilder().append(p1.trim()).append(p2.trim()).append(NEXT_MEMORY).toString();
	}

	public ConstraintConnector fullFifo(String c, String k) {
		return fifo(c, k, true);
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

	public ConstraintConnector writer(String k, int n) {
		String writer;
		if (n > 0)
			writer = String.format("( %s " + OR + "%s )", flow(k), flow(k));
		else
			writer = String.format("( " + NOT + "%s )", flow(k));
		return new ConstraintConnector(writer, k);
	}
}
