package org.ict4h.formdefinition.model;

import com.google.gson.annotations.SerializedName;

public class FormDefinition {
    @SerializedName(value = "form")
    private Form form;

    public Form getForm() {
        return form;
    }
    public String getName() {
        return form.getName();
    }
}