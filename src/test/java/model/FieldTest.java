package model;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FieldTest {

    @Test
    public void shouldName() throws IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/sample.json")));
        FormDefinition definition = new Gson().fromJson(data, FormDefinition.class);
        assertNotNull(definition);
        assertEquals(1,definition.getForm().getFields().size());
    }
}
