package priority.solving;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class IOComponent {
	private String node;
	@Getter
	private int requests;

	public IOComponent(String name, int requests) {
		this.node = name;
		this.requests = requests;
	}

	public String getNodeName() {
		return node;
	}

	public int consume() {
		if (requests > 0)
			return requests-1;
		return requests;
	}
}
