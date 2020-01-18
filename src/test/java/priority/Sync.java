package priority;

import org.behnaz.rcsp.*;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class Sync {
    @Test
    public void replicatorNodes() throws IOException {
        ConnectorFactory factory = new ConnectorFactory();
        ConstraintConnector sync = factory.sync("A1", "A2");

        assertEquals(sync.getName(0), "A1");
        assertEquals(sync.getName(1), "A2");

        assertTrue(sync.extractVariablesAndUpdateConstraint(sync.getConstraint()).contains("A1TILDE"));
        assertTrue(sync.extractVariablesAndUpdateConstraint(sync.getConstraint()).contains("A2TILDE"));

        assertEquals(0, sync.getStates().length);
        assertEquals(0, sync.getNextStates().length);

        assertNotNull(sync.getConstraint());
//TODO
        final IOAwareStateValue initState = new IOAwareStateValue(StateValue.builder().build(), new IOComponent("a1", 1));

        final Solver solver = Solver.builder()
                .initState(initState)
                .connectorConstraint(new ConstraintConnector(sync.getConstraint(), sync.getVariableNames()))
                .build();

        solver.solve(3);
    }
}
