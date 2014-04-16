package org.ict4h.formdefinition.service;

import org.ict4h.formdefinition.model.Form;
import org.ict4h.formdefinition.model.FormMetadata;

import java.sql.Connection;
import java.util.List;

public interface SqlService {
    boolean createTable(Connection connection, String data);
    int createEntity(Connection connection, String data, String modifiedByUser);

    int updateEntity(Connection connection, String data, int id, String modifiedByUser);
    boolean updateTable(Connection connection, String data);
    Form selectEntity(Connection connection, int id, String formDefinition);

    List<FormMetadata> listMetadata(Connection connection, String entityId, String formName);
}
