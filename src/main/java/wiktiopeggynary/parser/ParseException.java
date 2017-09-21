package wiktiopeggynary.parser;

/**
 * @author Krzysztof Witukiewicz
 */
public class ParseException extends RuntimeException {
	
	public ParseException() {
	}
	
	public ParseException(String message) {
		super(message);
	}
	
	public ParseException(String message, Throwable cause) {
		super(message, cause);
	}
}

