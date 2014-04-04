package service;

import executor.impl.StatementExecutorImpl;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import service.impl.SqlServiceImpl;

import java.io.File;
import java.io.IOException;
import java.sql.*;

import static org.junit.Assert.assertEquals;

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
    public void shouldCreateNestedTables() throws SQLException, ClassNotFoundException, IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/sampleData.json")));

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

        service.createEntity(connection, data);
        service.createEntity(connection, data);

        ResultSet resultSet = statement.executeQuery("select * from OOGA");
        int count = 0;
        while (resultSet.next()){
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    public void shouldInsertIntoNestedTables() throws SQLException, ClassNotFoundException, IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/subForms.json")));

        SqlService service = new SqlServiceImpl(new StatementExecutorImpl());
        service.createTable(connection, data);
        service.createEntity(connection,data);

        ResultSet resultSet = statement.executeQuery("select * from doctor_visit");

        int count = 0;
        while (resultSet.next()){
            count++;
        }
        assertEquals(1, count);


        ResultSet resultSet1 = statement.executeQuery("select * from medications_doctor_visit");

        int count1 = 0;
        while (resultSet1.next()){
            count1++;
        }
        assertEquals(1, count1);
    }

    @Ignore
    //TODO - fix update. Looking at count is not fool proof
    public void shouldUpdateTable() throws SQLException, IOException {
        File jsonFileVersionOne = FileUtils.toFile(this.getClass().getResource("/metamodel/update/simple_form_001.json"));
        File jsonFileVersionTwo = FileUtils.toFile(this.getClass().getResource("/metamodel/update/simple_form_002.json"));

        String versionOne = FileUtils.readFileToString(jsonFileVersionOne);
        String versionTwo = FileUtils.readFileToString(jsonFileVersionTwo);

        SqlService service = new SqlServiceImpl(new StatementExecutorImpl());
        service.createTable(connection, versionOne);
        int originalColumnCount = connection.prepareStatement("select * from delivery_details_and_pnc1").getMetaData().getColumnCount();

        service.updateTable(connection, versionTwo);

        int currentColumnCount = connection.prepareStatement("select * from delivery_details_and_pnc1").getMetaData().getColumnCount();
        assertEquals(originalColumnCount + 1, currentColumnCount);
    }
}