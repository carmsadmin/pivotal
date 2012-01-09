package com.p5solutions.core.jpa.orm;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import junit.framework.TestCase;

import org.junit.Test;

import com.p5solutions.core.jpa.orm.lob.BlobStream;

public class BlobStreamTest extends TestCase {

    protected File createFile() throws Exception {
      File file = File.createTempFile("test_", "");
      FileOutputStream output = new FileOutputStream(file);
      
      for (int i = 0; i < 256; i++) {
        output.write(i);
      }
      output.close();

      return file;
    }
    
    public void testBlobStreamFile() throws Exception {
      FileInputStream input = new FileInputStream(createFile());
      System.out.println("------ START TEST OF setBinaryStream and getBinaryStream on BlobStream");

      BlobStream blob = new BlobStream(input);
      
      InputStream input2 = blob.getBinaryStream();
      long length = blob.length();
      System.out.println("Length of Input: " + length);
      assertEquals(length, 256);
      outputInputStream(input2);
      input2.close();
    }
    
    public void testBlobStreamByteArrayInputStream() throws Exception {
      System.out.println("------ START TEST OF ByteArrayInputStream into BlobStream");
      byte[] buf = new byte[256];
      for (int i = 0; i < 256; i++) {
        buf[i] = (byte)i;
      }

      ByteArrayInputStream bais = new ByteArrayInputStream(buf);
      BlobStream blob = new BlobStream(bais);
      InputStream input = blob.getBinaryStream();
      if (!(input instanceof FileInputStream)) {
        fail("must be of type input stream, instead received type " + input);
      }
      
      assertEquals(256, blob.length());

      System.out.println("Length of Input: " + blob.length());
      outputInputStream(input);
    }
    
    @Test
    public void testBlobStreamGetAndSetBinaryStream() throws Exception {
      System.out.println("------ START TEST OF setBinaryStream and getBinaryStream on BlobStream");

      BlobStream blob = new BlobStream();
      OutputStream output = blob.setBinaryStream();
      for (int i = 0; i < 256; i++) {
        output.write(i);
      }
      output.close();
      
      InputStream input = blob.getBinaryStream();
      long length = blob.length();
      System.out.println("Length of Input: " + length);
      assertEquals(length, 256);
      outputInputStream(input);
      input.close();
    }
    
    protected void outputInputStream(InputStream input) throws IOException {
      int b = -1;
      while ((b = input.read()) != -1) {
        if (b > 0 && (b % 16) == 0) {
          System.out.println();
        }
        if (b < 10) {
          System.out.print("00");
        } else if (b < 100) {
          System.out.print("0");
        }
        
        System.out.print(b);
        System.out.print(':');
      }
      System.out.println();
    }
    
    @Test
    public void testFiles() throws Exception {
      System.out.println("------ START TEST OF FILE INPUT OUTPUT");
      try {
        File file = createFile();
        FileInputStream input = new FileInputStream(file);
        outputInputStream(input);
        input.close();
        file.delete();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      
    }

}
