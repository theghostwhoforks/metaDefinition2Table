package service;

import java.sql.Connection;

public interface SqlService {
    boolean createTable(Connection connection, String data);
    default boolean updateTable(Connection connection,String data){
        return false;
    }

    boolean createEntity(Connection connection,String data);
    default boolean updateEntity(Connection connection,String data){
        return false;
    }
}
