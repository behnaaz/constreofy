package org.behnaz.rcsp;

import org.behnaz.rcsp.model.RouteNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class ConnectorFactory extends Primitive {
	public ConstraintConnector merger(final String p1, final String p2, final String p3) {
		final String merger = String.format(
				"(%s" + AbstractConnector.RIGHTLEFTARROW + "(%s" + AbstractConnector.OR + "%s))" + AbstractConnector.AND + "(" + AbstractConnector.NOT + "(%s" + AbstractConnector.AND + "%s))" + AbstractConnector.AND + "((" + AbstractConnector.NOT
						+ "%s )" + AbstractConnector.IMPLIES + "((" + AbstractConnector.NOT + "%sk )" + AbstractConnector.AND + "%sk" + AbstractConnector.AND + "%sk)" + AbstractConnector.OR + "(%sc" + AbstractConnector.AND + ""
						+ AbstractConnector.NOT + " %sk " + AbstractConnector.AND + " %sk)" + AbstractConnector.OR + "(%sc" + "" + AbstractConnector.AND + "" + " %sk" + AbstractConnector.AND + AbstractConnector.NOT + "%sk))"
						+ AbstractConnector.AND + "(" + AbstractConnector.NOT + " (%s" + AbstractConnector.AND + "%s))",
				flow(p3), flow(p1), flow(p2), flow(p1), flow(p2), flow(p3), p3, p1, p2, p3, p1, p2, p3, p1, p2, p1, p2);
		return new ConstraintConnector(merger, p1, p2, p3);
	}

	public ConstraintConnector sync(final String p1, final String p2) {
		String sync = String.format("(%s %s %s)"/* + AND + NOT + "(%sc" + AND + "%sk)"*/, 
				flow(p1), AbstractConnector.RIGHTLEFTARROW, flow(p2));//, p1, p2);
		return new ConstraintConnector(sync, p1, p2);
	}

	/**
	 * Returns the constraints of a new instance of FIFO initialized or uninitialized depending on the third parameter
	 * @param source
	 * @param sink
	 * @return
	 */
	public ConstraintConnector getFIFOConstraint(final String source, final String sink) {
		return new FIFO(source, sink).generateConstraint();
	}

	public ConstraintConnector router(final String c, final String k1, final String k2) {
		return new ConstraintConnector(new RouteNode("", c, new HashSet<>(Arrays.asList(k1, k2))).getConstraint(), c, k1, k2);
	}

	public ConstraintConnector lossySync(final String p1, final String p2) {
		final String lossy = String.format("(%s %s %s)", flow(p2), AbstractConnector.IMPLIES, flow(p1));
		return new ConstraintConnector(lossy, p1, p2);
	}

	public ConstraintConnector syncDrain(final String p1, final String p2) {
		String syncDrain = String.format("(%s %s %s)", flow(p1), AbstractConnector.RIGHTLEFTARROW, flow(p2));
		return new ConstraintConnector(syncDrain, p1, p2);
	}

	public ConstraintConnector prioritySync(final String p1, final String p2) {
		final String prioritySync = String.format("(%s" + AbstractConnector.RIGHTLEFTARROW + "%s)" + AbstractConnector.AND + AbstractConnector.NOT + "(%sc" + AbstractConnector.AND + "%sk)" + AbstractConnector.AND
				+ "%sbullet" + AbstractConnector.AND + "%sbullet", flow(p1), flow(p2), p1, p2, p1, p2);
		return new ConstraintConnector(prioritySync, p1, p2);
	}

	public ConstraintConnector replicator(final String c, final String... ks) {
		String replicator = "";
		for (String k : ks) {
			if (!replicator.isEmpty()) {
				replicator += AbstractConnector.AND;
			}
			replicator += String.format("(%s %s %s) %s (%s %s %s)",
					// + NOT + "(%sc" + AND + "%sk)" + AND + "%sbullet" + AND +
					// "%sbullet",
					flow(c), AbstractConnector.RIGHTLEFTARROW, flow(k), AbstractConnector.AND, flow(c), AbstractConnector.RIGHTLEFTARROW, flow(k));
		}
		final List<String> l = new ArrayList<>();
		l.add(c);
		l.addAll(Arrays.asList(ks));
		return new ConstraintConnector(replicator, l);
	}

	public ConstraintConnector replicator(final String c, final String k) {
		String replicator = String.format("(%s %s %s)",
				// + NOT + "(%sc" + AND + "%sk)" + AND + "%sbullet" + AND +
				// "%sbullet",
				flow(c), AbstractConnector.RIGHTLEFTARROW, flow(k));
		return new ConstraintConnector(replicator, c, k);
	}

	public ConstraintConnector join(final String c1, final String c2, final String k) {
		/*
		 * String replicator = String.format( "(%s" + RIGHTLEFTARROW + "%s)"+
		 * AND + "(%s" + RIGHTLEFTARROW + "%s)", //+ NOT + "(%sc" + AND + "%sk)"
		 * + AND + "%sbullet" + AND + "%sbullet", flow(c), flow(k1), flow(c),
		 * flow(k2) ); return new ConstraintConnector(replicator, c, k1, k2);
		 */
		return replicator(k, c1, c2);
	}

	public ConstraintConnector writer(final String source, final int capacity) {
		return new ConstraintConnector(capacity > 0 ?
				String.format("(%s %s %s %s)", flow(source), AbstractConnector.OR, AbstractConnector.NOT, flow(source)) :
		 		String.format("( %s %s)", AbstractConnector.NOT, flow(source)),
		 	source);
	}

	public ConstraintConnector fifo(final String source, final String sink) {
		return new FIFO(source, sink).generateConstraint();
	}
}