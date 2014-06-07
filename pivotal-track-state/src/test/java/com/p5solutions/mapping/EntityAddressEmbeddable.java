package com.p5solutions.mapping;

import com.p5solutions.trackstate.annotation.MapExpand;
import com.p5solutions.trackstate.annotation.MapProperties;
import com.p5solutions.trackstate.annotation.MapProperty;

public class EntityAddressEmbeddable {

  private String addressLine1;

  private String addressLine2;

  private String city;

  private String postalCode;

  private EntityAddressEmbeddableExtra extra;

  @MapExpand(clazz = {
    ValueObject.class
  })
  public EntityAddressEmbeddableExtra getExtra() {
    return extra;
  }

  public void setExtra(EntityAddressEmbeddableExtra extra) {
    this.extra = extra;
  }

  @MapProperties( {
    @MapProperty(clazz = ValueObject.class, to = "addressLine1")
  })
  public String getAddressLine1() {
    return addressLine1;
  }

  public void setAddressLine1(String addressLine1) {
    this.addressLine1 = addressLine1;
  }

  @MapProperties( {
    @MapProperty(clazz = ValueObject.class, to = "addressLine2")
  })
  public String getAddressLine2() {
    return addressLine2;
  }

  public void setAddressLine2(String addressLine2) {
    this.addressLine2 = addressLine2;
  }

  @MapProperties( {
    @MapProperty(clazz = ValueObject.class, to = "city")
  })
  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  @MapProperties( {
    @MapProperty(clazz = ValueObject.class, to = "postalCode")
  })
  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

}
