package org.ict4h.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Form{
    @SerializedName(value = "bind_type")
    private String name;

    private List<Field> fields = new ArrayList<>();

    @SerializedName(value = "sub_forms")
    private List<SubForm> subForms = new ArrayList<>();

    public Form() {}

    public Form(String name, List<Field> fields, List<SubForm> subForms) {
        this.name = name;
        this.fields = fields;
        this.subForms = subForms;
    }

    public List<SubForm> getSubForms() {return subForms;}

    public List<Field> getFields() {
        return fields;
    }

    public String getName() { return name; }
}
