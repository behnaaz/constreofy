package org.behnaz.rcsp;

import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.behnaz.rcsp.model.util.SolverHelper;

import static org.behnaz.rcsp.Solver.USE_EQUAL_SET_ON;
import static priority.Variable.CURRENT_MEMORY;
import static priority.Variable.NEXT_MEMORY;

/**
 * The building block of a network 
 * (representing connectors using constraint semantics)
 * @author behnaz.changizi
 *
 */
public class ConstraintConnector extends AbstractConnector {
	private static final String BAR = "|";
	public static final String KEY_WORDS_REGEX = String.format("%s%s%s%s%s%s%s%s%s%s%s%s%s",
			AND.trim(), BAR, IMPLIES.trim(), BAR, NOT.trim(), "|\\(|\\)|,|", RIGHTLEFTARROW.trim(), BAR,
			OR.trim(), BAR, TRUE.trim(), BAR, FALSE.trim());

	@Getter
	private String constraint;
	private final ConnectorFactory factory = new ConnectorFactory();
	private String[] states;
	private String[] nextStates;
	private final Connection connection;
	public static final String WORD_BOUNDARY = "\\b";

	/**
	 * The constraint representing the connector and lists of its port ends
	 * @param constraint
	 * @param boundaryPortNames
	 */
	public ConstraintConnector(final String constraint, final String... boundaryPortNames) {
		super(boundaryPortNames);
		connection = new Connection();
		this.constraint = constraint;
	}

	public ConstraintConnector(final String constraint, final List<String> boundaryPortNames) {
		super(boundaryPortNames);
		connection = new Connection();
		this.constraint = constraint;
	}

	/**
	 * Initializing the state variables
	 * @return
	 *///????TODO
	public Map<String, Boolean> initStateValues() {
		final Map<String, Boolean> result = new ConcurrentHashMap<>();
		for (final String state : states) {
			final String lowerCaseState = state.toLowerCase(Locale.US);
			result.put(lowerCaseState.replace(CURRENT_MEMORY, NEXT_MEMORY), false);
		}
		return result;
	}

	void capitalizeVars() {
		constraint = SolverHelper.capitalizeVars(constraint);
	}

	/**
	 * Conjuncts the new connector logic to the existing and
	 *  adds the first passed port to the list of names
	 * @param newConnector
	 * @param port1
	 * @param port2
	 */
	public void add(final ConstraintConnector newConnector, final String port1, final String port2) {
		if (newConnector != null) {
			String newConstraint = newConnector.getConstraint();
			if (USE_EQUAL_SET_ON) {
				addEquals(port1, port2);
				newConstraint = replaceEquals(newConnector);
			}
			if (TRUE.equals(constraint))
				constraint = newConstraint;
			else
				constraint = String.format("%s %s %s", constraint, AND, newConstraint);
		}
		if (!USE_EQUAL_SET_ON) {
			if (StringUtils.isNotBlank(port1)) {
				variableNames.add(port1);
			}
			if (StringUtils.isNotBlank(port2)) {
				variableNames.add(port2);
			}
			if (StringUtils.isNotBlank(port1) && StringUtils.isNotBlank(port2))
				constraint = String.format("%s %s ( %s %s  %s)",
					constraint, AND, factory.flow(port1), RIGHTLEFTARROW, factory.flow(port2));
		}
	}

	// ???TODO
	/**
	 * Adds p1 IMPLIES p2
	 * 
	 * @param port1
	 * @param port2
	 */
	public ConstraintConnector connect(final String port1, final String port2) {
		return new ConstraintConnector(String.format(" (%s %s %s) ", port1, IMPLIES, port2));
	}

	/**
	 * Sets states
	 * 
	 * @param currents
	 */
	public void setStates(final String... currents) {
		states = currents;
	}

	/**
	 * Sets next states
	 * 
	 * @param nexts
	 */
	public void setNextStates(final String... nexts) {
		nextStates = nexts;
	}

	/**
	 * Returns the states and initializes if null
	 * 
	 * @return
	 */
	public String[] getStates() {
		if (states == null) {
			states = new String[0];
		}
		return states.clone();
	}

	/**
	 * Returns an array of next states and initializes if null
	 * 
	 * @return
	 */
	public String[] getNextStates() {
		if (nextStates == null) {
			nextStates = new String[0];// TODO???
		}
		return nextStates.clone();
	}

	private String replaceEquals(final ConstraintConnector rawConstraint) {
		long startTime = System.nanoTime();

		final Primitive prim = new Primitive();
		final String wipConstraint = doReplace(prim, rawConstraint);

		long endTime = System.nanoTime();

		long duration = endTime - startTime;
		Starter.log("replaceEquals took in milliseconds: " + duration/1000000);
		return wipConstraint;
	}

	private String doReplace(final Primitive prim, final ConstraintConnector cc) {
		String newConstraint = cc.getConstraint();
		for (String var : cc.getVariableNames()) {
			final Set<String> equals = connection.findEquals(var);
			final Optional<String> representative = equals.stream().findFirst();
			if (representative.isPresent() && !var.equals(representative.get()) && newConstraint.contains(var))
				newConstraint = newConstraint.replaceAll(prim.flow(var), prim.flow(representative.get()));
		}
		return newConstraint;
	}

	public void addEquals(String port1, String port2) {
		if (USE_EQUAL_SET_ON)
			connection.addEqual(port1, port2);
	}

	public Set<HashSet<String>> getEquals() {
		return connection.getEquals();
	}
}
