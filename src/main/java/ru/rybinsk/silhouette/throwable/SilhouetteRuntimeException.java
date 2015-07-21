/**
 * © Alexey Smirnov, 2013
 */
package ru.rybinsk.silhouette.throwable;

/**
 * @author Alexey Smirnov (smirnov89@bk.ru)
 * 
 */
public class SilhouetteRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SilhouetteRuntimeException() {
        super();
    }

    public SilhouetteRuntimeException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public SilhouetteRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public SilhouetteRuntimeException(String message) {
        super(message);
    }

    public SilhouetteRuntimeException(Throwable cause) {
        super(cause);
    }

}
