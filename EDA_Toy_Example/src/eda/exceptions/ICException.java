package eda.exceptions;

@SuppressWarnings("serial")
public class ICException extends Exception {

    public ICException() {
    }
    public ICException(String message) {
        super("ICMessege: " + message + "\n");
    }
    public ICException(Throwable cause) {
        super(cause);
    }
    public ICException(String message, Throwable cause) {
        super("ICMessege: " + message + "\n", cause);
    }	
}
