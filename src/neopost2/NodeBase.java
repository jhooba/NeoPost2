package neopost2;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Created by jhooba on 2016-10-11.
 */
abstract public class NodeBase implements INode {

  protected final ZipFile zip;
  protected static ZipOutputStream zout = null;

  protected NodeBase(ZipFile zip) {
    this.zip = zip;
  }

  protected void populateSub(Object... args) {
    BufferedReader reader = null;
    try {
      reader = openReader(args);
      if (reader == null) {
        return;
      }
      parseContent(reader, args);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException ignored) {
        }
      }
    }
  }

  @Nullable
  private BufferedReader openReader(Object... args) throws IOException {
    if (zip != null) {
      ZipEntry ze = zip.getEntry(getStoredFileName(args));
      if (ze == null) {
        return null;
      }
      return new BufferedReader(new InputStreamReader(zip.getInputStream(ze)));
    }
    BufferedReader reader = new BufferedReader(new InputStreamReader(openConnectedInputStream(args)));
    StringBuilder buffer = new StringBuilder();
    char [] cb = new char [1024];
    int len;
    while ((len = reader.read(cb, 0, cb.length)) != -1) {
      buffer.append(cb, 0, len);
    }
    String str = buffer.toString();
    synchronized (NodeBase.class) {
      if (zout == null) {
        File f = new File(Country.getStoredDir(), "NeoPost2.zip");
        f.createNewFile();
        zout = new ZipOutputStream(new FileOutputStream((f)));
      }
      zout.putNextEntry(new ZipEntry(getStoredFileName(args)));
      OutputStreamWriter w = new OutputStreamWriter(zout);
      w.write(str);
      w.flush();
      zout.closeEntry();
    }
    return new BufferedReader(new StringReader(str));
  }

  protected abstract InputStream openConnectedInputStream(Object... args) throws IOException;
  protected abstract void parseContent(BufferedReader reader, Object... args) throws IOException;
  protected abstract String getStoredFileName(Object... args);

  public static void closeZipWrite() {
    if (zout == null) {
      return;
    }
    try {
      zout.close();
    } catch (IOException ignored) {
    }
  }
}
