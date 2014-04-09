package service;

import model.Form;

import java.sql.Connection;

public interface SqlService {
    boolean createTable(Connection connection, String data);
    boolean createEntity(Connection connection, String data, String modifiedByUser);

    boolean updateEntity(Connection connection, String data, int id, String modifiedByUser);
    boolean updateTable(Connection connection, String data);

    Form selectData(Connection connection, int id, String formName, String... subForms);
}
