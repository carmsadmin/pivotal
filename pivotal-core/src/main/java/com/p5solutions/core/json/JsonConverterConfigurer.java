/* Pivotal 5 Solutions Inc. - Core Java library for all other Pivotal Java Modules.
 * 
 * Copyright (C) 2011  KASRA RASAEE
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 */
package com.p5solutions.core.json;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

/**
 * JsonConverterConfigurer: Component class that configures our custom
 * {@link JsonHttpMessageConverter}.
 * 
 * Our {@link JsonHttpMessageConverter} is a replacement of
 * {@link MappingJacksonHttpMessageConverter}, it is a simpler implementation,
 * and allows use to map dot notation paths.
 * 
 * @author Kasra Rasaee
 * @since 2010-04-12
 * 
 * @see JsonHttpMessageConverter
 * @see JsonSerializer
 * @see JsonDeserializer
 */
@Component
public class JsonConverterConfigurer implements BeanPostProcessor {

	private JsonHttpMessageConverter jsonHttpMessageConverter;

	/**
	 * Instantiates a new json converter configurer.
	 */
	public JsonConverterConfigurer() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.springframework.beans.factory.config.BeanPostProcessor#
	 * postProcessBeforeInitialization(java.lang.Object, java.lang.String)
	 */
	public Object postProcessBeforeInitialization(Object bean, String beanName)
	    throws BeansException {
		return bean;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.springframework.beans.factory.config.BeanPostProcessor#
	 * postProcessAfterInitialization(java.lang.Object, java.lang.String)
	 */
	public Object postProcessAfterInitialization(Object bean, String beanName)
	    throws BeansException {

		/**
		 * This is more of a hack since Springs
		 * org.springframework.transaction.config
		 * .AnnotationDrivenBeanDefinitionParser does not allow us to inject a
		 * custom HttpMessageConverter. Look at SPR-7091 on jira.springframework.org
		 * !!!
		 */
		if (bean instanceof AnnotationMethodHandlerAdapter) {
			AnnotationMethodHandlerAdapter adapter = (AnnotationMethodHandlerAdapter) bean;
			HttpMessageConverter<?>[] converters = adapter.getMessageConverters();

			// new list
			List<HttpMessageConverter<?>> cs = new ArrayList<HttpMessageConverter<?>>();

			// append each
			for (HttpMessageConverter<?> converter : converters) {
				cs.add(converter);
			}

			// add in the custom json http message converter
			cs.add(this.jsonHttpMessageConverter);

			HttpMessageConverter<?>[] css = new HttpMessageConverter<?>[cs.size()];
			css = cs.toArray(css);
			adapter.setMessageConverters(css);
		}
		return bean;
	}

	public JsonHttpMessageConverter getJsonHttpMessageConverter() {
		return jsonHttpMessageConverter;
	}

	@Resource
	public void setJsonHttpMessageConverter(
	    JsonHttpMessageConverter jsonHttpMessageConverter) {
		this.jsonHttpMessageConverter = jsonHttpMessageConverter;
	}

}
