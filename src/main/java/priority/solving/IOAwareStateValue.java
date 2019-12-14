package priority.solving;

import java.util.Arrays;

import lombok.ToString;
import priority.states.StateValue;

@ToString
public class IOAwareStateValue implements Comparable<Object> {
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