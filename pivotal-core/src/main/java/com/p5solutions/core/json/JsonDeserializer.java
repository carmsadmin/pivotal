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

import java.beans.PropertyEditor;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.WebRequest;

import com.p5solutions.core.aop.Targetable;
import com.p5solutions.core.utils.Comparison;
import com.p5solutions.core.utils.NumberUtils;
import com.p5solutions.core.utils.ReflectionUtility;

/**
 * The Class JsonDeserializer: Standard JSON deserializer. Does not need to be
 * prototype based, this class is completely thread-safe and can be used in a
 * singleton fashion.
 * 
 * @author Kasra Rasaee
 * @since 2010-04-10
 * 
 * @see JsonConverterConfigurer
 * @see JsonHttpMessageConverter
 * @see JsonSerializer
 * @see ConversionService
 * @see GenericConverter
 */
public class JsonDeserializer {

  /** The logger. */
  private static Log logger = LogFactory.getLog(JsonDeserializer.class);

  /** The Constant DEFAULT_BUFFER_SIZE. */
  private static final int DEFAULT_BUFFER_SIZE = 4;

  /** The conversion service. */
  private ConversionService conversionService;

  /**
   * Throw input stream reader exception.
   */
  protected void throwInputStreamReaderException(Reader reader) {
    if (reader == null) {
      throw new NullPointerException(getClass() + " cannot deserialize a null input stream. " + "You must instantiate with a vaild JSON input stream.");
    }

    try {
      if (!reader.ready()) {
        logger.error("Input stream reader is not ready to be read. Check instance of " + getClass());
      }
    } catch (Exception e) {
      logger.error(e + " : " + "Input stream reader is not ready to be read. Check instance of " + getClass());
    }

  }

  /**
   * Copy.
   * 
   * @param source
   *          the source
   * @param destination
   *          the destination
   */
  protected void copy(char[] source, char[] destination) {
    copy(source, destination, 0);
  }

  /**
   * Copy array from source to destination, starting from index zero, up to the
   * maximum length of destination array.
   * 
   * @param source
   *          the source
   * @param destination
   *          the destination
   * @param start
   *          the start
   */
  protected void copy(char[] source, char[] destination, int start) {
    // if (source.length >= destination.length) {
    int j = start;
    for (int i = 0; i < destination.length; i++) {
      if (source.length == j) {
        break;
      }
      destination[i] = source[j++];
    }
  }

  /**
   * Copy.
   * 
   * @param a
   *          the a
   * @param pos
   *          the pos
   * @return the char[]
   */
  public static char[] copy(char[] a, int pos) {
    // char[] { 1, 2, 3, 4 };
    // count = 3
    // result { 4, , , }
    char[] dest = new char[a.length - pos];
    int j = 0;
    for (int i = pos; i < a.length; i++) {
      dest[j++] = a[i];
    }
    return dest;
  }

  /**
   * Checks if is start array tag.
   * 
   * @param c
   *          the c
   * @return true, if is start array tag
   */
  protected boolean isStartArrayTag(char c) {
    return c == '[';
  }

  /**
   * Checks if is end array tag.
   * 
   * @param c
   *          the c
   * @return true, if is end array tag
   */
  protected boolean isEndArrayTag(char c) {
    return c == ']';
  }

  /**
   * Checks if is start tag.
   * 
   * @param c
   *          the c
   * @return true, if is start tag
   */
  protected boolean isStartTag(char c) {
    return c == '{';
  }

  /**
   * Checks if is colon tag.
   * 
   * @param c
   *          the c
   * @return true, if is colon tag
   */
  protected boolean isColonTag(char c) {
    return c == ':';
  }

  /**
   * Checks if is end tag.
   * 
   * @param c
   *          the c
   * @return true, if is end tag
   */
  protected boolean isEndTag(char c) {
    return c == '}';
  }

  /**
   * Checks if is comma tag.
   * 
   * @param c
   *          the c
   * @return true, if is comma tag
   */
  protected boolean isCommaTag(char c) {
    return c == ',';
  }

  /**
   * Checks if is quotes.
   * 
   * @param c
   *          the c
   * @return true, if is quotes
   */
  protected boolean isQuotes(char c) {
    return c == '"';
  }

  /**
   * The Class Read.
   */
  protected class Read {

    /** The count. */
    int count;

    /** The buffer. */
    char[] buffer;
  }

  /**
   * Read.
   * 
   * @param previous
   *          the previous
   * @return the read
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  protected Read read(Reader reader, char[] previous, int bufferSize) throws IOException {
    Read read = new Read();
    if (previous != null && previous.length > 0) {
      read.buffer = previous;
      read.count = previous.length;
    } else {
      read.buffer = new char[bufferSize];
      read.count = reader.read(read.buffer);
    }
    return read;
  }

  // TODO comments.
  protected boolean attemptMapMapping(String fieldName, Object target, Object nvpValue, Class<?> returnType, JsonProperty jdt, WebRequest webRequest,
      WebDataBinder binder) {

    // quick return if the target field "method" is not of type List<?>
    // e.g. public List<?> pojo.getNames<String>() then continue
    if (!ReflectionUtility.isMapClass(returnType)) {
      return false;
    }

    if (isListWithinList(nvpValue)) {
      // cast the list of list
      List<List<?>> listOfObjects = (List<List<?>>) nvpValue;

      // is array
      if (ReflectionUtility.isMapClass(returnType) && jdt != null) {
        // TODO figure this out
        Map<Object, Object> put = new HashMap<Object, Object>();

        // iterate each element, and add it to the list
        for (List<?> list : listOfObjects) {
          if (isNameValueList(list)) {

            // recurisvely walk each list element
            List<NameValuePair> nvl = (List<NameValuePair>) list;
            Object next = ReflectionUtility.newInstance(jdt.value());
            mapJsonDataToPOJO(next, nvl, webRequest, binder);
            // add the walked element to the arraylist
            // put.put(next);
          }
        }
      }
      return true;
    } else if (nvpValue instanceof List<?>) {
      List<NameValuePair> listOfObjects = (List<NameValuePair>) nvpValue;
      // TODO cannot assume its a hashmap, maybe a treemap?
      Map<Object, Object> putMap = new HashMap<Object, Object>();
      if (listOfObjects.size() > 0) {
        for (NameValuePair nvp : listOfObjects) {
          if (jdt != null && ReflectionUtility.isBasicClass(jdt.value())) {
            putMap.put(nvp.name, nvp.value);
          } else {
            // TODO
            // mapJsonDataToPOJO(target, pairs, webRequest, binder)
          }
        }
      }
      ReflectionUtility.setValue(fieldName, target, putMap);
      return true;
    }

    return false;
  }

  /**
   * Attempt list mapping.
   * 
   * @param fieldName
   *          the field name
   * @param target
   *          the target
   * @param nvpValue
   *          the nvp value
   * @param returnType
   *          the return type
   * @param jdt
   *          the jdt
   * @return true, if successful
   */
  @SuppressWarnings("unchecked")
  protected boolean attemptListMapping(String fieldName, Object target, Object nvpValue, Class<?> returnType, JsonProperty jdt, WebRequest webRequest,
      WebDataBinder binder) {

    // quick return if the target field "method" is not of type List<?>
    // e.g. public List<?> pojo.getNames<String>() then continue
    if (!ReflectionUtility.isCollectionClass(returnType)) {
      return false;
    }

    if (isListWithinList(nvpValue)) {
      // cast the list of list
      List<List<?>> listOfObjects = (List<List<?>>) nvpValue;

      // is array
      if (ReflectionUtility.isCollectionClass(returnType) && jdt != null) {
        List<Object> put = new ArrayList<Object>();

        // iterate each element, and add it to the list
        for (List<?> list : listOfObjects) {
          if (isNameValueList(list)) {

            // recurisvely walk each list element
            List<NameValuePair> nvl = (List<NameValuePair>) list;
            Object next = ReflectionUtility.newInstance(jdt.value());
            mapJsonDataToPOJO(next, nvl, webRequest, binder);

            // add the walked element to the arraylist
            put.add(next);
          }
        }

        // set the list to the main object
        ReflectionUtility.setValue(fieldName, target, put);
      }
      return true;
    } else if (nvpValue instanceof List<?>) {
      List<NameValuePair> listOfObjects = (List<NameValuePair>) nvpValue;
      List<Object> putList = new ArrayList<Object>();
      for (NameValuePair nvp : listOfObjects) {
        if (jdt != null && ReflectionUtility.isBasicClass(jdt.value())) {
          putList.add(nvp.value);
        } else {
          // TODO woohoo
          // mapJsonDataToPOJO(target, pairs, webRequest, binder)
        }
      }
      ReflectionUtility.setValue(fieldName, target, putList);
      return true;
    }

    return false;
  }

  /**
   * Attempt simple mapping.
   * 
   * @param fieldName
   *          the field name
   * @param target
   *          the target
   * @param nvpValue
   *          the nvp value
   * @param returnType
   *          the return type
   * @param webRequest
   *          the web request
   * @param binder
   *          the binder
   * @return true, if successful
   */
  @SuppressWarnings("unchecked")
  protected boolean attemptSimpleMapping(String fieldName, Object target, Object nvpValue, Class<?> returnType, WebRequest webRequest, WebDataBinder binder) {
    // recursively walk the next elements and set their properties
    if (nvpValue instanceof List<?>) {
      List<NameValuePair> list = (List<NameValuePair>) nvpValue;
      Object next = ReflectionUtility.newInstance(returnType);
      mapJsonDataToPOJO(next, list, webRequest, binder);
      ReflectionUtility.setValue(fieldName, target, next);
      return true;
    }

    return false;
  }

  /**
   * Sets the value. May also use the {@link ConversionService} to convert
   * types, such as {@link Date} using the {@link DateTimeFormat}. @see
   * 
   * @param method
   *          the method
   * @param fieldName
   *          the field name
   * @param target
   *          the target
   * @param value
   *          the value
   * @param binder
   *          the binder {@link ConversionService} and implementation of custom
   *          converters by implementing {@link GenericConverter}
   * @throws Exception
   */
  protected void setValue(Method method, String fieldName, Object target, Object value, WebRequest webRequest, WebDataBinder binder) {
    
    // Expose the real method, if proxied, since annotations need to be found.
    Method realMethod = method;
    if (target instanceof Targetable) {
      Targetable proxy = (Targetable) target;
      Class<?> clazz = proxy.getTarget().getClass();

      realMethod = ReflectionUtility.findMethod(clazz, realMethod.getName());
    }
    // TODO expose TrackStateUtility as part of Core??
    // Method realMethod = TrackStateUtility.exposeRealMethod(method, target);

    if (realMethod == null && method == null) {
      // if there are any binding, or formatting issues, put an error
      // in the model state.

      Object tempState = webRequest.getAttribute(ModelState.MODEL_STATE, WebRequest.SCOPE_REQUEST);
      if (tempState == null) {
        tempState = new ModelState();
      }
      ModelState modelState = (ModelState) tempState;
      modelState.add(fieldName, "Cannot bind value " + value + " to target object " + (target != null ? target.getClass() : "<null>"), new RuntimeException(
          "Field " + fieldName + " does not exist for " + target.getClass().getName()));
      webRequest.setAttribute(ModelState.MODEL_STATE, modelState, WebRequest.SCOPE_REQUEST);
    }
    // get the nullable property annotation, if any
    JsonNotNullProperty jnullpt = ReflectionUtility.findAnnotation(realMethod, JsonNotNullProperty.class);

    // get the json property, if any
    JsonProperty jpt = ReflectionUtility.findAnnotation(realMethod, JsonProperty.class);

    Class<?> returnType = method.getReturnType();
    if (ReflectionUtility.isNumberClass(returnType)) {

      try {
        Object numeric = NumberUtils.valueOf(value.toString(), returnType);
        ReflectionUtility.setValue(fieldName, target, numeric);
      } catch (NumberFormatException nfe) {
        // if there are any binding, or formatting issues, put an error
        // in the model state.

        Object tempState = webRequest.getAttribute(ModelState.MODEL_STATE, WebRequest.SCOPE_REQUEST);
        if (tempState == null) {
          tempState = new ModelState();
        }
        ModelState modelState = (ModelState) tempState;
        modelState.add(fieldName, "Cannot bind value " + value + " to target object " + (target != null ? target.getClass() : "<null>"), nfe);
        webRequest.setAttribute(ModelState.MODEL_STATE, modelState, WebRequest.SCOPE_REQUEST);
      }

    } else if (ReflectionUtility.isStringClass(returnType)) {

      // set empty values to null
      String sv = (String) value;
      sv = Comparison.isEmpty(sv) ? null : sv;

      // if the Nullable property is et with emptyNull to false
      // then actually set the value, even if its empty.
      if (jnullpt != null) {
        sv = (String) value;
      }

      // unescape the sting character.
      sv = unescape(sv);
      sv = unnull(sv);

      ReflectionUtility.setValue(fieldName, target, sv);
    } else if (!attemptListMapping(fieldName, target, value, returnType, jpt, webRequest, binder)) {

      // / attempt to map of Map<?,?>
      if (!attemptMapMapping(fieldName, target, value, returnType, jpt, webRequest, binder)) {

        // attempt to simple map the object
        if (!attemptSimpleMapping(fieldName, target, value, returnType, webRequest, binder)) {

          // Use the Spring Conversion service and try to map the
          // values
          TypeDescriptor sourceType = TypeDescriptor.forObject(value);

          // specify the method, -1 such that it uses the return value
          // type
          MethodParameter mp = new MethodParameter(realMethod, -1);

          // setup the type descriptor and pass it to the converter
          TypeDescriptor targetType = new TypeDescriptor(mp);

          // PropertyValue pv = new PropertyValue(fieldName, value);

          Object converted = null;
          PropertyEditor editor = null;
          if (binder != null) {
            editor = binder.findCustomEditor(returnType, null);
          }

          if (editor != null) {
            editor.setAsText(value.toString());
            converted = editor.getValue();
          } else if (conversionService != null) {
            // use the conversion service to translate the value
            converted = this.conversionService.convert(value, sourceType, targetType);
          }

          // set the converted value, if any
          ReflectionUtility.setValue(fieldName, target, converted);
        }
      }
    }
  }

  /**
   * Escaping new lines and quotes for json.
   * 
   * @param value
   * @return
   */
  protected String unescape(String value) {
    if (value == null) {
      return null;
    }

    // TODO should we really be stripping these out? depends on the format we
    // are trying to save the content?
    // HTML doesn't support these types anyway, for example, what is a \t even
    // represented as? spaces <div> spacing. what?
    // since Json is usually used in web applications, this probably makes some
    // sort of sense...
    value = value.replace("\\\\", "\\");
    value = value.replace("\\t", "\t");
    value = value.replace("\\n", "\n");
    value = value.replace("\\r", "\r");
    value = value.replace("\\\"", "\"");

    // String result = value.replace("\\\\", "\\").replace("\\n",
    // "\n").replace("\\r", "\r").replace("\\\"", "\"").replace("\t", "");
    return value;
  }

  /**
   * Unnull.
   * 
   * @param value
   *          the value
   * @return the string
   */
  protected String unnull(String value) {
    if (value == null) {
      return null;
    }

    if (Comparison.isEqualCaseInsentiveTrim("null", value)) {
      return null;
    }

    return value;
  }

  /**
   * Builds the map.
   * 
   * @param target
   *          the target
   * @param pairs
   *          the pairs
   */
  @SuppressWarnings("unchecked")
  private void buildMap(Object target, List<NameValuePair> pairs) {
    if (pairs != null) {
      for (NameValuePair pair : pairs) {
        String name = pair.name;
        Object value = pair.value;
        if (isNameValueList(value)) {
          HashMap<Object, Object> newMap = new HashMap<Object, Object>();
          buildMap(newMap, (List<NameValuePair>) value);
          ((HashMap) target).put(name, newMap);
        } else {
          ((HashMap) target).put(name, value);
        }
      }
    }
  }
  
  private String tempfixandlogger(String value) {
	  if (Comparison.isNotEmpty(value)) {
		  StringBuilder sb = new StringBuilder();
		  byte[] buf = value.getBytes();
		  for (int i = 0; i < buf.length; i++) {
			  if (buf[i] == '\0') {
				  logger.fatal("Found \0 in string buffer, something is not correct in the handling of the message, so far we've received on the buffer: " + sb.toString());
				  continue;
			  }
			  
			  sb.append(buf[i]);
		  }
		  return sb.toString();
	  }
	  return value;
  }
  
  /**
   * Map json data to a plain old java object, all relevant data should be
   * mapped, as long as the name matches a getter/setter. For example
   * {address:{city: "Toronto"}} should match target object target.address.city.
   * It will also map values via dot path notation, such as json
   * {"address.city": "Toronto"}
   * 
   * @param target
   *          the object
   * @param pairs
   *          the pairs
   * @param binder
   *          the binder
   */
  protected void mapJsonDataToPOJO(Object target, List<NameValuePair> pairs, WebRequest webRequest, WebDataBinder binder) {

    Class<?> clazz = target.getClass();
    // if the target parameter is of type map.
    if (ReflectionUtility.isMapClass(clazz)) {
      buildMap(target, pairs);
      return;
    }

    // otherwise not map but some sort of POJO
    if (pairs != null) {
      for (NameValuePair pair : pairs) {
        //String name = tempfixandlogger(pair.name);
    	String name = pair.name;
        Object value = pair.value;
        Method method = ReflectionUtility.findGetterMethod(clazz, name);

        if (method != null) {
          setValue(method, name, target, value, webRequest, binder);
        } else {
          if (name != null && name.indexOf(".") > 0) {
            mapByPath(value, target, name, webRequest, binder);
          } else {
            logger.error("No method found under clazz: " + clazz + " when searching for field name of " + name);
          }
        }
      }
    }
  }

  protected Object isArrayOrMap(String fieldName) {
    if (Comparison.isNotEmpty(fieldName)) {
      int start = fieldName.indexOf('[');
      int end = fieldName.indexOf(']', start);
      if (start != -1 && end > start) {
        String indexer = fieldName.substring(start + 1, end);
        if (Comparison.isNumeric(indexer)) {
          Integer i = NumberUtils.parseNumber(indexer, Integer.class);
          return i >= 0 ? i : null;
        } else {
          return indexer;
        }
      }
    }
    return null;
  }

  protected void mapByPath(Object value, Object target, String path, WebRequest webRequest, WebDataBinder binder) {
    mapByPath(value, null, target, null, path, webRequest, binder);
  }

  /**
   * Map value to target via path, usually a nested object, such as
   * target.address.city instead of using the standard JSON nesting.
   * 
   * @param value
   *          the value to set against the target object
   * @param lastIndexer
   *          the indexer, null if the value is not part of an Collection, Set
   *          or Map
   * @param target
   *          the target object that the value will be set against
   * @param property
   *          the json property of the given value
   * @param path
   *          the path, usually a single level path, but this JsonDeserializer
   *          supports "spring" type bind path variables.
   * @param webRequest
   *          the web request
   * @param binder
   *          the binder
   */
  protected void mapByPath(Object value, Object lastIndexer, Object target, JsonProperty property, String path, WebRequest webRequest, WebDataBinder binder) {

    int pos = path.indexOf('.', 1);
    Class<?> clazz = target.getClass();
    Object pushInstance = null;
    String field = path;

    if (pos > 0 && value != null) {
      field = path.substring(0, pos);
      String next = path.substring(pos + 1);

      boolean isArray = false;
      Object currentIndexer = isArrayOrMap(field);
      if (currentIndexer != null) {
        isArray = true;
        int start = field.indexOf('[');
        field = field.substring(0, start);
      }

      Method method = ReflectionUtility.findGetterMethod(clazz, field);
      if (method != null) {
        Class<?> pushType = method.getReturnType();
        pushInstance = ReflectionUtility.getValue(field, target);

        if (pushInstance == null) {
          // its an array
          // TODO .. need to create map?? based on pushType
          if (isArray) {
            pushInstance = new ArrayList<Object>();
          } else {
            pushInstance = ReflectionUtility.newInstance(pushType);
          }
        }
        // is there a json property attribute?
        JsonProperty jsonProperty = ReflectionUtility.findAnnotation(method, JsonProperty.class);

        // recursion until we set the appropriate value
        mapByPath(value, currentIndexer, pushInstance, jsonProperty, next, webRequest, binder);

        // set teh instance to the current target
        ReflectionUtility.setValue(field, target, pushInstance);
      }
    } else {
      if (ReflectionUtility.isCollectionClass(clazz)) {
        if (Comparison.isNotNull(property) && Comparison.isNotNull(property.value())) {
          Object addTarget = ReflectionUtility.newInstance(property.value());
          if (Comparison.isNotNull(addTarget)) {
            Method method = ReflectionUtility.findGetterMethod(property.value(), field);

            setValue(method, field, addTarget, value, webRequest, binder);

            ((Collection<Object>) target).add(addTarget);
          }
        } else {
          // TODO do basic mapping.. add values to array
          ((Collection<Object>) target).add(value);
        }
        // attemptListMapping(field, pushInstance, nvpValue, returnType,
        // jdt,
        // binder)
      } else if (ReflectionUtility.isMap(clazz)) {
        Map<Object, Object> map = ((Map<Object, Object>) target);

        if (map != null) {
          Object mapValue = map.get(lastIndexer);
          if (mapValue == null) {
            // TODO need to create an object, but the JsonProperty
            // must be set??
            throw new NotImplementedException("Cannot create a new instance of object type ? please define " + JsonProperty.class);
          }

          Class<?> mapClass = mapValue.getClass();
          // TODO probably should check against real class, not some
          // proxied class

          Method method = ReflectionUtility.findGetterMethod(mapClass, field);
          if (method == null) {
            throw new NullPointerException("Setter must be defined for path " + field + " on class type " + mapClass);
          }

          setValue(method, field, mapValue, value, webRequest, binder);

          // System.out.println("Last Indexer " + lastIndexer);
        }
      } else {
        Method method = ReflectionUtility.findGetterMethod(clazz, field);
        setValue(method, field, target, value, webRequest, binder);
      }
    }
  }

  /**
   * Checks if is name value list.
   * 
   * @param value
   *          the value
   * @return true, if is name value list
   */
  protected boolean isNameValueList(Object value) {
    if (value instanceof List<?>) {
      List<?> list = (List<?>) value;
      if (list.size() > 0) {
        Object v = list.get(0);
        return v instanceof NameValuePair;
      }
    }
    return false;
  }

  /**
   * Checks if is list within list.
   * 
   * @param value
   *          the value
   * @return true, if is list within list
   */
  protected boolean isListWithinList(Object value) {
    if (value instanceof List<?>) {
      List<?> list = (List<?>) value;
      if (list.size() > 0) {
        Object v = list.get(0);
        return v instanceof List<?>;
      }
    }
    return false;
  }

  /**
   * Deserialize the JSON data and return a populated POJO, however use UTF-8 as
   * the default reader encoding.
   * 
   * @param <T>
   *          the generic type
   * @param clazz
   *          the clazz
   * @param input
   *          the input
   * @return the t
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public <T> T deserialize(Class<T> clazz, InputStream input) throws IOException {
    return deserialize(clazz, input, Charset.forName("UTF-8"));
  }

  /**
   * Deserialize.
   * 
   * @param <T>
   *          the generic type
   * @param clazz
   *          the clazz
   * @param target
   *          the target
   * @param binder
   *          the binder
   * @param input
   *          the input
   * @param charset
   *          the charset
   * @return the t
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public <T> T deserialize(Class<T> clazz, T target, WebRequest webRequest, WebDataBinder binder, InputStream input, Charset charset) throws IOException {

    Reader reader = new InputStreamReader(input, charset);
    return deserialize(clazz, target, webRequest, binder, reader, null);
  }

  /**
   * Deserialize the JSON data and return a populated POJO.
   * 
   * @param <T>
   *          the generic type
   * @param clazz
   *          the clazz
   * @param input
   *          the input
   * @param charset
   *          the charset
   * @return the object
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public <T> T deserialize(Class<T> clazz, InputStream input, Charset charset) throws IOException {
    Reader reader = new InputStreamReader(input, charset);
    return deserialize(clazz, null, null, null, reader, null);
  }

  /**
   * Deserialize the JSON data and return a populated POJO.
   * 
   * @param <T>
   *          the generic type
   * @param clazz
   *          the clazz
   * @param target
   *          the target
   * @param binder
   *          the binder
   * @param reader
   *          the reader
   * @param previous
   *          the previous
   * @return the object
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  @SuppressWarnings("unchecked")
  protected <T> T deserialize(Class<T> clazz, T target, WebRequest webRequest, WebDataBinder binder, Reader reader, char[] previous) throws IOException {
    char[] buffer;

    T root = null;
    if (target == null) {
      root = ReflectionUtility.newInstance(clazz);
    } else {
      root = target;
    }

    for (;;) {
      Read read = read(reader, previous, DEFAULT_BUFFER_SIZE);
      buffer = read.buffer;
      int count = read.count;
      previous = null; // reset, so that next time we don't use the same
      // buffer
      read = null; // read variable should not be used after this point
      if (count == -1) {
        break;
      }

      for (int i = 0; i < count; i++) {
        char c = buffer[i];

        // if its a start tag, then it must be the start of a
        // new object, or start of the actual json content,
        // either way, it needs to be walked to the next element
        if (isStartTag(c)) {
          // cut the buffer and return remainder
          char[] buffernext = cutBuffer(buffer, i, count);

          // walk to the next element
          ValueBufferPair vbp = walkNames(reader, buffernext, DEFAULT_BUFFER_SIZE);
          List<NameValuePair> nvps = (List<NameValuePair>) vbp.value;
          mapJsonDataToPOJO(root, nvps, webRequest, binder);
        }
      }
    }
    return root;
  }

  /**
   * The Class NameValuePair.
   */
  protected class NameValuePair {

    /** The name. */
    String name;

    /** The value. */
    Object value;

    /** The is array. */
    Boolean isArray;
  }

  /**
   * The Class ValueBufferPair.
   */
  protected class ValueBufferPair {

    /** The value. */
    Object value;

    /**
     * The buffer. This is the remainder of the buffer, whatever that was
     * leftover, and not necessarily part of the name/value pair
     */
    char[] buffer;

    /** The is end tag. Was the ValuBufferPair returned due to a } END TAG */
    boolean isEndTag;

    /** The is array. */
    boolean isArray;

    /**
     * The adjusted buffer size. This is different to the above buffer and its
     * current size.
     */
    int adjustedBufferSize;
  }

  /**
   * Name value pair.
   * 
   * @param name
   *          the name
   * @param vbp
   *          the vbp
   * @return the name value pair
   */
  protected NameValuePair createNameValuePair(String name, ValueBufferPair vbp) {
    // create a new name value pair for the returned value of the stream
    // walk
    NameValuePair nvp = new NameValuePair();

    // strip out any quotes from the BEGINNING and its END
    nvp.name = stripQuotes(name);
    nvp.value = vbp.value;
    return nvp;
  }

  /**
   * Creates the value buffer.
   * 
   * @param value
   *          the value
   * @return the value buffer pair
   */
  protected ValueBufferPair createValueBuffer(Object value, int adjustedBufferSize) {
    return createValueBuffer(value, null, -1, -1, false, adjustedBufferSize);
  }

  /**
   * Creates the value buffer.
   * 
   * @param value
   *          the value
   * @param buffer
   *          the buffer
   * @return the value buffer pair
   */
  protected ValueBufferPair createValueBuffer(Object value, char[] buffer, int adjustedBufferSize) {
    return createValueBuffer(value, buffer, -1, -1, false, adjustedBufferSize);
  }

  /**
   * Creates the value buffer.
   * 
   * @param value
   *          the value
   * @param buffer
   *          the buffer
   * @param count
   *          the count
   * @param index
   *          the index
   * @param isEndTag
   *          the is end tag
   * @return the value buffer pair
   */
  protected ValueBufferPair createValueBuffer(Object value, char[] buffer, int count, int index, boolean isEndTag, int adjustedBufferSize) {
    ValueBufferPair vbp = new ValueBufferPair();
    vbp.value = value;
    vbp.isEndTag = isEndTag;

    // cut the buffer from this position forward
    if (buffer != null) {
      char[] buffernext = null;

      if (count == -2 && index == -2) {
        // DO NOT DO ANYTHING, SIMPLY CREATE THE ValueBuffer and return it.
        buffernext = buffer;
      } else if (count != -1 && index != -1) {
        // copy only from the index position forward
        int nlen = count - index - 1;
        if (nlen > 0) {
          buffernext = new char[nlen];
          copy(buffer, buffernext, index + 1);
        }
      } else {
        // TODO check this logic..., OK SO....
        // previously in the walk
        int size = buffer.length - 1;
        if (size > 0) {
          // copy the entire content of the buffer
          // buffernext = new char[buffer.length];
          buffernext = new char[size];
          copy(buffer, buffernext, 1); // start at next position
        } else {
          buffernext = null;
        }
      }
      vbp.buffer = buffernext;
      vbp.adjustedBufferSize = adjustedBufferSize;
    }
    return vbp;
  }

  /**
   * Strip quotes.
   * 
   * @param value
   *          the value
   * @return the string
   */
  protected String stripQuotes(String value) {
    // trim the string from any whitespaces
    value = value.trim();

    int x1 = 0;
    int x2 = value.length();
    if ('"' == value.charAt(0)) {
      x1 = 1;
    }
    if ('"' == value.charAt(x2 - 1)) {
      x2 -= 1;
    }
    value = value.substring(x1, x2);

    return value;
  }

  /**
   * Adjust buffer size, if the amount of read bytes is equal to the size of the
   * buffer, then double the buffer, otherwise shrink it.
   * 
   * @param read
   *          the read
   * @param size
   *          the size
   * @return the char[]
   */
  protected int adjustBufferSize(int read, int size) {
    // increase the buffer size * 2
    if (read == size) {
      return read * 2;
    } else if (read < size) {
      // shrink it. make sure to not go lower than 4 characters
      int split = read / 2;
      if (split < 4) {
        return 4;
      } else {
        return split;
      }
    }
    return size;
  }

  /**
   * Cut the buffer from the cut position, and return the remainder.
   * 
   * @param buffer
   *          the buffer
   * @param cut
   *          the cut
   * @return the char[], return <code>null</code> if there is nothing remaining
   */
  protected char[] cutBuffer(char[] buffer, int cut, int read) {
    int nlen = buffer.length - cut - 1;
    char[] buffernext = null;
    if (nlen > 0) {
      int actualbufferlen = read - cut - 1;
      if (actualbufferlen > 0) {
    	buffernext = new char[actualbufferlen];
      	copy(buffer, buffernext, cut + 1);
      }
    }
    return buffernext;
  }

  /**
   * Walk names.
   * 
   * @param previous
   *          the previous
   * @return the value buffer pair
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  protected ValueBufferPair walkNames(Reader reader, char[] previous, int bufferSize) throws IOException {
    char[] buffer = null;
    String name = new String();
    List<NameValuePair> nvps = new ArrayList<NameValuePair>();

    // this is populated when processig the value,
    // in some cases, the value may hit an endtag '}'
    // for example, in the case of EOF or end of a nested
    // object for a given parameter name, as such, we need
    // to identify this, and exit the recursion properly.
    boolean isEndTagFlag = false;
    // boolean inBetweenQuotes = false;

    for (;;) {
      Read read = read(reader, previous, bufferSize);
      buffer = read.buffer;
      int count = read.count;
      if (count == -1) {
        // TODO: do we need to add the last entry ??
        return createValueBuffer(nvps, bufferSize);
      }

      previous = null; // reset, so that next time we don't use the same
      // buffer
      read = null; // read variable should not be used after this point
      int cut = count;
      boolean reset = false;
      for (int i = 0; i < count; i++) {
        char c = buffer[i];

        if (isColonTag(c)) {
          cut = i;
          break;
        } else if (isEndArrayTag(c)) {
          // SO, THIS IS THE CATCH, if this method is called from the walkArray,
          // then it expects
          // an array termination character "]", meaning we cannot move the
          // array forward +1, otherwise
          // the previous call "walkArray" will think there are more elements to
          // be inserted into the array list.
          // as such, we need to return the valuebuffer but not move the array
          // forward.
          return createValueBuffer(nvps, buffer, -2, -2, false, bufferSize);
        } else if (isEndTagFlag || isEndTag(c)) {
          // make sure to return the buffer to the previous call
          return createValueBuffer(nvps, buffer, bufferSize);
        }

        if (isCommaTag(c)) {
          previous = copy(buffer, i + 1);
          name = new String();
          reset = true;
          break;
        }
      }

      if (reset) {
        // go back up and try again, usually in the case of skipping a
        // character
        // like ','
        continue;
      }

      if (cut != count) {
        name = append(name, buffer, cut, count);

        // cut the buffer if necessary
        char[] buffernext = cutBuffer(buffer, cut, count);

        // walk the stream, and return its value
        ValueBufferPair vbp = walkValue(reader, buffernext, bufferSize);
        if (vbp == null) {
          throw new NullPointerException("Value can back as null on JSON parameter name: " + name);
        }

        // add name value pair
        nvps.add(createNameValuePair(name, vbp));

        // make sure we use the buffer remaining from above call
        previous = vbp.buffer;

        // reset to new name, next token
        name = new String();

        // setup the end tag from value call
        isEndTagFlag = vbp.isEndTag;
      } else {
        name += new String(buffer, 0, count);
        bufferSize = adjustBufferSize(count, bufferSize);
      }
    }
  }

  /**
   * Walk array.
   * 
   * @param previous
   *          the previous
   * @return the object
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  protected Object walkArray(Reader reader, char[] previous, int bufferSize) throws IOException {
    char[] buffer = null;
    String value = new String();
    boolean inBetweenQuotes = false;
    List<Object> list = new ArrayList<Object>();

    for (;;) {
      Read read = read(reader, previous, bufferSize);
      buffer = read.buffer;
      int count = read.count;
      if (count == -1) {
        return createValueBuffer(value, bufferSize);
      }
      previous = null; // reset, so that next time we don't use the same
      // buffer
      read = null; // read variable should not be used after this point

      for (int i = 0; i < count; i++) {
        char c = buffer[i];

        // new element
        if (!inBetweenQuotes && isStartTag(c)) {

          // cut the buffer
          char[] buffernext = cutBuffer(buffer, i, count);

          // walk to the next element
          ValueBufferPair vbpReturn = walkNames(reader, buffernext, bufferSize);
          list.add(vbpReturn.value);

          // the buffer probably moved forward, as such,
          // we must use the one passed back from walkNames(..)
          buffernext = vbpReturn.buffer;
          ValueBufferPair vbpNew = createValueBuffer(vbpReturn.value, buffernext, bufferSize);
          vbpNew.isArray = true;
          previous = buffernext;
          break;
        }

        if (!inBetweenQuotes && isEndTag(c)) {
          // return valuebuffer(value, buffer, count, i, true);
        }

        if (!inBetweenQuotes && isEndArrayTag(c)) {
          return createValueBuffer(list, buffer, count, i, false, bufferSize);
        }

      }
    }
  }

  /**
   * Walk value.
   * 
   * @param previous
   *          the previous
   * @return the value buffer pair
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  protected ValueBufferPair walkValue(Reader reader, char[] previous, int bufferSize) throws IOException {
    char[] buffer = null;
    String value = new String();
    boolean inBetweenQuotes = false;
    boolean isArray = false;

    int pc = '\0';
    for (;;) {
      Read read = read(reader, previous, bufferSize);
      buffer = read.buffer;

      int count = read.count;
      if (count == -1) {
        return createValueBuffer(value, bufferSize);
      }
      previous = null; // reset, so that next time we don't use the same
      // buffer
      read = null; // read variable should not be used after this point

      int cut = count;
      for (int i = 0; i < count; i++) {
        char c = buffer[i];
        
        if (isQuotes(c) && pc != '\\' ) {
          inBetweenQuotes = !inBetweenQuotes;
          pc = c;
          continue;
        }

        pc = c;
        
        if (!inBetweenQuotes && isCommaTag(c)) {
          cut = i;
          break;
        }

        if (!inBetweenQuotes && isColonTag(c)) {
          // it could also be a property name (nested), not a value
        }

        if (!inBetweenQuotes && isStartArrayTag(c)) {
          isArray = true;

          // cut the buffer before walking the array
          char[] buffernext = cutBuffer(buffer, i, count);

          Object array = walkArray(reader, buffernext, bufferSize);
          return (ValueBufferPair) array;
        }

        if (!inBetweenQuotes && isStartTag(c)) {
          // cut the buffer before walking recursively the next names
          char[] buffernext = cutBuffer(buffer, i, count);

          // walk to the next element
          ValueBufferPair vbpReturn = walkNames(reader, buffernext, bufferSize);
          buffernext = vbpReturn.buffer;
          ValueBufferPair vbpNew = createValueBuffer(vbpReturn.value, buffernext, bufferSize);
          vbpNew.isArray = isArray;
          return vbpNew;
        }

        if (!inBetweenQuotes && isEndTag(c)) {
          // append to the value
          value = stripQuotes(append(value, buffer, i, count));

          return createValueBuffer(value, buffer, count, i, true, bufferSize);
        }

        if (!inBetweenQuotes && isEndArrayTag(c)) {
          return createValueBuffer(value, buffer, count, i, false, bufferSize);
        }

      }

      if (cut != count) {
        // append to the value
        value = stripQuotes(append(value, buffer, cut, count));

        // walk value
        return createValueBuffer(value, buffer, count, cut, false, bufferSize);
      } else {
        value = append(value, buffer, -1, count);
         bufferSize = adjustBufferSize(count, bufferSize);
      }
    }
  }

  /**
   * Append.
   * 
   * @param appendTo
   *          the append to
   * @param buffer
   *          the buffer
   * @return the string
   */
  // BAD IDEA AS THE BUFFER IS NOT NECESSARILY ALWAYS FILLED
  //protected String append(String appendTo, char[] buffer) {
  //  return append(appendTo, buffer, -1);
  //}

  /**
   * Append.
   * 
   * @param appendTo
   *          the append to
   * @param buffer
   *          the buffer
   * @param cut
   *          the cut
   * @return the string
   */
  protected String append(String appendTo, char[] buffer, int cut, int read) {
    if (cut == -1) {
      appendTo += new String(buffer, 0, read);
    } else {
      char[] temp = new char[cut];
      copy(buffer, temp);
      appendTo += new String(temp);
    }
    return appendTo;
  }

  /**
   * Sets the conversion service.
   * 
   * @param conversionService
   *          the new conversion service
   */
  @Resource
  public void setConversionService(ConversionService conversionService) {
    this.conversionService = conversionService;
  }
}
