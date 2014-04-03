package model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SubForm implements Form {

    private String name;

    private List<Field> fields = new ArrayList<>();

    @SerializedName(value = "instances")
    private List<Map<String, String>> instances;

    @Override
    public String getName() {return name;}

    @Override
    public List<SubForm> getSubForms() {
        return null;
    }

    @Override
    public List<Field> getFields() {
        return fields;
    }

    public List<Map<String, String>> getInstance() {
        return instances;
    }
}
