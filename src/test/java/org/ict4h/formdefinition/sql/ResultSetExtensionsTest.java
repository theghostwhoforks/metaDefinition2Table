package org.ict4h.formdefinition.sql;

import org.ict4h.formdefinition.constant.Constants;
import org.ict4h.formdefinition.exception.MetaDataServiceRuntimeException;
import org.ict4h.formdefinition.model.FormMetadata;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

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

        ResultSetExtensions.extractFields(resultSet);
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

    @Test
    public void shouldExtractListOfPrimaryKeysFromATable() throws Exception {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        stub(resultSet.getInt(Constants.ID)).toReturn(1);
        String entityId = "B213";
        stub(resultSet.getString(Constants.MODIFIED_AT)).toReturn("date");
        stub(resultSet.getString(Constants.MODIFIED_BY)).toReturn("me");
        String formName =   "doctor_visit";
        List<FormMetadata> list = ResultSetExtensions.mapToListOfFormMetadata(resultSet, formName, entityId);
        assertEquals(1, list.size());
        assertEquals(formName,list.get(0).getName());
    }

    @Test
    public void shouldReturnEmptyListWhenResultSetIsEmpty() throws Exception {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false);
        String formName = "doctor_visit";
        List<FormMetadata> integers = ResultSetExtensions.mapToListOfFormMetadata(resultSet, formName, null);
        assertEquals(0, integers.size());
        verify(resultSet,never()).getInt(anyString());
    }
}
