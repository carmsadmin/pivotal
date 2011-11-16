package com.p5solutions.mapping;

import com.p5solutions.trackstate.annotation.MapClass;
import com.p5solutions.trackstate.annotation.MapClasses;

/**
 * MapToObject2:
 * 
 * @author Kasra Rasaee
 * @since 2009-02-24
 * 
 */
@MapClasses(map={
    @MapClass(to=ValueObject.class)
})
public class Entity2 {

  private Long id;
  
  private Boolean someBoolean;

  public Long getId() {
    return this.id;
  }
  
  public void setId(Long id) {
    this.id = id;
  }
  
  public Boolean getSomeBoolean() {
    return someBoolean;
  }

  public void setSomeBoolean(Boolean someBoolean) {
    this.someBoolean = someBoolean;
  }

}
