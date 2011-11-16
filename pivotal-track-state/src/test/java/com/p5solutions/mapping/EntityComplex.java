package com.p5solutions.mapping;

import java.util.Date;

import com.p5solutions.trackstate.annotation.MapClass;
import com.p5solutions.trackstate.annotation.MapClasses;


@MapClasses(map = {
  @MapClass(to = ValueObjectComplex.class)
})
public class EntityComplex {
  public Date someDate;

  public Integer someInteger;

  public Date getSomeDate() {
    return someDate;
  }

  public void setSomeDate(Date someDate) {
    this.someDate = someDate;
  }

  public Integer getSomeInteger() {
    return someInteger;
  }

  public void setSomeInteger(Integer someInteger) {
    this.someInteger = someInteger;
  }

}
