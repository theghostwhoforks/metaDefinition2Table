package wrapper;

import exception.MetaDataServiceRuntimeException;
import model.Field;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public class ResultSetWrapper {
    private ResultSet resultSet;

    public ResultSetWrapper(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    private Set<String> getColumnNames(ResultSetMetaData resultSet) throws SQLException {
        Set<String> fields = new HashSet();
        int columnCount = resultSet.getColumnCount();
        for (int i = 1; i <= columnCount; i++)
            fields.add(resultSet.getColumnName(i));
        return fields;
    }

    public List<Field> getParentTableFields() {
        List<Field> fields = new ArrayList();
        try {
            while (resultSet.next()) {
                for (String columnName : getColumnNames(resultSet.getMetaData())) {
                    fields.add(new Field(columnName, resultSet.getString(columnName)));
                }
            }
            return fields;
        } catch (SQLException e) {
            throw new MetaDataServiceRuntimeException("could not get the data from tables", e);
        }
    }

    public void addInstancesForATable(List<Map<String, String>> instances) {
        try {
            while (resultSet.next()) {
                Map<String, String> instance = new HashMap<>();
                for (String columnName : getColumnNames(resultSet.getMetaData())) {
                    instance.put(columnName, resultSet.getString(columnName));
                }
                instances.add(instance);
            }
        } catch (SQLException ex) {
            throw new MetaDataServiceRuntimeException("could not get the data from nested tables", ex);
        }
    }
}
