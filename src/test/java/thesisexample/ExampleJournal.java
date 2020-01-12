package thesisexample;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class ExampleJournal implements ExampleData {
    @Test
    public void example() {
        final JSONObject jsonObject = new JSONObject(CONTENT);
        assertEquals(20, jsonObject.getJSONArray("nodes").length());
        assertEquals(49, jsonObject.getJSONArray("connections").length());
        assertEquals(24, jsonObject.getJSONArray("channels").length());
        assertEquals(4, jsonObject.getJSONArray("readers").length());
        assertEquals(3, jsonObject.getJSONArray("writers").length());
    }
}
