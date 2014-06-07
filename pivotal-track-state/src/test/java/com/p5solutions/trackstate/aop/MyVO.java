package com.p5solutions.trackstate.aop;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

/**
 * TestVO: 
 * 
 * @author Kasra Rasaee
 * @since 2009-02-11
 *
 */
@TrackState
public class MyVO implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private Long id;
  private String firstName;
  private String lastName;
  private Date birthDate;

  // public Object writeReplace() {
  // return this;
  // }
  //
  public MyVO() {
    super();
  }

  @Track(clazz = MyVO.class)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Track(clazz = MyVO.class)
  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  @Track(clazz = MyVO.class)
  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  @Track(clazz = MyVO.class)
  @DateTimeFormat(iso = ISO.DATE)
  public Date getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(Date birthDate) {
    this.birthDate = birthDate;
  }
}
