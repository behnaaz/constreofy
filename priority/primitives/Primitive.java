package priority.primitives;

import priority.common.Constants;

public class Primitive implements Constants {
	public String flow(String node) {
		return new StringBuilder().append(node).append(TILDE).toString();
	}

	public String memory(String p1, String p2) {
		p1 = p1.toLowerCase().trim();
		p2 = p2.toLowerCase().trim();
		return new StringBuilder().append(p1).append(p2).append(CURRENT_MEMORY).toString();
	}

	public String nextMemory(String p1, String p2) {
		p1 = p1.toLowerCase().trim();
		p2 = p2.toLowerCase().trim();
		return new StringBuilder().append(p1).append(p2).append(NEXT_MEMORY).toString();
	}
}
