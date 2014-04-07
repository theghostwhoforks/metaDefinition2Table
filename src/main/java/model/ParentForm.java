package model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ParentForm implements Form {
    @SerializedName(value = "bind_type")
    private String name;

    private List<Field> fields = new ArrayList<>();

    @SerializedName(value = "sub_forms")
    private List<SubForm> subForms = new ArrayList<>();

    public ParentForm() {
    }

    public ParentForm(List<Field> fields, List<SubForm> subForms) {
        this.fields = fields;
        this.subForms = subForms;
    }

    @Override
    public List<SubForm> getSubForms() {return subForms;}

    @Override
    public List<Field> getFields() {
        return fields;
    }

    @Override
    public String getName() { return name; }

}
