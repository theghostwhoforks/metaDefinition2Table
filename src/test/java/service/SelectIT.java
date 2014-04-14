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
    private SqlService service;

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

        String formDefinition = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/subForms.json")));
        int primaryId = setUpData(formDefinition,formName);

        Form form = service.selectEntity(connection, primaryId, formDefinition);

        assertEquals("doctor_visit", form.getName());
        assertEquals(5, form.getFields().size());
        assertEquals(2, form.getSubForms().size());
        SubForm firstSubForm = form.getSubForms().get(0);
        SubForm secondSubForm = form.getSubForms().get(1);
        int firstSubFormSize = firstSubForm.getFieldValues().collect(Collectors.toList()).size();
        assertEquals(2, firstSubFormSize);
        int secondSubFormSize = secondSubForm.getFieldValues().collect(Collectors.toList()).size();
        assertEquals(2, secondSubFormSize);
    }

    private int setUpData(String formDefinition, String formName) throws IOException, SQLException {
        service.createTable(connection, formDefinition);
        service.createEntity(connection, formDefinition, "dataEntry1");
        PreparedStatement preparedStatement = connection.prepareStatement(String.format("SELECT ID FROM %s ORDER BY modified_at DESC LIMIT 1;", formName));
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getInt("ID");
    }
}
