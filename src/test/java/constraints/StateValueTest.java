package constraints;

import static org.junit.Assert.assertEquals;//TODO
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.*;

import org.junit.Test;

import priority.states.StateValue;
import priority.states.StateVariableValue;

public class StateValueTest {
	Optional<Boolean> optTrue = Optional.of(Boolean.TRUE);
	Optional<Boolean> optFalse = Optional.of(Boolean.FALSE);

	@Test
	public void testEquality() {
		StateValue t1 = StateValue.builder().build();
		StateValue t2 = StateValue.builder().build();
		assertEquals(t1, t2);
		
		t1.add(stateVariableValue("a", optTrue));
		assertNotSame(t1, t2);

		t2.add(stateVariableValue("a", optTrue));
		assertTrue(t1.equals(t2));

		t2.add(stateVariableValue("b", optFalse));
		assertNotSame(t1, t2);

		t1.add(stateVariableValue("b", optFalse));
		assertEquals(t1, t2);
		
		// test for duplicate
		assertEquals(t1.getVariableValues().size(), 2);
		t1.add(stateVariableValue("b", optFalse));
		assertEquals(t1.getVariableValues().size(), 2);
		assertEquals(t1, t2);
		
		// same name different value
		t1.add(stateVariableValue("b", optTrue));
		assertEquals(t1.getVariableValues().size(), 3);
	}

	private StateVariableValue stateVariableValue(final String name, final Optional<Boolean> val) {
		return StateVariableValue.builder()
				.stateName(name)
				.value(val)
				.build();
	}

	@Test
	public void testContains() {
		StateVariableValue v = stateVariableValue("a", optTrue);
		StateValue t = StateValue.builder().build();
		assertFalse(t.getVariableValues().contains(v));
		
		t.add(stateVariableValue("a", optTrue));
		assertTrue(t.getVariableValues().contains(v));
	}
	
	@Test
	public void testHashSet() {
		Set<StateValue> t1 = new HashSet<>();
		Set<StateValue> t2 = new HashSet<>();
		assertEquals(t1, t2);
		
		StateValue s1 = StateValue.builder().build();
		s1.add(stateVariableValue("a", optTrue));
		t1.add(s1);
		assertNotSame(t1, t2);
		assertTrue(t1.contains(s1));
		assertFalse(t2.contains(s1));

		StateValue s2 = StateValue.builder().build();
		s2.add(stateVariableValue("a", optTrue));
		t2.add(s2);
		assertEquals(t1, t2);

		s1.add(stateVariableValue("b", optFalse));
		t1.add(s1);
		assertNotSame(t1, t2);

		s2.add(stateVariableValue("b", optFalse));
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
		
		StateValue s1 = StateValue.builder().build();
		s1.add(stateVariableValue("jk1jk2ring", optTrue));
		t1.add(s1);
	//	assertEquals(s1.toString(), "jk1jk2ringtrue");
		
		StateValue s2 = StateValue.builder().build();
		s2.add(stateVariableValue("jk1jk2ring", optTrue));
		assertTrue(s1.equals(s2));
		
		t1.get(0).add(stateVariableValue("b", optFalse));
	//	assertEquals(s1.toString(),"jk1jk2ringtrue,bfalse");
		assertFalse(s1.equals(s2));

		assertTrue(t1.contains(s1));
		
		s2.add(stateVariableValue("b", optFalse));

		assertTrue(t1.contains(s2));
		boolean deleted = t1.remove(s2);
		assertTrue(deleted);

		StateValue s3 = StateValue.builder().build();
		s3.add(stateVariableValue("jk1jk2ring", optTrue));
		s3.add(stateVariableValue("b", optFalse));
		assertFalse(t1.contains(s3));
	}
	
	@Test
	public void testHashSetContains() {
		Set<StateValue> t1 = new HashSet<>();
		
		StateValue s1 = StateValue.builder().build();
		s1.add(stateVariableValue("jk1jk2ring", optTrue));
		t1.add(s1);
	//	assertEquals(s1.toString(), "jk1jk2ringtrue");
		
		StateValue s2 = StateValue.builder().build();
		s2.add(stateVariableValue("jk1jk2ring", optTrue));
		assertTrue(s1.equals(s2));
		
		s1.add(stateVariableValue("b", optFalse));
		//assertEquals(s1.toString(),"jk1jk2ringtrue,bfalse");
		assertFalse(s1.equals(s2));

		//assertTrue(t1.contains(s1));
		
		s2.add(stateVariableValue("b", optFalse));
		
	//	assertTrue(t1.contains(s2));
		boolean deleted = t1.remove(s2);
	//	assertTrue(deleted);

		StateValue s3 = StateValue.builder().build();
		s3.add(stateVariableValue("jk1jk2ring", optTrue));
		s3.add(stateVariableValue("b", optFalse));
		assertFalse(t1.contains(s3));
	}
}