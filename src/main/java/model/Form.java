package model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Form {
    @SerializedName(value = "bind_type")
    private String name;
    private List<Field> fields = new ArrayList<>();
    @SerializedName(value = "entity_id")
    private Integer entityId;

    public List<Field> getFields() {
        return fields;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public String getName() {
        return name;
    }
}
