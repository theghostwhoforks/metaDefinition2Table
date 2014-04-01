package model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Form {
    @SerializedName(value = "bind_type")
    private String name;
    @SerializedName(value = "name")
    private String subFormName;
    private List<Field> fields = new ArrayList<>();
    @SerializedName(value = "sub_forms")
    private List<Form> forms = new ArrayList<>();

    public String getSubFormName() {return subFormName;}

    public List<Form> getSubForms() {return forms;}

    public List<Field> getFields() {
        return fields;
    }

    public String getName() {
        return name;
    }

    public boolean hasSubForms() {return getSubForms().size() > 0;}
}
