package connection;

public class UnexpectedResponseException extends RuntimeException {
	
	public UnexpectedResponseException(String string) {
		super(string);
	}
}
