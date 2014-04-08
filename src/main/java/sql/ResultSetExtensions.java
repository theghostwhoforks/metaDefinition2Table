package sql;

import exception.MetaDataServiceRuntimeException;
import model.Field;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class ResultSetExtensions {

    public static List<Field> getParentTableFields(ResultSet resultSet) {
        try {
            if(resultSet.next()){
                return getColumnNames(resultSet.getMetaData()).stream().map(x -> {
                    try {
                        return new Field(x, resultSet.getString(x));
                    } catch (SQLException e) {
                        throw new MetaDataServiceRuntimeException("",e);
                    }
                }).collect(Collectors.toList());
            }
            else{
                return null;
            }
        } catch (SQLException e) {
            throw new MetaDataServiceRuntimeException("could not get the data from tables", e);
        }
    }

    public static List<Map<String, String>> tableAsAnInstance(ResultSet resultSet) {
        List<Map<String, String>> instances = new ArrayList<>();
        try {
            while (resultSet.next()) {
                Map<String, String> instance = new HashMap<>();
                for (String s : getColumnNames(resultSet.getMetaData())) {
                    instance.put(s,resultSet.getString(s));
                }
                instances.add(instance);
            }
        } catch (SQLException ex) {
            throw new MetaDataServiceRuntimeException("could not get the data from nested tables", ex);
        }
        return instances;
    }

    private static Set<String> getColumnNames(ResultSetMetaData metaData) throws SQLException {
        Set<String> fields = new HashSet();
        int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++)
            fields.add(metaData.getColumnName(i));
        return fields;
    }
}