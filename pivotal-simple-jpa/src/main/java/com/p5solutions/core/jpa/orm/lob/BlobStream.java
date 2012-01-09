package com.p5solutions.core.jpa.orm.lob;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.sql.Blob;
import java.sql.SQLException;

import org.apache.commons.lang.NotImplementedException;

// TODO NOT COMPLETED, BUT IT WORKS FOR INPUTSTREAMS GOING INTO THE DATABASE.

public class BlobStream implements Blob {
  private InputStream input;

  private File file;
  private OutputStream output;
  private long _length = 0;

  public BlobStream() {
    // usually used when requiring an output stream via the setBinaryStream()
  }

  public BlobStream(FileInputStream input) {
    this.input = input;
    try {
      long skip = input.skip(Long.MAX_VALUE);
      this._length = skip;
      input.reset();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    
  }
  
  public BlobStream(InputStream input) {
    this.input = input;
    if (input instanceof ByteArrayInputStream) {
      convertByteArrayInputStream();
    }
  }

  protected void convertByteArrayInputStream() {
    try {
      ByteArrayInputStream bais = (ByteArrayInputStream) input;
      File file = newTemporaryFile();
      OutputStream output = newStagingOutputStream(file);
      byte[] chunks = new byte[256];

      int read = 0;
      int count = 0;
      while ((read = bais.read(chunks)) > 0) {
        output.write(chunks, 0, read);
        count += read;
      }
      output.close();

      this.input = new FileInputStream(file);
      this.file = file;
      this._length = count;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public BlobStream(String content) {
    this(content, Charset.forName("UTF-8"));
  }

  public BlobStream(String content, Charset charset) {
    this(content.getBytes(charset));
  }

  public BlobStream(byte[] buffer) {
    try {
      File file = newTemporaryFile();
      OutputStream output = newStagingOutputStream(file);
      output.write(buffer);
      output.close();

      this.file = file;
      this.input = new FileInputStream(file);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    resetInputStream();
  }

  protected void resetInputStream() {
    if (input != null) {
      try {
        input.close();
      } catch (IOException e) {
        // close your eyes
      }
      input = null;
    }
  }

  protected void resetOuputStream() {
    if (output != null) {
      try {
        output.close();
      } catch (IOException e) {
        // close your eyes
      }
      output = null;
    }
  }

  public BlobStream(File file) {
    try {
      _length = file.length();
      this.input = new FileInputStream(file);
    } catch (FileNotFoundException e) {
      throw new RuntimeException("File not found " + file.toString());
    }

  }

  //
  // public BlobStream(OutputStream output) {
  // this.output = output;
  // }

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
    if (output == null) {
      return this.input;
    }

    if (output instanceof ByteArrayOutputStream) {
      try {
        File file = newTemporaryFile();
        OutputStream os = newStagingOutputStream(file);
        byte[] buffer = ((ByteArrayOutputStream) this.output).toByteArray();
        os.write(buffer);
        os.close();

        // replace the ByteArrayOutputStream to a FileOutputStream
        this.output = os;
        this.file = file;
        this._length = buffer.length;
      } catch (IOException e) {
        // TODO log??
        throw new RuntimeException(e);
      }
    }

    if (file != null) {
      try {
        FileInputStream input = new FileInputStream(file);
        this.input = input;
        this._length = file.length();
        return this.input;
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    return null;
  }

  @Override
  public InputStream getBinaryStream(long pos, long length) throws SQLException {
    throwNotImplemented();
    return null;
  }

  @Override
  public byte[] getBytes(long pos, int length) throws SQLException {
    byte[] buf = new byte[length];
    InputStream input = getBinaryStream();
    try {
      input.read(buf, (int) pos, length);
    } catch (IOException e) {
      e.printStackTrace();
      buf = null;
    }
    return buf;
  }

  @Override
  public long length() throws SQLException {
    return _length;
  }

  @Override
  public long position(byte[] pattern, long start) throws SQLException {
    throwNotImplemented();
    return 0;
  }

  @Override
  public long position(Blob pattern, long start) throws SQLException {
    throwNotImplemented();
    return 0;
  }

  @Override
  public OutputStream setBinaryStream(long pos) throws SQLException {
    if (pos > 0) {
      throw new RuntimeException("position must be zero, since its returning an instance of FileOutputStream, as a temporary staging area");
    }
    return setBinaryStream();
  }

  protected File newTemporaryFile() throws IOException {
    File file = File.createTempFile("blobstream_", ".tmp");
    return file;
  }

  protected OutputStream newStagingOutputStream(File file) throws IOException {
    return new FileOutputStream(file);
  }

  public OutputStream setBinaryStream() {
    try {
      this.file = newTemporaryFile();
      this.output = newStagingOutputStream(this.file);
      return this.output;
    } catch (Exception e) {
      // TODO log it.
      throw new RuntimeException(e);
    }
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

  @Override
  protected void finalize() throws Throwable {
    try {
      if (output instanceof FileOutputStream) {
        ((FileOutputStream) output).close();
      }

      if (file != null) {
        file.delete();
      }

      file = null;
      output = null;
      input = null;

    } catch (Exception e) {
      // turn the other way.
    } finally {
      super.finalize();
    }
  }
}