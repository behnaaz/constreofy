package priority.solving;

import com.google.common.base.Strings;

import priority.common.Constants;

public class IOComponent implements Comparable<Object>, Constants {
	private String node;
	private int requests;

	public IOComponent(String name, int requests) {
		this.node = name;
		this.requests = requests;
	}

	public String getNodeName() {
		return node;
	}

	public int getRequests() {
		return requests;
	}

	@Override
	public String toString() {
		return new StringBuilder().append(Integer.toString(requests)).append(String.valueOf(node)).toString();
	}
	
	@Override
	public int hashCode() {
		return Integer.hashCode(requests) * 13 + String.valueOf(node).hashCode() * 23 ;
	}
	
	@Override
	public boolean equals(Object o) {
		return compareTo(o) == 0;
	}
	
	@Override
	public int compareTo(Object o) {
		if (o instanceof IOComponent && !Strings.isNullOrEmpty(node)) {
				IOComponent t = (IOComponent)o;
				if (node.equals(t.getNodeName())) {
					return Integer.compare(requests, t.getRequests());
				}
		}
		return -1;
	}

	public int consume() {
		if (requests > 0)
			return requests-1;
		return requests;
	}
}
