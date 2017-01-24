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
		return new FIFO(p1, p2, full).constraint();
	}

	public ConstraintConnector router(String c, String k1, String k2) {
		String router = String.format(
				"(%s" + RIGHTLEFTARROW + "( %s" + OR + "%s ))" + AND + "(" + NOT + "(%s" + AND + "%s))", 
				flow(c), flow(k1),	flow(k2), 
				flow(k1), flow(k2));
		return new ConstraintConnector(router, c, k1, k2);
	}

	public ConstraintConnector lossyDrain(String p1, String p2) {
		String lossyDrain = String.format("(%s" + IMPLIES + "%s)", flow(p2), flow(p1));
		return new ConstraintConnector(lossyDrain, p1, p2);
	}

	public ConstraintConnector syncDrain(String p1, String p2) {
		String syncDrain = String.format("(%s" + RIGHTLEFTARROW + "%s)", flow(p1), flow(p2));
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
			writer = String.format("( %s " + OR + NOT + " %s )", flow(k), flow(k));
		else
			writer = String.format("( " + NOT + "%s )", flow(k));
		return new ConstraintConnector(writer, k);
	}
}