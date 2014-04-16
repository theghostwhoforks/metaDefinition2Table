package org.ict4h.formdefinition.model;

public class FormMetadata {
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
}