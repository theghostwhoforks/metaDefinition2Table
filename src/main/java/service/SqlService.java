package service;

import java.sql.Connection;

public interface SqlService {
    boolean createTable(Connection connection, String data);
    default boolean updateTable(String data){
        return false;
    }
    default boolean createEntity(String data){
        return false;
    }
    default boolean updateEntity(String data){
        return false;
    }
}
