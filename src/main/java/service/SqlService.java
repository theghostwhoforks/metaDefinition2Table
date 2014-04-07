package service;

import model.Form;

import java.sql.Connection;
import java.util.List;

public interface SqlService {
    boolean createTable(Connection connection, String data);
    boolean createEntity(Connection connection, String data);

    default boolean updateEntity(Connection connection,String data){
        return false;
    }
    //TODO:FIX this
    @Deprecated
    boolean updateTable(Connection connection, String data);

    Form getDataFor(Connection connection,int id, String formName, String... subForms);
}
