package model;

import com.google.gson.annotations.SerializedName;

public class FormDefinition {
    @SerializedName(value = "form")
    private ParentForm form;

    public ParentForm getForm() {
        return form;
    }
    public String getName() {
        return form.getName();
    }
}