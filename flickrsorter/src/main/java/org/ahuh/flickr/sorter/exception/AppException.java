package org.ahuh.flickr.sorter.exception;

/**
 * Application Exception
 * @author Julien
 *
 */
public class AppException extends Exception {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 7711393498486820458L;
	
	/**
	 * Constructor
	 */
	public AppException(String message) {
		super(message);
	}
	
	/**
	 * Constructor
	 */
	public AppException(String message, Throwable e) {
		super(message, e);
	}
}
