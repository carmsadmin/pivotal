package com.p5solutions.search;

import java.util.Map;

public interface FilterStorage<STATE extends FilterStorageState, GROUP extends FilterStorageStateGroup> {
  
  GROUP saveGroup(GROUP group);
  
  GROUP loadGroup(Long stateGroupId);
  
  STATE loadState(Long stateId);
  
  <T extends Filter<STATE>> STATE save(T filter);
  
  <T extends Filter<STATE>> T load(Long stateId);
  
  <T extends Filter<STATE>> Map<Object, T> loadAll(Long stateGroupId);
  
  void setFilterUtility(FilterUtility filterEntity);
}
