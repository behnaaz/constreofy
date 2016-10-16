package priority;

import java.util.ArrayList;

public class ConstraintConnector extends AbstractConnector {
	String constraint;

	public String getConstraint() {
		return constraint;
	}

	public ConstraintConnector(String constraint, ArrayList<String> names) {
		super(names);
		this.constraint = constraint;
	}
	
	public ConstraintConnector(String constraint, String... names) {
		super(names);
		this.constraint = constraint;
	}

	@Override
	public void output() {
		System.out.println(constraint);
	}

	public void add(ConstraintConnector newConnector, String p1, String p2) {
		if (p1 != null && p1.length() > 0)
			names.add(p1);
		constraint = String.format("%s and %s", constraint, newConnector.getConstraint());
	}

	public ConstraintConnector connect(String p1, String p2) {
		return new ConstraintConnector(String.format(" (%s == %s) ", p1, p2), new ArrayList<>());
	}
}