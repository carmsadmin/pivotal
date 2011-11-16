package com.p5solutions.mapping;

import com.p5solutions.trackstate.annotation.MapClass;
import com.p5solutions.trackstate.annotation.MapClasses;
import com.p5solutions.trackstate.annotation.MapProperties;
import com.p5solutions.trackstate.annotation.MapProperty;

/**
 * The Class ValueObject.
 */
@MapClasses(map = {
    @MapClass(to = Entity1.class),
    @MapClass(to = Entity2.class),
    @MapClass(to = Entity3.class)
})
public class ValueObject {

  /** The id. */
  private Long id;

  /** The some text. */
  private String someText;

  /** The some long. */
  private Long someLong;

  /** The some boolean. */
  private Boolean someBoolean;

  private String addressLine1;

  private String addressLine2;

  private String extraAddressLine;
 
  private String city;

  private String postalCode;
  

  /** The complex. */
  private ValueObjectComplex complex;

  /**
   * Gets the id.
   * 
   * @return the id
   */
  public Long getId() {
    return this.id;
  }

  /**
   * Sets the id.
   * 
   * @param id
   *          the new id
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Gets the some text.
   * 
   * @return the some text
   */
  @MapProperties( {
      @MapProperty(clazz = Entity1.class), @MapProperty(clazz = Entity3.class)
  })
  public String getSomeText() {
    return someText;
  }

  /**
   * Sets the some text.
   * 
   * @param someText
   *          the new some text
   */
  public void setSomeText(String someText) {
    this.someText = someText;
  }

  /**
   * Gets the some long.
   * 
   * @return the some long
   */
  @MapProperties( {
      @MapProperty(clazz = Entity1.class), @MapProperty(clazz = Entity3.class)
  })
  public Long getSomeLong() {
    return someLong;
  }

  /**
   * Sets the some long.
   * 
   * @param someLong
   *          the new some long
   */
  public void setSomeLong(Long someLong) {
    this.someLong = someLong;
  }

  /**
   * Gets the some boolean.
   * 
   * @return the some boolean
   */
  @MapProperties( {
      @MapProperty(clazz = Entity2.class), @MapProperty(clazz = Entity3.class)
  })
  public Boolean getSomeBoolean() {
    return someBoolean;
  }

  /**
   * Sets the some boolean.
   * 
   * @param someBoolean
   *          the new some boolean
   */
  public void setSomeBoolean(Boolean someBoolean) {
    this.someBoolean = someBoolean;
  }

  /**
   * Sets the complex.
   * 
   * @param complex
   *          the new complex
   */
  public void setComplex(ValueObjectComplex complex) {
    this.complex = complex;
  }

  /**
   * Gets the complex.
   * 
   * @return the complex
   */
  @MapProperties( {
    @MapProperty(clazz = Entity3.class, to = "complexObject")
  })
  public ValueObjectComplex getComplex() {
    return complex;
  }

  @MapProperties( {
    @MapProperty(clazz = Entity1.class, to = "address.addressLine1")
  })
  public String getAddressLine1() {
    return addressLine1;
  }

  public void setAddressLine1(String addressLine1) {
    this.addressLine1 = addressLine1;
  }

  @MapProperties( {
    @MapProperty(clazz = Entity1.class, to = "address.addressLine2")
  })
  public String getAddressLine2() {
    return addressLine2;
  }

  public void setAddressLine2(String addressLine2) {
    this.addressLine2 = addressLine2;
  }

  @MapProperties( {
    @MapProperty(clazz = Entity1.class, to = "address.city")
  })
  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  @MapProperties( {
    @MapProperty(clazz = Entity1.class, to = "address.postalCode")
  })
  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  @MapProperties( {
    @MapProperty(clazz = Entity1.class, to = "address.extra.extraAddressLine")
  })
  public String getExtraAddressLine() {
    return extraAddressLine;
  }

  public void setExtraAddressLine(String extraAddressLine) {
    this.extraAddressLine = extraAddressLine;
  }

}