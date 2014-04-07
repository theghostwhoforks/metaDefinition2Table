package model.query;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SelectQueryTest {
    @Test
    public void shouldBuildDescribeQuery() throws Exception {
        SelectQuery selectQuery = new SelectQuery("OOGA");

        assertEquals("SELECT * FROM OOGA LIMIT 1;",selectQuery.asSql());
    }

    @Test
    public void shouldBuildSelectQueryForASpecificId() throws Exception {
        SelectQuery selectQuery = new SelectQuery("OOGA","ID",1);

        assertEquals("SELECT * FROM OOGA WHERE ID = 1;",selectQuery.asSql());
    }
}
