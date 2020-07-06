package com.javainuse.consumer.exception;

public class NonRecoverableException extends RuntimeException {

  public NonRecoverableException(String message, Throwable cause) {
    super(message, cause);
  }
}
