package org.ict4h.model;

import com.google.gson.Gson;
import org.ict4h.constant.Constants;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FieldTest {

    @Test
    public void shouldGetFields() throws IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/sample.json")));
        FormDefinition definition = new Gson().fromJson(data, FormDefinition.class);
        assertNotNull(definition);
        List<Field> fields = definition.getForm().getFields();
        assertEquals(49, fields.size());
        Optional<Field> fieldOptional = fields.stream().findFirst();
        boolean isFieldPresent = fieldOptional.isPresent();
        assertEquals(true, isFieldPresent);
        assertEquals("formhub", fieldOptional.get().getName());
    }

    @Test
    public void shouldIgnoreFieldsWithReservedKeywords() throws Exception {
        Constants.RESERVED_KEYWORDS.stream().forEach(name -> {
            Field field = new FieldStub(name,"42");
            assertEquals(false,field.isNotReservedKeyword());
        });
    }

    @Test
    public void shouldAllowFieldWithoutAnyReservedKeywords() throws Exception {
        Field field = new FieldStub("surveyId","42");
        assertEquals(true,field.isNotReservedKeyword());
    }

    @Test
    public void shouldIgnoreFieldHavingSectionInIt() throws Exception {
        Field field = new FieldStub("section1","42");
        assertEquals(false,field.isNotSection());
    }


    @Test
    public void shouldAllowFieldNotHavingSectionInIt() throws Exception {
        Field field = new FieldStub("surveyId","42");
        assertEquals(true,field.isNotSection());
    }
}

class FieldStub extends Field {

    public FieldStub(String name, String value) {
        super(name, value);
    }
}