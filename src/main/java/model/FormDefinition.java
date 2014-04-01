package model;

public class FormDefinition {
    private Form form;

    public Form getForm() {
        return form;
    }
    public String getName() {
        return form.getTableName();
    }
}