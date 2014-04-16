package org.ict4h.service;

import org.ict4h.model.Form;

import java.sql.Connection;

public interface SqlService {
    boolean createTable(Connection connection, String data);
    int createEntity(Connection connection, String data, String modifiedByUser);

    int updateEntity(Connection connection, String data, int id, String modifiedByUser);
    boolean updateTable(Connection connection, String data);
    Form selectEntity(Connection connection, int id, String formDefinition);
}
