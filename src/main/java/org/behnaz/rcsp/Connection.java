package org.behnaz.rcsp;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Connection {
	private Set<HashSet<String>> equals;

	public Connection() {
		equals = new HashSet<>();
	}
	
	
	public static void main(String[] args) {
		Connection ss = new Connection();
		ss.addEqual("d", "f");
		ss.addEqual("e", "q");
		ss.addEqual("d", "b");
		ss.addEqual("w", "f");
	}

	public void addEqual(String... vars) {
		Set<String> set = null;
		HashSet<String> temp = new HashSet<>();
		for (String var : vars) {
			temp.add(var);
			if (set == null || set.isEmpty()) {
				set = findEquals(var);
			}
		}
		if (set != null) {
			if (set.isEmpty()) {
				set = new HashSet<>();
				equals.add((HashSet<String>) set);	
			}
			set.addAll(temp);
		}
	}

	public Set<String> findEquals(String var) {
		for (Set<String> s : equals) {
			if (s.contains(var))
				return s;
		}
		return Collections.emptySet();
	}


	public Set<HashSet<String>> getEquals() {
		return equals;
	}
}