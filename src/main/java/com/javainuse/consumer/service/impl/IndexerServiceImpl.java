package com.javainuse.consumer.service.impl;

import com.ge.gargoyle.indexer.constants.Constants;
import com.ge.gargoyle.indexer.service.IndexerService;
import com.ge.gargoyle.search.NodeSearchProto.NodeSearch;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class IndexerServiceImpl implements IndexerService {

  private static Logger LOGGER = LoggerFactory.getLogger(IndexerServiceImpl.class);

  @Value("${application.appsearch.urlscheme}")
  private String urlScheme;

  @Value("${application.appsearch.domainurl}")
  private String domainUrl;

  @Value("${application.appsearch.enginename}")
  private String engineName;

  @Value("${application.appsearch.accesskey}")
  private String accessKey;

  @Value("${application.appsearch.port}")
  private String port;

  @Autowired
  private RestTemplate restTemplate;

  @Override
  public void indexAppSearch(NodeSearch metadata)
    throws InvalidProtocolBufferException, ResourceAccessException {
    ResponseEntity<String> result =
      restTemplate.postForEntity(createUri(), createRequest(metadata), String.class);
    LOGGER.debug("ResultSet: ()", result);
  }

  private HttpEntity<String> createRequest(NodeSearch metadata)
    throws InvalidProtocolBufferException {
    HttpHeaders headers = new HttpHeaders();
    headers.set(
      HttpHeaders.AUTHORIZATION,
      new StringBuilder().append("Bearer ").append(accessKey).toString());
    headers.set(HttpHeaders.CONTENT_TYPE, "application/json");
    String jsonStr =
      JsonFormat.printer()
        .preservingProtoFieldNames()
        .includingDefaultValueFields()
        .print(metadata);
    return new HttpEntity<>(jsonStr, headers);
  }

  private URI createUri() {
    Map<String, String> uriDynamicTagsMap = new HashMap<>();
    uriDynamicTagsMap.put("engine", engineName);
    UriComponents uriComponents =
      UriComponentsBuilder.newInstance()
        .scheme(urlScheme)
        .host(domainUrl)
        .port(port)
        .path(Constants.APPSEARCH_INDEX_URL)
        .buildAndExpand(uriDynamicTagsMap);
    return uriComponents.toUri();
  }
}
