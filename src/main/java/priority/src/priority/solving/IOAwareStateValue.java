package priority.src.priority.solving;

import java.util.Arrays;

import priority.common.Constants;
import priority.states.StateValue;

public class IOAwareStateValue implements Comparable<Object>, Constants {
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
			return new StateValue();
		return stateValue;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(stateValue.toString());
		for (IOComponent io : ios) 
			sb.append(io.toString());
		return sb.toString();
	}

	@Override
	public int compareTo(Object o) {//???
		if (o == null)//???TODO
			return -1;

		if (o instanceof IOAwareStateValue) {
			IOAwareStateValue temp = (IOAwareStateValue)o;
			int stateComparison = temp.getStateValue().compareTo(getStateValue());
			if (stateComparison == 0) {
				return Integer.compare(Arrays.hashCode(temp.getIOs()), Arrays.hashCode(getIOs()));
			}
			return stateComparison;
		}

		return 1;
	}

	public void setIOs(IOComponent... ios) {
		this.ios = ios;
	}
}