package org.behnaz.rcsp;

import lombok.ToString;

@ToString
public class IOAwareStateValue {
	private IOComponent[] ios;
	private StateValue stateValue;

	public IOAwareStateValue(StateValue stateValue, IOComponent... ios) {
		this.stateValue = stateValue;
		this.ios = ios;
	}

	public IOComponent[] getIOs() {
		if (ios == null)
			return new IOComponent[0];
		return ios;//TODO null
	}
	
	public StateValue getStateValue() {
		if (stateValue == null)
			stateValue = StateValue.builder().build();
		return stateValue;
	}
}