package com.javainuse.consumer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

  @Value("${application.rabbitmq.inbound.exchange.name}")
  private String inboundExchange;

  @Value("${application.rabbitmq.original.queue.name}")
  private String originalQueue;

  @Value("${application.rabbitmq.original.routing.key}")
  private String originalRoutingKey;

  @Value("${application.rabbitmq.retry.exchange.name}")
  private String retryExchange;

  @Value("${application.rabbitmq.retry.queue.name}")
  private String retryQueue;

  @Value("${application.rabbitmq.retry.routing.key}")
  private String retryRoutingKey;

  @Value("${application.rabbitmq.dlq.default.queue.name}")
  private String dlqDefaultQueue;

  @Value("${application.rabbitmq.dlq.default.routing.key}")
  private String dlqDefaultRoutingKey;

  @Value("${application.rabbitmq.retry.interval}")
  private int retryInterval;

  @Value("${application.rabbitmq.retry.max}")
  private int retryMax;

  public String getInboundExchange() {
    return inboundExchange;
  }

  public void setInboundExchange(String inboundExchange) {
    this.inboundExchange = inboundExchange;
  }

  public String getOriginalQueue() {
    return originalQueue;
  }

  public void setOriginalQueue(String originalQueue) {
    this.originalQueue = originalQueue;
  }

  public String getOriginalRoutingKey() {
    return originalRoutingKey;
  }

  public void setOriginalRoutingKey(String originalRoutingKey) {
    this.originalRoutingKey = originalRoutingKey;
  }

  public String getRetryExchange() {
    return retryExchange;
  }

  public void setRetryExchange(String retryExchange) {
    this.retryExchange = retryExchange;
  }

  public String getRetryQueue() {
    return retryQueue;
  }

  public void setRetryQueue(String retryQueue) {
    this.retryQueue = retryQueue;
  }

  public String getRetryRoutingKey() {
    return retryRoutingKey;
  }

  public void setRetryRoutingKey(String retryRoutingKey) {
    this.retryRoutingKey = retryRoutingKey;
  }

  public String getDlqDefaultQueue() {
    return dlqDefaultQueue;
  }

  public void setDlqDefaultQueue(String dlqDefaultQueue) {
    this.dlqDefaultQueue = dlqDefaultQueue;
  }

  public String getDlqDefaultRoutingKey() {
    return dlqDefaultRoutingKey;
  }

  public void setDlqDefaultRoutingKey(String dlqDefaultRoutingKey) {
    this.dlqDefaultRoutingKey = dlqDefaultRoutingKey;
  }

  public int getRetryInterval() {
    return retryInterval;
  }

  public void setRetryInterval(int retryInterval) {
    this.retryInterval = retryInterval;
  }

  public int getRetryMax() {
    return retryMax;
  }

  public void setRetryMax(int retryMax) {
    this.retryMax = retryMax;
  }
}
