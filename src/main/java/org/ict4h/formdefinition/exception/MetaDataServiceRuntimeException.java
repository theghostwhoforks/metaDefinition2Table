package org.ict4h.formdefinition.exception;

public class MetaDataServiceRuntimeException extends RuntimeException {
    public MetaDataServiceRuntimeException(String message,Throwable ex) {
        super(message,ex);
    }

    public MetaDataServiceRuntimeException(String message) {
        super(message);
    }
}
