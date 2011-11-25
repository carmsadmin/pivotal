package com.p5solutions.core.jpa.orm.lob;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;

import org.apache.commons.lang.NotImplementedException;

// TODO NOT COMPLETED, BUT IT WORKS FOR INPUTSTREAMS GOING INTO THE DATABASE.

public class BlobStream implements Blob {
  private InputStream input;
//  private OutputStream output;

  public BlobStream(InputStream input) {
    this.input = input;
  }
//
//  public BlobStream(OutputStream output) {
//    this.output = output;
//  }

  protected void throwNotImplemented() {
    throw new NotImplementedException(
        "Cannot get input stream within a pos <> length on a temporary "
            + BlobStream.class
            + " this class is designed as an interm data storage to and from a database, it should be replaced by a blob handler as per database implementation.");
  }

  @Override
  public void free() throws SQLException {
    // TODO Auto-generated method stub

  }

  @Override
  public InputStream getBinaryStream() throws SQLException {
    return this.input;
  }

  @Override
  public InputStream getBinaryStream(long pos, long length) throws SQLException {
    throwNotImplemented();
    return null;
  }

  @Override
  public byte[] getBytes(long pos, int length) throws SQLException {
    byte[] buf = new byte[length];
    try {
      input.read(buf, (int)pos, length);
    } catch (IOException e) {
      e.printStackTrace();
      buf = null;
    }
    return buf;
  }

  @Override
  public long length() throws SQLException {
    throwNotImplemented();
    return 0;
  }

  @Override
  public long position(byte[] pattern, long start) throws SQLException {
    throwNotImplemented();
    return 0;  }

  @Override
  public long position(Blob pattern, long start) throws SQLException {
    throwNotImplemented();
    return 0;
  }

  @Override
  public OutputStream setBinaryStream(long pos) throws SQLException {
    throwNotImplemented();
    return null;
  }

  @Override
  public int setBytes(long pos, byte[] bytes) throws SQLException {
    throwNotImplemented();
    return 0;
  }

  @Override
  public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException {
    throwNotImplemented();
    return 0;
  }

  @Override
  public void truncate(long len) throws SQLException {
    throwNotImplemented();
  }

}
