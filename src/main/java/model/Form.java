package model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Form {
    @SerializedName(value = "bind_type")
    private String name;
    private List<Field> fields = new ArrayList<>();

    public List<Field> getFields() {
        return fields;
    }

    public String getName() {
        return name;
    }
}
