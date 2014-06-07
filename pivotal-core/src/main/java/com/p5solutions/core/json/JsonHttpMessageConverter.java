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
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import javax.annotation.Resource;

import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.scheduling.config.AnnotationDrivenBeanDefinitionParser;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

/**
 * JsonHttpMessageConverter: {@link HttpMessageConverter} that handles JSON
 * www.json.org data. One way to inject this into the
 * {@link AnnotationMethodHandlerAdapter} is to use the
 * {@link JsonConverterConfigurer}.
 * 
 * Ideally it would be nice to inject {@link HttpMessageConverter} via the
 * {@link AnnotationDrivenBeanDefinitionParser}. However currently, this is
 * restricted to only a set of {@link HttpMessageConverter}'s which are
 * hardcoded. Please see SPR-7091 at
 * http://jira.springframework.org/browse/SPR-7091
 * 
 * @author Kasra Rasaee
 * @since 2010-04-01
 * 
 * @see JsonConverterConfigurer
 * @see JsonDeserializer
 * @see JsonSerializer
 * @see ConversionService
 */
public class JsonHttpMessageConverter extends
    AbstractHttpMessageConverter<Object> implements
    SessionBindingHttpMessageConverter<Object> {

	/** The Constant DEFAULT_CHARSET. */
	public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

	/** The deserializer. */
	private JsonDeserializer deserializer;

	/** The serializer. */
	private JsonSerializer serializer;

	/** The conversion service. */
	private ConversionService conversionService;

	/**
	 * Instantiates a new json http message converter.
	 */
	public JsonHttpMessageConverter() {
		super(new MediaType("application", "json", DEFAULT_CHARSET));
	}

	/**
	 * Can read.
	 * 
	 * @param clazz
	 *          the clazz
	 * @param mediaType
	 *          the media type
	 * @return true, if successful
	 * @see org.springframework.http.converter.AbstractHttpMessageConverter#canRead
	 *      (java.lang.Class, org.springframework.http.MediaType)
	 */
	@Override
	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		return super.canRead(clazz, mediaType);
	}

	/**
	 * Deserialize.
	 * 
	 * @param clazz
	 *          the clazz
	 * @param bindObject
	 *          the bind object
	 * @param inputMessage
	 *          the input message
	 * @return the object
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("unchecked")
	protected Object deserialize(Class<?> clazz, Object bindObject,
			WebRequest webRequest, WebDataBinder binder, 
			HttpInputMessage inputMessage) throws IOException {
		
		///
		
		HttpHeaders headers = inputMessage.getHeaders();
		MediaType mediaType = headers.getContentType();
		Charset charset = mediaType.getCharSet();
		InputStream input = inputMessage.getBody();
		Object value = deserializer.deserialize(
				(Class<Object>) clazz, bindObject, 
				webRequest, binder, input, charset);

		return value;
	}

	/**
	 * Read.
	 * 
	 * @param clazz
	 *          the clazz
	 * @param bindObject
	 *          the bind object
	 * @param binder
	 *          the binder
	 * @param inputMessage
	 *          the input message
	 * @return the object
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @throws HttpMessageNotReadableException
	 *           the http message not readable exception
	 */
	@Override
	public Object read(Class<? extends Object> clazz, Object bindObject,
			WebRequest webRequest, WebDataBinder binder, HttpInputMessage inputMessage) throws IOException,
	    HttpMessageNotReadableException {
		return deserialize(clazz, bindObject, webRequest, binder, inputMessage);
	}

	/**
	 * Read internal.
	 * 
	 * @param clazz
	 *          the clazz
	 * @param inputMessage
	 *          the input message
	 * @return the object
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @throws HttpMessageNotReadableException
	 *           the http message not readable exception
	 * @see org.springframework.http.converter.AbstractHttpMessageConverter#readInternal
	 *      (java.lang.Class, org.springframework.http.HttpInputMessage)
	 */
	@Override
	protected Object readInternal(Class<? extends Object> clazz,
	    HttpInputMessage inputMessage) throws IOException,
	    HttpMessageNotReadableException {
		return read(clazz, null, null, null, inputMessage);
	}

	/**
	 * Supports.
	 * 
	 * @param clazz
	 *          the clazz
	 * @return true, if successful
	 * @see org.springframework.http.converter.AbstractHttpMessageConverter#supports
	 *      (java.lang.Class)
	 */
	@Override
	protected boolean supports(Class<?> clazz) {
		return true;
	}

	/**
	 * Write internal.
	 * 
	 * @param t
	 *          the t
	 * @param outputMessage
	 *          the output message
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @throws HttpMessageNotWritableException
	 *           the http message not writable exception
	 * @see org.springframework.http.converter.AbstractHttpMessageConverter#writeInternal
	 *      (java.lang.Object, org.springframework.http.HttpOutputMessage)
	 */
	@Override
	protected void writeInternal(Object t, HttpOutputMessage outputMessage)
	    throws IOException, HttpMessageNotWritableException {
		HttpHeaders headers = outputMessage.getHeaders();
		MediaType mediaType = headers.getContentType();
		Charset charset = mediaType.getCharSet();
		OutputStream output = outputMessage.getBody();
		serializer.serialize(t, output, charset);
	}

	/**
	 * Gets the deserializer.
	 * 
	 * @return the deserializer
	 */
	public JsonDeserializer getDeserializer() {
		return deserializer;
	}

	/**
	 * Sets the deserializer.
	 * 
	 * @param deserializer
	 *          the new deserializer
	 */
	public void setDeserializer(JsonDeserializer deserializer) {
		this.deserializer = deserializer;
	}

	/**
	 * Gets the serializer.
	 * 
	 * @return the serializer
	 */
	public JsonSerializer getSerializer() {
		return serializer;
	}

	/**
	 * Sets the serializer.
	 * 
	 * @param serializer
	 *          the new serializer
	 */
	public void setSerializer(JsonSerializer serializer) {
		this.serializer = serializer;
	}

	/**
	 * Sets the conversion service.
	 * 
	 * @param conversionService
	 *          the new conversion service
	 */
	@Resource()
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	/**
	 * Gets the conversion service.
	 * 
	 * @return the conversion service
	 */
	public ConversionService getConversionService() {
		return conversionService;
	}

}