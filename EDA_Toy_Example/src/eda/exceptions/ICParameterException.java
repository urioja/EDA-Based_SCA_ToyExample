package eda.exceptions;

@SuppressWarnings("serial")
public class ICParameterException extends ICException {

    public ICParameterException (){
        super();
    }
    public ICParameterException (String messege){
        super(messege);
    }
    public ICParameterException (String messege, Throwable cause){
        super(messege, cause);
    }
    public ICParameterException (Throwable cause){
        super(cause);
    }
}
