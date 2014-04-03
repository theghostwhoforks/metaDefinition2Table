package model;

import java.util.List;

public interface Form {
    List<Field> getFields();
    String getName();
    List<SubForm> getSubForms();
}
