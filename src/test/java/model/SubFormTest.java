package model;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.Assert.assertEquals;

public class SubFormTest {

    @Test
    public void shouldCreateSubForm() throws IOException {
        String data = FileUtils.readFileToString(FileUtils.toFile(this.getClass().getResource("/metamodel/subForms.json")));
        FormDefinition definition = new Gson().fromJson(data, FormDefinition.class);
        List<SubForm> subForms = definition.getForm().getSubForms();
        assertEquals(2,subForms.size());

        List<Field> firstSubFormFields = subForms.get(0).getFieldValues().collect(Collectors.toList());
        assertEquals(1, firstSubFormFields.size());

        List<Field> secondSubFormFields = subForms.get(1).getFieldValues().collect(Collectors.toList());
        assertEquals(1, secondSubFormFields.size());
    }
}
