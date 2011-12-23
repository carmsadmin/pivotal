package com.p5solutions.core.jpa.orm;

import java.sql.Timestamp;
import java.util.Date;

import junit.framework.TestCase;

public class EntityMapperTest extends TestCase{

  public void testAssignableFromTimestamp() {
    Timestamp sqltimestamp = new Timestamp(new Date().getTime());
    
    assertTrue(Date.class.isAssignableFrom(Timestamp.class));
    
  }
}
