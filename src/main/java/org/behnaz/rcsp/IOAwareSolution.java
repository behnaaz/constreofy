package org.behnaz.rcsp;

import java.util.ArrayList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class IOAwareSolution {
	@Getter
	private IOComponent[] postIOs;
	@Getter
	private Solution solution;
	@Getter
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
}
