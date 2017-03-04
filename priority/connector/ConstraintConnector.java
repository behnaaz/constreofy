package priority.connector;

import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.base.Strings;

import javassist.bytecode.stackmap.TypeData.ClassName;
import priority.common.Constants;
import priority.primitives.Primitive;
import priority.states.StateValue;
/**
 * The building block of a network 
 * (representing connectors using constraint semantics)
 * @author behnaz.changizi
 *
 */
public class ConstraintConnector extends AbstractConnector implements Constants {
	private static final Logger LOGGER = Logger.getLogger( ClassName.class.getName() );
	private String constraint;
	private final transient ConnectorFactory factory = new ConnectorFactory();
	private String[] states;
	private String[] nextStates;
	private Connection connection;
	// Reduce the number of variables by omitting equal ones
	private boolean useEqualSet = true;

	/**
	 * The constraint representing the connector and lists of its port ends
	 * @param constraint
	 * @param names
	 */
	public ConstraintConnector(final String constraint, final String... names) {
		super(names);
		connection = new Connection();
		setConstraint(constraint);
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
	 * Constraint
	 * @return
	 */
	public String getConstraint() {
		return constraint;
	}

	/**
	 * Sets constraint
	 * @return
	 */
	public void setConstraint(final String constraint) {
		this.constraint = constraint;
	}

	/**
	 * Variables used in the constraint
	 * @return
	 */
	public Set<String> getVariables() {
		return extractVariables(constraint);
	}

	private Set<String> extractVariables(final String newConstraint) {
		final Set<String> result = new HashSet<>();

		if (!Strings.isNullOrEmpty(newConstraint)) {
			final String copy = newConstraint.replaceAll(AND.trim() + "|" + IMPLIES.trim() + "|" + NOT.trim()
					+ "|\\(|\\)|,|" + RIGHTLEFTARROW.trim() + "|" + OR.trim() + "|" + TRUE.trim() + "|" + FALSE.trim(),
					"");
			// constraint
			final StringBuilder builder = new StringBuilder();
			for (final String temp : copy.split(SPACE)) { // NOPMD by behnaz.changizi on 2/23/17 11:27 AM
				builder.setLength(0);//Empty
				if (!temp.trim().isEmpty()) {
					result.add(temp.toUpperCase(Locale.US));
					builder.append(WORD_BOUNDARY);
					builder.append(temp);
					builder.append(WORD_BOUNDARY);
					constraint = constraint.replaceAll(builder.toString(), temp.toUpperCase(Locale.US));
				}
			}
		}
		return result;
	}

	private String printVariables(final String formulae) throws IOException {
		final StringBuilder builder = new StringBuilder();
		final Set<String> vars = this.extractVariables(formulae).stream().filter(item -> !item.isEmpty())//TODO orElse??
				.map(String::toUpperCase).collect(Collectors.toSet());
		builder.append("rlpcvar ");
		final String variable = vars.toString(); // NOPMD by behnaz.changizi on 2/23/17 11:27 AM
		builder.append(variable.substring(1, variable.length() - 1)).append(';');
		return builder.toString();
	}
	
	/**
	* Wrap the constraints with required by REDUCE 
	* @param constraint
	* @return
	*///TPDP ?? retire one 
	public String output(final StateValue stateValue) {
		final StringBuilder builder = new StringBuilder();

		try {
			builder.append(PREAMBLE);
			builder.append(printVariables(constraint));
			//???
			builder.append(FORMULA_NAME + " := " + getFinalConstraints(constraint, stateValue) + ";;");
			builder.append(dnf(FORMULA_NAME));
			builder.append(SHUT);
			builder.append("; end;");
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.toString());
		}
		return builder.toString();
	}

	private String getFinalConstraints(final String mainConstraint, final StateValue stateValue) {
		final StringBuilder builder = new StringBuilder();
		Set<String> fifos = getAllFIFOs(mainConstraint);
		builder.append(mainConstraint);
		
		for (String capitalLetterFIFO : fifos) {
			String fifo = capitalLetterFIFO.toLowerCase(Locale.ENGLISH).replaceAll("xring", "ring"); 
			if (stateValue.getValue(fifo) == null || !stateValue.getValue(fifo).isPresent() || stateValue.getValue(fifo).get() == false) {
				builder.append(AND);
				builder.append(" (");
				builder.append(NOT);
				builder.append(fifo.toUpperCase(Locale.ENGLISH));
				builder.append(") ");
			} else {
				if (stateValue.getValue(fifo).get()) {
					builder.append(AND);
					builder.append(fifo.toUpperCase(Locale.ENGLISH));
				} else {
					assert(false);
				}
			}
		}
		if (useEqualSet)
			return replaceEquals(builder.toString(), connection.getEquals());

		return builder.toString();
	}

	private Set<String> getAllFIFOs(String mainConstraint) {
		Set<String> result = new TreeSet<>();
		Pattern pattern = Pattern.compile("\\w+XRING");
		Matcher matcher = pattern.matcher(mainConstraint);
		
		while(matcher.find()){
			int start = matcher.start();
			int end = matcher.end();
			result.add(mainConstraint.substring(start, end));
		}
		return result;
	}

	/**
	 * Wrap the constraints with required by REDUCE 
	 * @param constraint
	 * @return
	 */
	public String output(final String constraint) {
		final StringBuilder builder = new StringBuilder();

		try {
			builder.append(PREAMBLE);
			builder.append(printVariables(constraint));
			builder.append(FORMULA_NAME + " := " + constraint + ";;");
			builder.append(dnf(FORMULA_NAME));
			builder.append(SHUT);
			builder.append("; end;");
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.toString());
		}
		return builder.toString();
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
		if (port1 != null && port1.length() > 0) {
			names.add(port1);
		}
		if (newConnector != null) {
			constraint = String.format("%s %s %s", constraint, AND, newConnector.getConstraint());
		}
		if (useEqualSet) {
			connection.addEqual(port1, port2);
		} else {
			constraint = String.format("%s %s ( %s %s  %s)",
					constraint, AND, factory.flow(port1), RIGHTLEFTARROW, factory.flow(port2));
		}
	}

	// ???TODO
	/**
	 * Adds p1 IMPLIES p2
	 * 
	 * @param nexts
	 */
	public ConstraintConnector connect(final String port1, final String port2) {
		return new ConstraintConnector(String.format(" (%s %s %s) ", port1, IMPLIES, port2));
	}

	/**
	 * Sets states
	 * 
	 * @param nexts
	 */
	public void setStates(final String... mems) {
		states = mems;
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

	public String replaceEquals(String rawConstraint, Set<HashSet<String>> equalsSuperSet) {
		Primitive prim = new Primitive();
		for (HashSet<String> equals : equalsSuperSet) {
			Optional<String> representor = equals.stream().findFirst();
			if (representor.isPresent()) {
				equals.remove(representor);
				for (String var : equals) {
					rawConstraint = rawConstraint.replaceAll(prim.flow(var), prim.flow(representor.get()));
				}
			}
		}
		System.out.println("reduced" + rawConstraint);
		return rawConstraint;
	}
}