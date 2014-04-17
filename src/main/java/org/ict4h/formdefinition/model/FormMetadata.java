package org.ict4h.formdefinition.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class FormMetadata {
    public static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd 'At' hh:mm a");

    private String name;
    private Integer instanceId;
    private String entityId;
    private String createdBy;
    private String createdAt;

    public FormMetadata(String name, Integer instanceId, String entityId, String createdBy,String createdAt) {
        this.name = name;
        this.instanceId = instanceId;
        this.entityId = entityId;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public String getName() {
        return name;
    }

    public Integer getInstanceId() {
        return instanceId;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getCreatedAt() {
        Timestamp timestamp = Timestamp.valueOf(createdAt);
        return FORMAT.format(timestamp);
    }
}
