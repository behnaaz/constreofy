package priority.solving;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import priority.primitives.Primitive;

public class IOAwareSolution implements Comparable<Object> {
	private IOComponent[] postIOs;
	private Solution solution;
	private IOComponent[] preIOs;
	public IOAwareSolution(Solution solution, IOComponent...preIOs) {
		this.solution = solution;
		this.preIOs = preIOs;
		this.postIOs = calculatePostIOs();
	}
	private IOComponent[] calculatePostIOs() {
		List<IOComponent> result = new ArrayList<>();
		for (IOComponent io : preIOs) {
			if (solution.getFlowVariables().contains(new Primitive().flow(io.getNodeName()))) {
				result.add(new IOComponent(io.getNodeName(), io.consume()));
			} else {
				result.add(new IOComponent(io.getNodeName(), io.getRequests()));
			}
		}
		return result.toArray(new IOComponent[result.size()]);
	}
	public Solution getSolution() {
		return solution;
	}

	@Override
	public String toString() {
		return Arrays.toString(preIOs).concat(solution.toString().concat(Arrays.toString(postIOs)));
		//TODO
	}
	
	@Override
	public int hashCode() {
		return solution.hashCode() * 13 + Arrays.hashCode(postIOs) * 31 + Arrays.hashCode(preIOs) * 29;
	}
	
	@Override
	public boolean equals(Object o) {
		return compareTo(o) == 0;
	}

	@Override
	public int compareTo(Object o) {
		if (o instanceof IOAwareSolution) {
			IOAwareSolution temp = (IOAwareSolution)o;
			int solutionCompareTo = temp.getSolution().compareTo(solution);
			if (solutionCompareTo == 0) {
				boolean iosCompareTo = Arrays.equals(temp.getPostIOs(), postIOs);
				boolean iosCompareTo2 = Arrays.equals(temp.getPreIOs(), preIOs);
				if (!iosCompareTo || !iosCompareTo2)
					return -1;
				return 0;
			}
			return solutionCompareTo;
		}
		return -1;
	}
	public IOComponent[] getPostIOs() {
		return postIOs;
	}
	public IOComponent[] getPreIOs() {
		return preIOs;
	}

}
