package model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Form {
    @SerializedName(value = "bind_type")
    private String tableName;

    @SerializedName(value = "name")
    private String name;

    private List<Field> fields = new ArrayList<>();

    @SerializedName(value = "sub_forms")
    private List<Form> subForms = new ArrayList<>();

    public String getName() {return name;}

    public List<Form> getSubForms() {return subForms;}

    public List<Field> getFields() {
        return fields;
    }

    public String getTableName() { return tableName; }

}
