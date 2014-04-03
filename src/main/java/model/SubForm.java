package model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class SubForm implements Form {

    public static final String INSTANCE_ID_FIELD = "id";
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

    public Stream<Field> getFieldValues() {
        return instances.stream().flatMap(instance ->
                (Stream<Field>) instance.keySet().stream()
                .map(key -> new Field(key, instance.get(key))))
                .filter(x -> !x.getName().equals(INSTANCE_ID_FIELD));
    }
}