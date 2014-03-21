package exception;

public class MetaDataServiceRuntimeException extends RuntimeException {
    public MetaDataServiceRuntimeException(String message,Throwable ex) {
        super(message,ex);
    }
}
