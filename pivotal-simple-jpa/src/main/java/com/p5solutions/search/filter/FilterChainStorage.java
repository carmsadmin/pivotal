package com.p5solutions.search.filter;

import java.util.Map;

public interface FilterChainStorage<CHAIN_STATE extends FilterChainStorageState, GROUP extends  FilterChainStorageStateGroup> {

  GROUP saveGroup(FilterChainStorageStateGroup group);

  GROUP loadGroup(Long stateGroupId);


  <T extends FilterChain<CHAIN_STATE>> CHAIN_STATE save(T filter);

  <T extends FilterChain<CHAIN_STATE>> T load(Long stateId);

  <T extends FilterChain<CHAIN_STATE>> Map<Object, T> loadAll(Long stateGroupId);

  void setFilterUtility(FilterUtility filterEntity);

}
