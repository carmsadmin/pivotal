package com.p5solutions.mapping;

import com.p5solutions.trackstate.annotation.MapClass;
import com.p5solutions.trackstate.annotation.MapClasses;
import com.p5solutions.trackstate.annotation.MapProperty;

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
public class Entity3 {

  private Long id;
  
  private String someText;

  private Long someLong;

  private Boolean someBoolean;

  private EntityComplex complexObject;
  
  public Long getId() {
    return this.id;
  }
  
  public void setId(Long id) {
    this.id = id;
  }
  
  public String getSomeText() {
    return someText;
  }

  public void setSomeText(String someText) {
    this.someText = someText;
  }

  public Long getSomeLong() {
    return someLong;
  }

  public void setSomeLong(Long someLong) {
    this.someLong = someLong;
  }

  public Boolean getSomeBoolean() {
    return someBoolean;
  }

  
  public void setSomeBoolean(Boolean someBoolean) {
    this.someBoolean = someBoolean;
  }

  public void setComplexObject(EntityComplex complexObject) {
    this.complexObject = complexObject;
  }

  @MapProperty(clazz=ValueObject.class, to="complex")
  public EntityComplex getComplexObject() {
    return complexObject;
  }

}
