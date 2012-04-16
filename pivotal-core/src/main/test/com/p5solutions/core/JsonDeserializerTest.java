package com.p5solutions.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.FormattingConversionService;

import com.p5solutions.core.json.JsonDeserializer;
import com.p5solutions.core.json.JsonSerializer;

public class JsonDeserializerTest extends TestCase {
  private ConversionService conversionService;

  public static String jsonDATA = "{" + "     \"firstName\": \"John\", " + "     \"lastName\": \"Smith\", "
      + "     \"age\": 25," + "     \"address\": {" + "         \"streetAddress\": \"21 2nd Street\","
      + "         \"city\": \"New York\"," + "         \"state\": \"NY\"," + "         \"postalCode\": \"10021\""
      + "     }," + "     \"phoneNumber\": [" + "         { \"type\": \"home\", \"number\": \"212 555-1234\" } ,"
      + "         { \"type\": \"fax\", \"number\": \"646 555-4567\" }" + "     ] " + " }";

  public static String jsonDataArray = "{" + "\"languages[0].languageType\":\"1\","
      + "\"languages[1].languageType\":\"1\"," + "\"languages[2].languageType\":\"1\","
      + "\"languages[3].languageType\":\"0\"," + "\"languages[4].languageType\":\"1\","
      + "\"languages[5].languageType\":\"0\"," + "\"languages[6].otherLanguages\":\"\","
      + "\"activities.memberships\":\"asdf\"," + "\"activities.medPositions\":\"asdf\","
      + "\"activities.accomplishments\":\"asdf\"," + "\"activities.awards\":\"asdfas\","
      + "\"activities.languageType\":\"0\",";

  @Before
  public void setup() {
    FormattingConversionService conversiontService = new FormattingConversionService();
    setConversionService(conversiontService);
  }

  @Test
  public void testCopyShift() {
    char[] a = new char[] { '1', '2', '3', '4' };
    char[] r = JsonDeserializer.copy(a, 3);

    assertEquals(r[0], '4');

    System.out.println(r);
  }

  @Test
  @Ignore
  public void testJsonSerializer() {
    JsonTestPerson person = new JsonTestPerson();
    person.setAge(25);
    person.setFirstName("Joe");
    person.setLastName("Henry");

    JsonTestPersonAddress address = new JsonTestPersonAddress();
    address.setCity("Ottawa");
    address.setPostalCode("K1B2K6");
    address.setState("Ontario");
    address.setStreetAddress("152 Kent St");
    person.setAddress(address);

    person.setPhoneNumber(new ArrayList<JsonTestPhoneNumber>());

    for (int i = 0; i < 3; i++) {
      JsonTestPhoneNumber phoneNumber = new JsonTestPhoneNumber();
      switch (i) {
        case 0:
          phoneNumber.setNumber("613-555-2231");
          phoneNumber.setType("Home");
          break;
        case 1:
          phoneNumber.setNumber("613-421-3222");
          phoneNumber.setType("Mobile");
          break;
        case 2:
          phoneNumber.setNumber("416-221-2233");
          phoneNumber.setType("Work");
          break;
      }
      person.getPhoneNumber().add(phoneNumber);
    }

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    JsonSerializer serializer = new JsonSerializer();
    try {
      serializer.serialize(person, output);
      String out = output.toString();
      System.out.println(out);

      ByteArrayInputStream input = new ByteArrayInputStream(out.getBytes());

      JsonDeserializer deser = new JsonDeserializer();
      JsonTestPerson person2 = deser.deserialize(JsonTestPerson.class, input, Charset.forName("UTF-8"));

      if (!(person2 instanceof JsonTestPerson)) {
        fail("invalid deserialization");
      }

      assertEquals(person.getFirstName(), person2.getFirstName());
      assertEquals(person.getLastName(), person2.getLastName());
      assertEquals(person.getAge(), person2.getAge());

      assertEquals(person.getAddress().getCity(), person2.getAddress().getCity());
      assertEquals(person.getAddress().getPostalCode(), person2.getAddress().getPostalCode());
      assertEquals(person.getAddress().getState(), person2.getAddress().getState());
      assertEquals(person.getAddress().getStreetAddress(), person2.getAddress().getStreetAddress());

      for (int i = 0; i < person.getPhoneNumber().size(); i++) {
        JsonTestPhoneNumber number1 = person.getPhoneNumber().get(i);
        JsonTestPhoneNumber number2 = person2.getPhoneNumber().get(i);
        assertEquals(number1.getNumber(), number2.getNumber());
        assertEquals(number1.getType(), number2.getType());
      }

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  @Test
  public void testDeserializerHTML() throws IOException {
    JsonDeserializer deser = new JsonDeserializer();

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    JsonSerializer serializer = new JsonSerializer();
    try {
      JsonTestHTML jsonTestHTML = new JsonTestHTML();
      jsonTestHTML.setHtml("<table border=\"1\"></table>");
      serializer.serialize(jsonTestHTML, output);
      String out = output.toString();
      System.out.println(out);

      ByteArrayInputStream input = new ByteArrayInputStream(out.getBytes());
      JsonTestHTML jsonTestHTML2 = deser.deserialize(JsonTestHTML.class, input, Charset.forName("UTF-8"));

      if (!(jsonTestHTML2 instanceof JsonTestHTML)) {
        fail("invalid deserialization");

      }

      assertEquals(jsonTestHTML.getHtml(), jsonTestHTML2.getHtml());
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Test
  public void testJsonArrayDeserializer() {
    try {
      InputStream input = new ByteArrayInputStream(jsonDataArray.getBytes("UTF-8"));
      JsonDeserializer deser = new JsonDeserializer();
      deser.setConversionService(this.conversionService);

      JsonTestArray array = deser.deserialize(JsonTestArray.class, input, Charset.forName("UTF-8"));

      return;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void testJsonDeserializer() {
    try {

      InputStream input = new ByteArrayInputStream(jsonDATA.getBytes("UTF-8"));
      JsonDeserializer deser = new JsonDeserializer();
      JsonTestPerson person = deser.deserialize(JsonTestPerson.class, input, Charset.forName("UTF-8"));

      assertEquals(person.getFirstName(), "John");
      assertEquals(person.getLastName(), "Smith");
      assertEquals(person.getAge(), new Integer(25));

      assertEquals(person.getAddress().getCity(), "New York");
      assertEquals(person.getAddress().getPostalCode(), "10021");
      assertEquals(person.getAddress().getState(), "NY");
      assertEquals(person.getAddress().getStreetAddress(), "21 2nd Street");

      JsonTestPhoneNumber number1 = person.getPhoneNumber().get(0);
      JsonTestPhoneNumber number2 = person.getPhoneNumber().get(1);
      assertEquals(number1.getNumber(), "212 555-1234");
      assertEquals(number1.getType(), "home");
      assertEquals(number2.getNumber(), "646 555-4567");
      assertEquals(number2.getType(), "fax");

    } catch (Exception e) {
      fail(e.toString());
    }
  }

  @Test
  public void testMe() {
    InputStream in = new ByteArrayInputStream(new byte[] { (byte) 0xD0, (byte) 0x96 });
    InputStreamReader reader = new InputStreamReader(in, Charset.forName("UTF-8"));
    try {
      char[] ch = new char[4];
      int ii = reader.read(ch);
      System.out.println(ch);
      ByteArrayOutputStream output = new ByteArrayOutputStream();

      return;
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Test
  public void TTTestSimpleJSONDeserialization() {
    int[] buf1 = new int[] { 0xD0, 0x96 };
    /*
     * int a = 0xD096; if (a < 128) { // ASCII char char1 = (char)a; System.out.println("ascii: " + char1); }
     */

    int twobyte = 0x800;
    int len = buf1.length;
    for (int i = 0; i < len; i++) {
      int c = buf1[i];

      if (c < 128) {
        // ASCII
        char char1 = (char) c;
        System.out.println("ascii: " + char1);
      } else if (c < twobyte) {
        // two byte
        int shift = c ^ 0x06;
        // shift <<= 3; // shift it back
        System.out.println("shift1: " + shift);
        int shift2 = 0x00;
        if (i + 1 < len) {
          int c2 = buf1[i + 1];
          shift2 = c2 << 2;
        }

        int shift3 = shift & shift2;
        System.out.println("Shift: " + shift3);
      } else {
        // three byte
      }
    }
    /*
     * if (a > 127 && a < 65536) { // shift two byte by 13 get the 3 first bits = 6 // UTF-8 two byte int s1 = a >> 13;
     * if (s1 == 6) {
     * 
     * } System.out.println(s1); }
     * 
     * if (a > 65535) {
     * 
     * }
     * 
     * for (int i = 0; i < buf2.length; i++) { int v = buf2[i] & 0xFF;
     * 
     * System.out.println(v); }
     */
    return;
  }

  public void setConversionService(ConversionService conversionService) {
    this.conversionService = conversionService;
  }
}
