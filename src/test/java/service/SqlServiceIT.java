package service;

import executor.impl.StatementExecutorImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import service.impl.SqlServiceImpl;

import java.sql.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SqlServiceIT {

    private Connection connection;
    private Statement statement;

    @Before
    public void setUp() throws Exception {
        Class.forName("org.h2.Driver");
        connection = DriverManager.getConnection("jdbc:h2:~/test");
        statement = connection.createStatement();
    }

    @After
    public void tearDown() throws Exception {
        statement.execute("DROP ALL OBJECTS");
        statement.close();
        connection.close();
    }

    @Test
    public void shouldCreateATable() throws SQLException, ClassNotFoundException {
        String data = "{\"form\" : {\"bind_type\" : \"OOGA\", \"fields\" : [{\"name\" : \"BOOGA\"}]}}";
        SqlService service = new SqlServiceImpl(new StatementExecutorImpl());
        service.createTable(connection, data);
        int count = statement.executeUpdate("INSERT INTO OOGA (BOOGA) VALUES ('sample')");

        assertEquals(1, count);
    }

    @Test
    public void shouldCreateNestedTables() throws SQLException, ClassNotFoundException {
        String data = "{\"form\" : {\"bind_type\" : \"OOGA\", \"fields\" : [{\"name\" : \"BOOGA\",\"value\" : \"TEST\"}],\"sub_forms\" : [{\"name\": \"medications\",\"bind_type\": \"OOGA\",\"fields\" : [{\"name\" : \"BOOGA\",\"value\" : \"TEST\"}]}]}}";
        SqlService service = new SqlServiceImpl(new StatementExecutorImpl());
        service.createTable(connection, data);

        int count = statement.executeUpdate("INSERT INTO OOGA (BOOGA) VALUES ('sample')");
        int nestedTableCount = statement.executeUpdate("INSERT INTO medications_OOGA (BOOGA) VALUES ('sample')");
        assertEquals(1, count);
        assertEquals(1, nestedTableCount);
    }

    @Test
    public void shouldInsertIntoATable() throws SQLException, ClassNotFoundException {
        String data = "{\"form\" : {\"bind_type\" : \"OOGA\", \"fields\" : [{\"name\" : \"BOOGA\",\"value\" : \"sample\"}]}}";
        SqlService service = new SqlServiceImpl(new StatementExecutorImpl());
        service.createTable(connection, data);

        int first = service.createEntity(connection, data);
        int second = service.createEntity(connection, data);

        ResultSet resultSet = statement.executeQuery("select * from OOGA");
        int count = 0;
        while (resultSet.next()){
            count++;
        }
        assertEquals(2, count);

        assertTrue(second > first);
    }
}