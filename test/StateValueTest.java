package test;

import org.junit.Ignore;
import org.junit.Test;

import priority.solving.Solution;
import priority.states.StateValue;
import priority.states.StateVariableValue;

import static org.junit.Assert.assertEquals;//TODO
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertFalse;

public class StateValueTest {
	@Test
	public void testEquality() {
		StateValue t1 = new StateValue();
		StateValue t2 = new StateValue();
		assertEquals(t1, t2);
		
		t1.add(new StateVariableValue("a", true));
		assertNotSame(t1, t2);

		t2.add(new StateVariableValue("a", true));
		assertEquals(t1, t2);

		t2.add(new StateVariableValue("b", false));
		assertNotSame(t1, t2);

		t1.add(new StateVariableValue("b", false));
		assertEquals(t1, t2);
		
		// test for duplicate
		assertEquals(t1.getVariableValues().size(), 2);
		t1.add(new StateVariableValue("b", false));
		assertEquals(t1.getVariableValues().size(), 2);
		assertEquals(t1, t2);
		
		// same name different value
		t1.add(new StateVariableValue("b", true));
		assertEquals(t1.getVariableValues().size(), 3);
	}

	@Test
	public void testContains() {
		StateVariableValue v = new StateVariableValue("a", true);
		StateValue t = new StateValue();
		assertFalse(t.getVariableValues().contains(v));
		
		t.add(new StateVariableValue("a", true));
		assertTrue(t.getVariableValues().contains(v));
	}
	
	@Test
	public void testTreeSet() {
		Set<StateValue> t1 = new TreeSet<>();
		Set<StateValue> t2 = new TreeSet<>();
		assertEquals(t1, t2);
		
		StateValue s1 = new StateValue();
		s1.add(new StateVariableValue("a", true));
		t1.add(s1);
		assertNotSame(t1, t2);
		assertTrue(t1.contains(s1));
		assertFalse(t2.contains(s1));

		StateValue s2 = new StateValue();
		s2.add(new StateVariableValue("a", true));
		t2.add(s2);
		assertEquals(t1, t2);

		s1.add(new StateVariableValue("b", false));
		t1.add(s1);
		assertNotSame(t1, t2);

		s2.add(new StateVariableValue("b", false));
		t2.add(s2);
		assertEquals(t1, t2);

		assertTrue(t2.contains(s1));
		boolean deleted = t2.remove(s1);
		assertTrue(deleted);
		assertFalse(t2.contains(s1));
	}

	@Test
	public void testListContains() {
		List<StateValue> t1 = new ArrayList<>();
		
		StateValue s1 = new StateValue();
		s1.add(new StateVariableValue("jk1jk2ring", true));
		t1.add(s1);
		assertEquals(s1.toString(), "jk1jk2ringtrue");
		
		StateValue s2 = new StateValue();
		s2.add(new StateVariableValue("jk1jk2ring", true));
		assertTrue(s1.equals(s2));
		
		t1.get(0).add(new StateVariableValue("b", false));
		assertEquals(s1.toString(),"jk1jk2ringtrue,bfalse");
		assertFalse(s1.equals(s2));

		assertTrue(t1.contains(s1));
		
		s2.add(new StateVariableValue("b", false));
		
		assertTrue(t1.contains(s2));
		boolean deleted = t1.remove(s2);
		assertTrue(deleted);

		StateValue s3 = new StateValue();
		s3.add(new StateVariableValue("jk1jk2ring", true));
		s3.add(new StateVariableValue("b", false));
		assertFalse(t1.contains(s3));
	}
	
	@Test
	public void testTreeSetContains() {
		Set<StateValue> t1 = new TreeSet<>();
		
		StateValue s1 = new StateValue();
		s1.add(new StateVariableValue("jk1jk2ring", true));
		t1.add(s1);
		assertEquals(s1.toString(), "jk1jk2ringtrue");
		
		StateValue s2 = new StateValue();
		s2.add(new StateVariableValue("jk1jk2ring", true));
		assertTrue(s1.equals(s2));
		
		s1.add(new StateVariableValue("b", false));
		assertEquals(s1.toString(),"jk1jk2ringtrue,bfalse");
		assertFalse(s1.equals(s2));

		assertTrue(t1.contains(s1));
		
		s2.add(new StateVariableValue("b", false));
		
		assertTrue(t1.contains(s2));
		boolean deleted = t1.remove(s2);
		assertTrue(deleted);

		StateValue s3 = new StateValue();
		s3.add(new StateVariableValue("jk1jk2ring", true));
		s3.add(new StateVariableValue("b", false));
		assertFalse(t1.contains(s3));
	}
}