package com.javainuse.consumer.service;

import com.ge.gargoyle.search.NodeSearchProto.NodeSearch;
import com.google.protobuf.InvalidProtocolBufferException;

public interface IndexerService {

  void indexAppSearch(NodeSearch node) throws InvalidProtocolBufferException;
}
