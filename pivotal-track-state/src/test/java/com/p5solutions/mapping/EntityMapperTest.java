package com.p5solutions.mapping;

import java.util.Date;

import junit.framework.TestCase;

/**
 * MapTest: 
 * 
 * @author Kasra Rasaee
 * @since 2009-02-24
 *
 */
public class EntityMapperTest extends TestCase {
  
	public void testMapToSameType() {
		Entity1 entity1_1 = new Entity1();
		entity1_1.setId(1234L);
		entity1_1.setSomeText("Hello World 1");
		entity1_1.setSomeLong(15525235L);
		
		EntityAddressEmbeddable address = new EntityAddressEmbeddable();
		address.setAddressLine1("123 Smith Rd.");
		address.setCity("Ottawa");
		address.setPostalCode("K2G5K8");
		entity1_1.setAddress(address);
		
		Entity1 entity1_2 = new Entity1();
		entity1_2.setSomeLong(2523523525L);
		
		Object o = EntityMapper.mapOneToOne(entity1_1, entity1_2);
		assertNotNull(o);
	}
	
  public void testMapEntityToValueObject() {
    Entity1 entity1 = new Entity1();
    entity1.setId(100L);
    entity1.setSomeLong(12345L);
    entity1.setSomeText("Hello World");
    
    EntityAddressEmbeddable address = new EntityAddressEmbeddable();
    address.setAddressLine1("522 Laurier Ave W");
    address.setAddressLine2("Unit 2212");
    address.setCity("Ottawa");
    address.setPostalCode("K1R 2B1");
    EntityAddressEmbeddableExtra addressExtra = new EntityAddressEmbeddableExtra();
    addressExtra.setExtraAddressLine("Back door entrance");
    address.setExtra(addressExtra);
    
    entity1.setAddress(address);
    
    Object o = EntityMapper.map(entity1);
    
    Entity2 entity2 = new Entity2();
    entity2.setSomeBoolean(true);
    entity2.setId(123L);
    o = EntityMapper.map(entity2, o);

    EntityComplex complex = new EntityComplex();
    complex.setSomeDate(new Date());
    complex.setSomeInteger(123);
    
    Entity3 entity3 = new Entity3();
    entity3.setSomeBoolean(false);
    entity3.setSomeLong(1231223L);
    entity3.setSomeText("Hello World 2");
    entity3.setComplexObject(complex);
    
    o = EntityMapper.map(entity3, o);
    
    assertNotNull(o);
  }
  
  public void testMapping() {
    
    ValueObject me = new ValueObject();
    me.setId(123L);
    me.setSomeBoolean(true);
    me.setSomeText("Hello World!");
    me.setSomeLong(5555L);
    me.setAddressLine1("445 Laurier Ave W");
    me.setAddressLine2("Unit 505");
    me.setCity("Ottawa");
    me.setPostalCode("K1R 0A2");
    me.setExtraAddressLine("Back Door");
    
    ValueObjectComplex complex = new ValueObjectComplex();
    complex.setSomeDate(new Date());
    complex.setSomeInteger(553311);
    me.setComplex(complex);
    
    Object o = EntityMapper.map(me);
    
    System.out.println(o);
  }
  
}
