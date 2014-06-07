package com.p5solutions.core;

import java.util.List;

import com.p5solutions.core.json.JsonProperty;

public class JsonTestPerson {
  private String firstName;
  private String lastName;
  private Integer age;
  private JsonTestPersonAddress address;
  private List<JsonTestPhoneNumber> phoneNumber;

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public Integer getAge() {
    return age;
  }

  public void setAge(Integer age) {
    this.age = age;
  }

  public JsonTestPersonAddress getAddress() {
    return address;
  }

  public void setAddress(JsonTestPersonAddress address) {
    this.address = address;
  }

  @JsonProperty(JsonTestPhoneNumber.class)
  public List<JsonTestPhoneNumber> getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(List<JsonTestPhoneNumber> phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

}
