package com.lumu.puzzle_decoder.exception;

public class FragmentClientException extends RuntimeException {

    public FragmentClientException(String message) { 
        super(message);
    }

    public FragmentClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
