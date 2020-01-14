package org.behnaz.rcsp;

import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import static org.behnaz.rcsp.Solver.FORMULA_NAME;
import static org.behnaz.rcsp.Solver.SHUT;
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
	private static final String KEY_WORDS_REGEX = String.format("%s%s%s%s%s%s%s%s%s%s%s%s%s", 
			AND.trim(), BAR, IMPLIES.trim(), BAR, NOT.trim(), "|\\(|\\)|,|", RIGHTLEFTARROW.trim(), BAR,
			OR.trim(), BAR, TRUE.trim(), BAR, FALSE.trim());
	private static final Logger LOGGER = Logger.getLogger( ConstraintConnector.class.getName() );
	@Getter
	private String constraint;
	private final ConnectorFactory factory = new ConnectorFactory();
	private String[] states;
	private String[] nextStates;
	private final Connection connection;
	static final String WORD_BOUNDARY = "\\b";
	static final String PREAMBLE = "set_bndstk_size 100000;load_package \"redlog\";rlset ibalp;";


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
		this.constraint = (constraint);
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

	/**
	 * Variables used in the constraint
	 * @return
	 */
	public Set<String> getVariables() {
		return extractVariablesAndUpdateConstraint(constraint, true);//TODO bad design side effect
	}

	private Set<String> extractVariablesAndUpdateConstraint(final String newConstraint, final boolean updateConstraint) {
		final Set<String> result = new HashSet<>();

		if (newConstraint != null && newConstraint.trim().length() > 0) {
			// Replace all keywords with empty string
			final String onlyVariables = newConstraint.replaceAll(KEY_WORDS_REGEX, "");
			// Constraint
			final StringBuilder builder = new StringBuilder();
			for (final String term : onlyVariables.split(SPACE)) {
				if (!term.trim().isEmpty()) {
					result.add(term.toUpperCase(Locale.US));

					if (updateConstraint) {
						// Update constraint
						builder.setLength(0); // Empty builder
						builder.append(WORD_BOUNDARY).append(term).append(WORD_BOUNDARY);
						constraint = constraint.replaceAll(builder.toString(), term.toUpperCase(Locale.US));
					}
				}
			}
		}
		return result;
	}

	private String prepareVariables(final String formulae) throws IOException {
		final StringBuilder builder = new StringBuilder();
		final Set<String> vars = this.extractVariablesAndUpdateConstraint(formulae, true).stream().filter(item -> !item.isEmpty())//TODO orElse??
				.map(String::toUpperCase).collect(Collectors.toSet());
		builder.append("rlpcvar ");
		final String variable = vars.toString();
		builder.append(variable.substring(1, variable.length() - 1)).append(';');
		return builder.toString();
	}
	
	/**
	* Wrap the constraints with required by REDUCE 
	* @param stateValue
	* @return
	*/ 
	public String buildConstraint(final StateValue stateValue) {
		final StringBuilder builder = new StringBuilder();

		try {
			builder.append(PREAMBLE);
			builder.append(prepareVariables(constraint));
			builder.append(FORMULA_NAME + " := " + applyFIFOStates(constraint, stateValue) + ";;");
			builder.append(dnf(FORMULA_NAME));
			builder.append(SHUT);
			builder.append("; end;");
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.toString());
		}
		String temp = builder.toString();
		Starter.log("Built constraints " + temp);
		return temp;
	}

	private String applyFIFOStates(final String mainConstraint, final StateValue stateValue) {
		final StringBuilder builder = new StringBuilder();
		builder.append(mainConstraint);
		
		for (final String capitalFIFO : getAllFIFOs(mainConstraint)) {
			final String fifo = capitalFIFO.toLowerCase(Locale.ENGLISH).replaceAll(NEXT_MEMORY, CURRENT_MEMORY); 
			if (stateValue != null && stateValue.getValue(fifo).isPresent() && stateValue.getValue(fifo).get()) {//TODO what to do with optional???
				if (stateValue.getValue(fifo).get()) {
					builder.append(AND);
					builder.append(fifo.toUpperCase(Locale.ENGLISH));
				} else {
					assert false;
				}
			} else {
				builder.append(AND);
				builder.append(" (");
				builder.append(NOT);
				builder.append(fifo.toUpperCase(Locale.ENGLISH));
				builder.append(") ");
			}
		}
		return builder.toString();
	}

	private Set<String> getAllFIFOs(final String mainConstraint) {
		final Set<String> result = new TreeSet<>();
		final Pattern pattern = Pattern.compile("\\w+XRING", Pattern.CASE_INSENSITIVE);
		final Matcher matcher = pattern.matcher(mainConstraint);
		
		while(matcher.find())
			result.add(mainConstraint.substring(matcher.start(), matcher.end()));

		return result;
	}

	private String dnf(final String formulae) throws IOException {
		return new StringBuilder().append("rldnf ").append(formulae).append(";").toString();
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

	public ConstraintConnector clean() {
		for (Set<String> eq : connection.getEquals()) {
			String temp = eq.iterator().next();
			for (String s : eq) {
				constraint = constraint.replace(" "+s+" ", " "+temp+" ");
			}
		}
		constraint = replaceEquals(this);
		return this;
	}

	public Set<HashSet<String>> getEquals() {
		return connection.getEquals();
	}
}
