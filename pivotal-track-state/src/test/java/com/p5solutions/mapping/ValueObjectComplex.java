package com.p5solutions.mapping;

import java.util.Date;

import com.p5solutions.trackstate.annotation.MapClass;
import com.p5solutions.trackstate.annotation.MapClasses;

/**
 * MapMeComplexObject: 
 * 
 * @author Kasra Rasaee
 * @since 2009-02-24
 *
 */
@MapClasses(map = {
    @MapClass(to = EntityComplex.class)
})
public class ValueObjectComplex {
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
