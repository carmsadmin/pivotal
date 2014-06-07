package com.p5solutions.mapping;

import com.p5solutions.trackstate.annotation.MapClass;
import com.p5solutions.trackstate.annotation.MapClasses;
import com.p5solutions.trackstate.annotation.MapExpand;
import com.p5solutions.trackstate.annotation.MapProperty;

/**
 * MapToEntity1:
 * 
 * @author Kasra Rasaee
 * @since 2009-02-24
 * 
 */
@MapClasses(map = { @MapClass(to = ValueObject.class) })
public class Entity1 {

	private Long id;

	private String someText;

	private Long someLong;

	private EntityAddressEmbeddable address;

	@MapProperty(clazz = ValueObject.class)
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

	@MapExpand(clazz = { ValueObject.class })
	public EntityAddressEmbeddable getAddress() {
		return address;
	}

	public void setAddress(EntityAddressEmbeddable address) {
		this.address = address;
	}
}
