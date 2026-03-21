package com.grouprace.core.common.result;

/**
 * A generic class that holds a value with its loading status.
 * @param <T> The type of data this Result will hold.
 */
public abstract class Result<T> {

    // Private constructor so nobody can create a raw "Result" object.
    // They MUST use Success, Error, or Loading.
    private Result() {}

    public static final class Success<T> extends Result<T> {
        public final T data;

        public Success(T data) {
            this.data = data;
        }
    }
    public static final class Error<T> extends Result<T> {
        public final Exception exception;
        public final String message;

        public Error(Exception exception, String message) {
            this.exception = exception;
            this.message = message;
        }
    }

   public static final class Loading<T> extends Result<T> {
        // No data needed, just a flag that work is happening
        public Loading() {}
    }
}