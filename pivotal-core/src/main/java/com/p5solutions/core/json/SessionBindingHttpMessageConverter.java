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

import java.io.IOException;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

/**
 * The Interface SessionBindingHttpMessageConverter. Allows an
 * {@link HttpMessageConverter} to be session binding object aware when reading
 * /writing out Stream. This is used in conjunction with
 * {@link ExtendedAnnotationMethodHandlerAdapter} which will delegate most of
 * its work to the {@link AnnotationMethodHandlerAdapter}.
 * 
 * @author Kasra Rasaee
 * @since 2010-04-19
 * 
 * @param <T>
 *          the generic type
 */
public interface SessionBindingHttpMessageConverter<T> extends
    HttpMessageConverter<T> {

	/**
	 * Read.
	 *
	 * @param clazz the clazz
	 * @param bindObject the bind object
	 * @param webRequest the web request
	 * @param binder the binder
	 * @param inputMessage the input message
	 * @return the t
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws HttpMessageNotReadableException the http message not readable exception
	 */
	T read(Class<? extends T> clazz, T bindObject, WebRequest webRequest, 
			WebDataBinder binder, HttpInputMessage inputMessage) throws IOException,
	    HttpMessageNotReadableException;

}