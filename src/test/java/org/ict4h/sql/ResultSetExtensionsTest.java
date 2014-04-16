package org.ict4h.sql;

import org.ict4h.exception.MetaDataServiceRuntimeException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.ict4h.sql.ResultSetExtensions;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ResultSetExtensionsTest {

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Test
    public void throwsExceptionWhenNextOfResultSetForParentTableThrowsAnException() throws Exception {
        ResultSet resultSet = mock(ResultSet.class);
        ResultSetMetaData resultSetMetaData = mock(ResultSetMetaData.class);

        thrown.expect(MetaDataServiceRuntimeException.class);

        when(resultSet.next()).thenThrow(new SQLException());
        when(resultSet.getMetaData()).thenReturn(resultSetMetaData);

        ResultSetExtensions.getParentTableFields(resultSet);
    }

    @Test
    public void throwsExceptionWhenNextOfResultSetThrowsAnException() throws Exception {
        ResultSet resultSet = mock(ResultSet.class);
        ResultSetMetaData resultSetMetaData = mock(ResultSetMetaData.class);

        thrown.expect(MetaDataServiceRuntimeException.class);

        when(resultSet.next()).thenThrow(new SQLException());
        when(resultSet.getMetaData()).thenReturn(resultSetMetaData);

        ResultSetExtensions.tableAsAnInstance(resultSet);
    }
}
