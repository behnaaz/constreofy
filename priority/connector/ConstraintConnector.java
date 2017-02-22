package priority.connector;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Strings;

import priority.common.Constants;
import priority.solving.IOAwareStateValue;

public class ConstraintConnector extends AbstractConnector implements Constants {
	private String constraint;
	private ConnectorFactory cf = new ConnectorFactory();
	private String[] states;
	private String[] nextStates;

	public ConstraintConnector(String constraint, String... names) {
		super(names);
		this.constraint = constraint;
	}
	
	public Map<String, Boolean> initStateValues() {
		final Map<String, Boolean> result = new HashMap<>();
		for (String state : states) {
			result.put(state.toLowerCase().replace("ring", "xring"), false);
		}
		return result;
	}
	
	public String getConstraint() {
		return constraint;
	}

	public String incorporateState(IOAwareStateValue currentStatesValue) {
		String newConstrint = constraint;
		return newConstrint;
	}
	
	public Set<String> getVariables() {
		return extractVariables(constraint);
	}

	private Set<String> extractVariables(String constraint) {
		if (Strings.isNullOrEmpty(constraint))
			return Collections.emptySet();

		Set<String> result = new HashSet<>();
		String copy = constraint.replaceAll(AND.trim()+"|"+IMPLIES.trim()+"|"+NOT.trim()+"|\\(|\\)|,|"+RIGHTLEFTARROW.trim()+"|"+OR.trim()+"|"+TRUE.trim()+"|"+FALSE.trim(), "");
		//constraint
		for (String s : copy.split(" ")) {
			if (s.trim().length() > 0) {
				result.add(s.toUpperCase());
				this.constraint = this.constraint.replaceAll(new StringBuilder().append(WORD_BOUNDARY).append(s).append(WORD_BOUNDARY).toString(), s.toUpperCase());//TODO
			}
		}
		return result;
	}

	private String printVariables(String formulae) throws IOException {
		StringBuilder sb = new StringBuilder();
		Set<String> vars = this.extractVariables(formulae).stream().filter(e -> e.length()>0).map(String::toUpperCase).collect(Collectors.toSet());
		sb.append("rlpcvar ");
		sb.append(vars.toString().substring(1, vars.toString().length()-1));
		sb.append(";");
		return sb.toString();
	}
	
	public String output() {
		StringBuilder sb = new StringBuilder();

		try {
			sb.append(PREAMBLE);
			sb.append(printVariables(constraint));
			sb.append(FORMULA_NAME + " := " + constraint+";;");
			sb.append(dnf(FORMULA_NAME));
			sb.append(SHUT);
			sb.append("; end;");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	public String output(String constraint) {
		StringBuilder sb = new StringBuilder();

		try {
			sb.append(PREAMBLE);
			sb.append(printVariables(constraint));
			sb.append(FORMULA_NAME + " := " + constraint+";;");
			sb.append(dnf(FORMULA_NAME));
			sb.append(SHUT);
			sb.append("; end;");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}

	private String dnf(String formulae) throws IOException {
		return new StringBuilder().append("rldnf ").append(formulae).append(";").toString();
	}

	public void add(final ConstraintConnector newConnector, final String port1, final String port2) {
		if (port1 != null && port1.length() > 0) {
			names.add(port1);
		}

		constraint = String.format("%s " + AND + " %s" + AND + 
				"( %s" + RIGHTLEFTARROW + " %s )"// + AND +
				//"(" + NEG + "("+ "))"
				,
				constraint, newConnector.getConstraint(), cf.flow(port1), cf.flow(port2));
				
	}
	
	public void add(ConstraintConnector newConnector, String p1, String p2, boolean use) {
		if (p1 != null && p1.length() > 0) {
			names.add(p1);
		}
		final ConnectorFactory factory = new ConnectorFactory();
		constraint = String.format("%s " + AND + " %s" + AND + 
				"( %s" + RIGHTLEFTARROW + " %s)"// + AND +
				//"(" + NEG + "("+ "))"
				,
				constraint, newConnector.getConstraint(), factory.flow(p1), factory.flow(p2));
				
	}
//???TODO
	public ConstraintConnector connect(String p1, String p2) {
		return new ConstraintConnector(String.format(" (%s %s %s) ", p1, IMPLIES, p2));
	}

	public void setStates(String... mems) {
		states = mems;
	}
	
	public void setNextStates(String... nexts) {
		nextStates = nexts;
	}
	
	public String[] getStates() {
		if (states == null)
			return new String[0];
		return states;
	}
	
	public String[] getNextStates() {
		if (nextStates == null)
			return new String[0];//TODO???
		return nextStates;
	}
}