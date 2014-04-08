package service;

import model.Form;
import model.SubForm;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import service.impl.SqlServiceImpl;

import java.io.IOException;
import java.sql.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class SelectIT {

    private Connection connection;
    private Statement statement;
    private SqlServiceImpl service;

    @Before
    public void setUp() throws Exception {
        Class.forName("org.h2.Driver");
        connection = DriverManager.getConnection("jdbc:h2:~/test");
        statement = connection.createStatement();
        service = new SqlServiceImpl();
    }

    @After
    public void tearDown() throws Exception {
        statement.execute("DROP ALL OBJECTS");
        statement.close();
        connection.close();
    }

    @Test
    public void shouldSelectSingleEntity() throws Exception {
        String formName = "doctor_visit";

        int primaryId = setUpData(formName);

        Form form = service.getDataFor(connection, primaryId, formName, "medications_doctor_visit","tests_doctor_visit");

        assertEquals("doctor_visit",form.getName());
        assertEquals(4, form.getFields().size());
        assertEquals(2,form.getSubForms().size());
        SubForm firstSubForm = form.getSubForms().get(0);
        SubForm secondSubForm = form.getSubForms().get(1);
        int firstSubFormSize = firstSubForm.getFieldValues().collect(Collectors.toList()).size();
        assertEquals(2,firstSubFormSize);
        int secondSubFormSize = secondSubForm.getFieldValues().collect(Collectors.toList()).size();
        assertEquals(2,secondSubFormSize);
    }

    private int setUpData(String formName) throws IOException, SQLException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/subForms.json")));
        service.createTable(connection,data);
        service.createEntity(connection,data);
        PreparedStatement preparedStatement = connection.prepareStatement(String.format("SELECT ID FROM %s ORDER BY CREATED_AT DESC LIMIT 1;", formName));
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getInt("ID");
    }
}