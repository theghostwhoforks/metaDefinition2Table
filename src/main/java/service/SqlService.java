package service;

import model.Form;

import java.sql.Connection;

public interface SqlService {
    boolean createTable(Connection connection, String data);
    boolean createEntity(Connection connection, String data);

    boolean updateEntity(Connection connection,String data, int id);
    boolean updateTable(Connection connection, String data);

    Form selectData(Connection connection, int id, String formName, String... subForms);
}
