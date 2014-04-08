package model.query;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DeleteQueryTest {

    @Test
    public void shouldCreateADeleteQuery() throws Exception {
        String tableName = "OOGA";
        int id = 1;
        DeleteQuery query = new DeleteQuery(tableName, id);

        String expected = "DELETE FROM OOGA WHERE ID = 1;";
        assertEquals(expected,query.asSql());

    }
}
