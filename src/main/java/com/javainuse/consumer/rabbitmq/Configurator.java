package com.javainuse.consumer.rabbitmq;

import com.ge.gargoyle.indexer.config.ApplicationConfig;
import com.ge.gargoyle.indexer.constants.Constants;
import java.util.HashMap;
import java.util.Map;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.ByteArrayMessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Configurator implements RabbitListenerConfigurer {

  @Autowired private ApplicationConfig applicationConfig;

  @Bean
  public Declarables inboundDeclarables() {
    TopicExchange inboundExchange = new TopicExchange(applicationConfig.getInboundExchange());

    Map<String, Object> arguments = new HashMap<String, Object>();
    arguments.put(Constants.X_DEAD_LETTER_EXCHANGE_ARG, applicationConfig.getRetryExchange());
    arguments.put(Constants.X_DEAD_LETTER_ROUTING_KEY_ARG, applicationConfig.getRetryRoutingKey());

    Queue topicQueue =
        new Queue(applicationConfig.getOriginalQueue(), true, false, false, arguments);
    return new Declarables(
        topicQueue,
        inboundExchange,
        BindingBuilder.bind(topicQueue)
            .to(inboundExchange)
            .with(applicationConfig.getOriginalRoutingKey()));
  }

  @Bean
  public Declarables deadLetterDeclarables() {
    TopicExchange retryExchange = new TopicExchange(applicationConfig.getRetryExchange());

    Map<String, Object> arguments = new HashMap<String, Object>();
    arguments.put(Constants.X_DEAD_LETTER_EXCHANGE_ARG, applicationConfig.getInboundExchange());
    arguments.put(
        Constants.X_DEAD_LETTER_ROUTING_KEY_ARG, applicationConfig.getOriginalRoutingKey());

    Queue retryQueue = new Queue(applicationConfig.getRetryQueue(), true, false, false, arguments);

    Queue parkingLotQueue =
        new Queue(applicationConfig.getDlqDefaultQueue()); // by default, this is a durable queue
    return new Declarables(
        retryQueue,
        parkingLotQueue,
        retryExchange,
        BindingBuilder.bind(retryQueue)
            .to(retryExchange)
            .with(applicationConfig.getRetryRoutingKey()),
        BindingBuilder.bind(parkingLotQueue)
            .to(retryExchange)
            .with(applicationConfig.getDlqDefaultRoutingKey()));
  }

  @Bean
  public ByteArrayMessageConverter converter() {
    ByteArrayMessageConverter converter = new ByteArrayMessageConverter();
    return converter;
  }

  @Bean
  public DefaultMessageHandlerMethodFactory handlerMethodFactory() {
    DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
    factory.setMessageConverter(converter());
    return factory;
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Override
  public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
    registrar.setMessageHandlerMethodFactory(handlerMethodFactory());
  }
}
