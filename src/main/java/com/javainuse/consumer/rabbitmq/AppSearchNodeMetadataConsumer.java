package com.javainuse.consumer.rabbitmq;

import com.ge.gargoyle.indexer.config.ApplicationConfig;
import com.ge.gargoyle.indexer.exception.NonRecoverableException;
import com.ge.gargoyle.indexer.exception.RecoverableException;
import com.ge.gargoyle.indexer.service.IndexerService;
import com.ge.gargoyle.indexer.util.AmqpUtils;
import com.ge.gargoyle.search.NodeSearchProto;
import com.google.protobuf.InvalidProtocolBufferException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

@Component
public class AppSearchNodeMetadataConsumer {

  private static final Logger logger = LoggerFactory.getLogger(AppSearchNodeMetadataConsumer.class);

  @Autowired IndexerService indexerService;

  @Autowired ApplicationConfig applicationConfig;

  @Autowired RabbitTemplate rabbitTemplate;

  @Autowired AmqpUtils amqpUtils;

  @RabbitListener(queues = "${application.rabbitmq.original.queue.name}")
  public void consume(Message message) {
    logger.info("Reading messages");
    try {
      processPayload(message);
    } catch (NonRecoverableException e) {
      logger.error(
          e.getMessage()
              + " - Unrecoverable Exception. Message will not be sent to a dead-letter queue.");
    } catch (RecoverableException e) {
      logger.error(
          e.getMessage() + " - Sending message to retry queue to be retried at a later date.");
      sendToRetryExchange(message, e.getRoutingKey());
    }
  }

  private void processPayload(Message message) {
    NodeSearchProto.NodeSearch node = null;
    try {
      node = NodeSearchProto.NodeSearch.parseFrom(message.getBody());
      if (logger.isDebugEnabled()) {
        logger.debug(
            "Node Id : {}, Node Name : {}, Repo Name : {},Creator Name : {}, Parent Name : {} ",
            Optional.ofNullable(node.getId()),
            Optional.ofNullable(node.getName()),
            Optional.ofNullable(node.getRepoName()),
            Optional.ofNullable(node.getCreatorName()),
            Optional.ofNullable(node.getParentName()));
      }
      indexerService.indexAppSearch(node);
    } catch (InvalidProtocolBufferException e) {
      logger.error("Processing payload | Failed to deserialize payload", e);
      throw new NonRecoverableException("Failed to deserialize payload", e);
    } catch (ResourceAccessException e) {
      logger.error("Failed to connect to service | Routing message to retry queue", e);
      throw new RecoverableException(e.getMessage(), applicationConfig.getRetryRoutingKey());
    }
  }

  private void sendToRetryExchange(Message message, String routingKey) {
    message = amqpUtils.setMessageExpiration(message);
    this.rabbitTemplate.send(applicationConfig.getRetryExchange(), routingKey, message);
  }
}
