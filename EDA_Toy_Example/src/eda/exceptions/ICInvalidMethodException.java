package eda.exceptions;

@SuppressWarnings("serial")
public class ICInvalidMethodException extends ICException {

    public ICInvalidMethodException() {
    }
    public ICInvalidMethodException(String message) {
        super(message);
    }
    public ICInvalidMethodException(Throwable cause) {
        super(cause);
    }
    public ICInvalidMethodException(String message, Throwable cause) {
        super(message, cause);
    }	
}