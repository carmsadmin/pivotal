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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.convert.ConversionService;

import com.p5solutions.core.aop.Targetable;
import com.p5solutions.core.utils.ReflectionUtility;


/**
 * The Class JsonSerializer.
 */
public class JsonSerializer {

  /** The Constant DATE_FORMAT_STRING. */
  public static final String DATE_FORMAT_STRING = "yyyy-MM-dd";
  
  /**
   * Date format based on the {@link #DATE_FORMAT_STRING}. Its reference Calendar is set with a UTC timezone so don't
   * reset it as this object is not thread safe.
   */
  public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_STRING);

  private ConversionService conversionService;

  protected Object valueFromPath(Object target, String path) {
    if (target == null) {
      return null;
    }

    Class<?> targetClazz = target.getClass();
    String[] p = path.split("\\.", 2);

    String fieldName = null;
    if (p.length > 0) {
      fieldName = p[0];
    }

    Method method = ReflectionUtility.findGetterMethod(targetClazz, fieldName);

    if (method == null) {
      throw new NullPointerException(JsonPropertyPath.class + " was specified on class type " + targetClazz
          + " with an invalid path of " + path + ". Please make sure that the path exists, it is case sensitive!");
    }

    if (p.length == 1) {
      return ReflectionUtility.getValue(method, target);
    } else {
      Object next = ReflectionUtility.getValue(method, target);
      return valueFromPath(next, p[1]);
    }
  }

  /**
   * Write start array tag.
   * 
   * @param output
   *          the output
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  protected void writeStartArrayTag(OutputStream output) throws IOException {
    output.write('[');
  }

  /**
   * Write end array tag.
   * 
   * @param output
   *          the output
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  protected void writeEndArrayTag(OutputStream output) throws IOException {
    output.write(']');
  }

  /**
   * Write start tag.
   * 
   * @param output
   *          the output
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  protected void writeStartTag(OutputStream output) throws IOException {
    output.write('{');
  }

  /**
   * Write end tag.
   * 
   * @param output
   *          the output
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  protected void writeEndTag(OutputStream output) throws IOException {
    output.write('}');
  }

  /**
   * Write colon.
   * 
   * @param output
   *          the output
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  protected void writeColon(OutputStream output) throws IOException {
    output.write(':');
  }

  /**
   * Write comma.
   * 
   * @param output
   *          the output
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  protected void writeComma(OutputStream output) throws IOException {
    output.write(',');
  }

  /**
   * Escaping new lines and quotes for json.
   * 
   * @param value
   * @return
   */
  protected String escape(String value) {
    return value.replace("\n", "\\n").replace("\r", "\\r").replace("\"", "\\\"");
  }

  /**
   * Write string.
   * 
   * @param output
   *          the output
   * @param value
   *          the value
   * @param charset
   *          the charset
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  protected void writeString(OutputStream output, String value, Charset charset) throws IOException {
    output.write(escape(value).getBytes(charset));
  }

  /**
   * Write between quotes.
   * 
   * @param output
   *          the output
   * @param value
   *          the value
   * @param charset
   *          the charset
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  protected void writeBetweenQuotes(OutputStream output, String value, Charset charset) throws IOException {
    output.write('"');
    writeString(output, value, charset);
    output.write('"');
  }

  /**
   * Write number.
   * 
   * @param output
   *          the output
   * @param value
   *          the value
   * @param charset
   *          the charset
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  protected void writeNumber(OutputStream output, Number value, Charset charset) throws IOException {
    String stringValue = value.toString();
    output.write(stringValue.getBytes(charset));
  }

  /**
   * Write date formatted as {@link DateHelper#DATE_FORMAT_STRING} unless otherwise specified with a
   * {@link JsonDateFormat} annotation.
   * 
   * @param output
   *          the output
   * @param method
   *          the method
   * @param value
   *          the value
   * @param charset
   *          the charset
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  protected void writeDate(OutputStream output, Method method, Date value, Charset charset) throws IOException {
    JsonDateFormat jsonFormat = null;
    if (method != null) {
      jsonFormat = ReflectionUtility.findAnnotation(method, JsonDateFormat.class);
    }

    DateFormat dateFormat = DATE_FORMAT;
    if (jsonFormat != null) {
      Locale locale = LocaleContextHolder.getLocale();
      dateFormat = new SimpleDateFormat(jsonFormat.format(), locale);
    }

    writeBetweenQuotes(output, dateFormat.format(value), charset);
  }

  /**
   * Write a boolean.
   * 
   * @param output
   *          the output
   * @param value
   *          the value
   * @param charset
   *          the charset
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  protected void writeBoolean(OutputStream output, Boolean source, Charset charset) throws IOException {
    output.write(source.toString().getBytes(charset));
  }

  /**
   * Write null.
   * 
   * @param output
   *          the output
   * @param charset
   *          the charset
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  protected void writeNull(OutputStream output, Charset charset) throws IOException {
    // writeBetweenQuotes(output, "", charset);
    output.write("null".getBytes(charset));
  }

  /**
   * Serialize.
   * 
   * @param source
   *          the source
   * @param output
   *          the output
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public void serialize(Object source, OutputStream output) throws IOException {
    serialize(source, output, Charset.forName("UTF-8"));
  }

  /**
   * Serialize.
   * 
   * @param source
   *          the source
   * @param output
   *          the output
   * @param charset
   *          the charset
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public void serialize(Object source, OutputStream output, Charset charset) throws IOException {
    serialize(null, source, output, charset);
  }

  /**
   * Serialize.
   * 
   * @param sourceMethod
   *          the source method
   * @param source
   *          the source
   * @param output
   *          the output
   * @param charset
   *          the charset
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public void serialize(Method sourceMethod, Object source, OutputStream output, Charset charset) throws IOException {
    if (charset == null) {
      charset = Charset.forName("UTF-8");
    }

    if (source == null) {
      writeNull(output, charset);
      return;
    }

    // if the object defines a targetable interface,
    // then its most likely wrapped around a proxy.
    if (source instanceof Targetable) {
      Targetable proxy = (Targetable) source;
      source = proxy.getTarget();
    }

    Class<?> valueClazz = source.getClass();

    // skip json transient types
    if (ReflectionUtility.hasAnyAnnotation(valueClazz, JsonTransient.class)) {
      return;
    }

    // write the necessary value to the output stream
    if (ReflectionUtility.isBooleanClass(valueClazz)) {
      writeBoolean(output, (Boolean) source, charset);
    } else if (ReflectionUtility.isDate(valueClazz)) {
      writeDate(output, sourceMethod, (Date) source, charset);
    } else if (ReflectionUtility.isNumberClass(valueClazz)) {
      writeNumber(output, (Number) source, charset);
    } else if (ReflectionUtility.isStringClass(valueClazz)) {
      writeBetweenQuotes(output, (String) source, charset);
    } else if (ReflectionUtility.isMapClass(valueClazz)) {
      serializeMap((Map<?, ?>) source, output, charset);
    } else if (ReflectionUtility.isCollectionClass(valueClazz)) {
      serializeCollection((Collection<?>) source, output, charset);
    } else if (ReflectionUtility.isArray(valueClazz)) {
      serializeArray((Object[]) source, output, charset);
    } else if (ReflectionUtility.isEnum(valueClazz)) {
      writeBetweenQuotes(output, source.toString(), charset);
    } else if (ReflectionUtility.isBlob(valueClazz)) {
      serializeBlob((Blob) source, output, charset);
    } else {
      // enter into this else statement when none of the types above match.
      // this will loop and recursively call each of the getter methods within
      // the source object, in turn probably recursively going through until its
      // end.

      writeStartTag(output);
      Class<?> clazz = source.getClass();
      List<Method> methods = ReflectionUtility.findGetMethodsWithNoParams(clazz);

      int counter = 0;

      // iterate each getter method
      for (Method method : methods) {
        JsonTransient jsonTransient = ReflectionUtility.findAnnotation(method, JsonTransient.class);
        // skip json transient methods
        if (jsonTransient != null) {
          continue;
        }

        // insert a comma if counter is greater than 0, meaning
        // that a previous item was serialized into the stream.
        if (counter > 0) {
          writeComma(output);
        }

        // append the property name
        String name = ReflectionUtility.buildFieldName(method);
        writeBetweenQuotes(output, name, charset);

        // separate the property name from the value.
        writeColon(output);

        // write out the value to the output stream
        Object value = ReflectionUtility.getValue(method, source);
        if (value != null) { // if the value is not null, then check for
          // check for json property path, if it exists, then
          // try to extract the value from the path specified
          JsonPropertyPath jsonPropertyPath = ReflectionUtility.findAnnotation(method, JsonPropertyPath.class);
          if (jsonPropertyPath != null) {
            value = valueFromPath(value, jsonPropertyPath.path());
          }
        }

        // serialize the object into the output stream
        serialize(method, value, output, charset);

        // serialized, as such increment counter
        counter++;
      }
      writeEndTag(output);
    }
  }

  /**
   * Serialize array.
   * 
   * @param array
   *          the array
   * @param output
   *          the output
   * @param charset
   *          the charset
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  protected void serializeArray(Object[] array, OutputStream output, Charset charset) throws IOException {
    if (array == null) {
      return;
    }

    writeStartArrayTag(output);
    int i = 0;
    int size = array.length;
    for (Object item : array) {
      serialize(item, output, charset);
      if (++i < size) {
        writeComma(output);
      }
    }
    writeEndArrayTag(output);
  }

  /**
   * Serialize map.
   * 
   * @param map
   *          the map
   * @param output
   *          the output
   * @param charset
   *          the charset
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  protected void serializeMap(Map<?, ?> map, OutputStream output, Charset charset) throws IOException {
    if (map == null) {
      return;
    }

    writeStartTag(output);
    int i = 0;
    int size = map.size();
    for (Entry<?, ?> entry : map.entrySet()) {
      writeBetweenQuotes(output, entry.getKey().toString(), charset);
      writeColon(output);
      serialize(entry.getValue(), output, charset);
      if (++i < size) {
        writeComma(output);
      }
    }
    writeEndTag(output);
  }

  /**
   * Serialize collection other than map.
   * 
   * @param collection
   *          the collection
   * @param output
   *          the output
   * @param charset
   *          the charset
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  protected void serializeCollection(Collection<?> collection, OutputStream output, Charset charset) throws IOException {
    if (collection == null) {
      return;
    }

    writeStartArrayTag(output);
    int i = 0;
    // int size = collection.size();
    for (Object item : collection) {
      if (i > 0) {
        writeComma(output);
      }

      // increment and add array index
      // writeNumber(output, i, charset);
      // writeColon(output);
      serialize(item, output, charset);

      // increment
      i++;
    }
    writeEndArrayTag(output);
  }

  protected void serializeBlob(Blob blob, OutputStream output, Charset charset) throws IOException {
    if (blob == null) {
      return;
    }
    try {
      output.write('"');
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      IOUtils.copy(blob.getBinaryStream(), bos);
      IOUtils.copy(new ByteArrayInputStream(escape(new String(bos.toByteArray(), charset)).getBytes(charset)), output);
      output.write('"');
    } catch (SQLException e) {
      throw new IOException("Cannot serialize the blob.", e);
    }
  }

  @Resource
  public void setConversionService(ConversionService conversionService) {
    this.conversionService = conversionService;
  }
}