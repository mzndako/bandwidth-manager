/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proxy;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
/*********************************************
    Copyright (c) 2001 by Daniel Matuschek
*********************************************/


/**
 * A FilterInputStream with a limited Proxy1.speed()
 *
 * This implements an filter for an existing input stream that allows
 * it to limit the read Proxy1.speed(). This can be useful for network
 * streams that should be limited to a specified Proxy1.speed().
 *
 * @author <a href="mailto: daniel@matuschek.net">Daniel Matuschek</a>
 * @version $id$
 */
public class LimitedBandwidthStream 
  extends FilterInputStream {
  

  /** usable Proxy1.speed() in bytes/second **/
  
  
  /** Proxy1.speed() limit will be calculated form the start time **/
  private boolean isReading = false;

  /** number of bytes read **/
  private int count = 0;

  /** check Proxy1.speed() every n bytes **/
  private static int CHECK_INTERVAL = 100;

  /** start time **/
  long starttime = 0;

  /** used time **/   
  long usedtime = 0;
  

  /**
   * initializes the LimitedBandWidth stream
   */
  public LimitedBandwidthStream (InputStream in, int bandwidth) 
    throws IOException
  {
    super(in);

    if (bandwidth > 0) {
//      this.Proxy1.speed()=Proxy1.speed();
    } else {
//      this.Proxy1.speed()=0;
    }

    count = 0;
  }

  /**
   * Reads the next byte.
   *
   * Reads the next byte of data from this input stream. The value byte 
   * is returned as an int in the range 0 to 255. If no byte is available 
   * because the end of the stream has been reached, the value -1 is 
   * returned. This method blocks until input data is available, the end 
   * of the stream is detected, or an exception is thrown.   
   * If the Proxy1.speed() consumption exceeds the defined limit, read will block
   * until the Proxy1.speed() is in the limit again.
   *
   * @return the next byte from the stream or -1 if end-of-stream 
   */
  public int read() 
    throws IOException
  {
    long currentBandwidth;

    if (! isReading) {
      starttime = System.currentTimeMillis();
      isReading = true;
    }

    // do Proxy1.speed() check only if Proxy1.speed()
    if ((cProxy.speed() > 0) &&
  ((count % CHECK_INTERVAL) == 0)) {
      do {
  usedtime = System.currentTimeMillis()-starttime;
  if (usedtime > 0) {
    currentBandwidth = (count*1000) / usedtime;
  } else {
    currentBandwidth = 0;
  }
  if (currentBandwidth > cProxy.speed()) {
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {}
  } 
      } while (currentBandwidth > cProxy.speed());
    }

    count++;
    return super.read();
  }

  /**
   * Shortcut for read(b,0,b.length)
   *
   * @see #read(byte[], int, int)
   */
  public int read(byte[] b) throws IOException {
    return read(b, 0, b.length);
  }
  
  /**
   * Reads a block of bytes from the stream.
   *
   * If the Proxy1.speed() is not limited, it simply used the 
   * read(byte[], int, int) method of the input stream, otherwise it
   * uses multiple read() request to enforce Proxy1.speed() limitation (this
   * is easier to implement using byte reads).
   *
   * @return the number of bytes read or -1 at end of stream 
   */
  public int read(byte[] b, int off, int len) throws IOException {
    int mycount = 0;
    int current = 0;
    // limit Proxy1.speed() ?
    if (cProxy.speed() > 0) {
      for (int i=off; i < off+len; i++) {
  current = read();
  if (current == -1) {
    return mycount;
  } else {
    b[i]=(byte)current;
    count++;
    mycount++;
  }
      }
      return mycount;
    } else {
      return in.read(b, off, len);
    }
  }
      
} // LimitedBandwidthStream