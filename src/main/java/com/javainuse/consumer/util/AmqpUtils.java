package com.javainuse.consumer.util;

import com.ge.gargoyle.indexer.config.ApplicationConfig;
import com.ge.gargoyle.indexer.constants.Constants;
import java.util.ArrayList;
import java.util.Map;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AmqpUtils {

  @Autowired private ApplicationConfig applicationConfig;

  public int getMessageRetryCount(Message message) {
    Map<String, Object> headers = message.getMessageProperties().getHeaders();
    if (headers.get(Constants.X_DEATH_HEADER) == null) {
      return 0;
    }
    ArrayList<Map<String, Object>> xDeath =
        (ArrayList<Map<String, Object>>) headers.get(Constants.X_DEATH_HEADER);
    Long count = (Long) xDeath.get(0).get("count");
    return count.intValue();
  }

  private long getMessageTtl(Message message) {
    int retryCount = getMessageRetryCount(message);
    if (retryCount > applicationConfig.getRetryMax()) {
      retryCount = applicationConfig.getRetryMax();
    }
    return ((retryCount + 1) * applicationConfig.getRetryInterval());
  }

  public Message setMessageExpiration(Message message) {
    long ttl = getMessageTtl(message);
    message.getMessageProperties().setExpiration(Long.toString(ttl));
    return message;
  }
}
