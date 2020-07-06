package com.javainuse.consumer.exception;

public class RecoverableException extends RuntimeException {

  private String routingKey;

  public RecoverableException(String message, String routingKey) {
    super(message);
    this.routingKey = routingKey;
  }

  public String getRoutingKey() {
    return routingKey;
  }
}
