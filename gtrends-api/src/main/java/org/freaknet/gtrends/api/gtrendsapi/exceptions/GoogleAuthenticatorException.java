package org.freaknet.gtrends.api.gtrendsapi.exceptions;

/**
 *
 * @author Marco Tizzoni <marco.tizzoni@gmail.com>
 */
public class GoogleAuthenticatorException extends Exception {

    /**
     * Creates a new instance of
     * <code>GoogleAuthenticatorException</code> without detail message.
     */
    public GoogleAuthenticatorException() {
    }

    /**
     * Constructs an instance of
     * <code>GoogleAuthenticatorException</code> with the specified detail
     * message.
     *
     * @param msg the detail message.
     */
    public GoogleAuthenticatorException(String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of
     * <code>GoogleAuthenticatorException</code> with the specified exception.
     *
     * @param e the detail message.
     */
    public GoogleAuthenticatorException(Exception e) {
        super(e);
    }
}
