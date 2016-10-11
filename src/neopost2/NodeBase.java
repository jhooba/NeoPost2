package neopost2;

import java.io.*;

/**
 * Created by jhooba on 2016-10-11.
 */
abstract public class NodeBase implements INode {

  protected void populateSub(Object... args) {
    BufferedReader reader = null;
    StringBuilder buffer = null;
    if (useStored()) {
      if (!getStoredFile(args).isFile()) {
        return;
      }
    } else {
      buffer = new StringBuilder();
    }
    try {
      reader = openReader(args);
      if (!useStored()) {
        reader.mark(1024 * 64);
      }
      parseContent(reader, args);
      if (!useStored()) {
        reader.reset();
        char [] cb = new char [1024];
        int len;
        while ((len = reader.read(cb, 0, cb.length)) != -1) {
          buffer.append(cb, 0, len);
        }
      }
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
    if (buffer != null) {
      BufferedWriter w = null;
      try {
        w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getStoredFile(args))));
        w.write(buffer.toString());
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          w.close();
        } catch (IOException ignored) {
        }
      }
    }
  }

  private BufferedReader openReader(Object... args) throws IOException {
    InputStream in;
    if (useStored()) {
      in = new FileInputStream(getStoredFile(args));
    } else {
      in = openConnectedInputStream(args);
    }
    return new BufferedReader(new InputStreamReader(in));
  }

  private File getStoredFile(Object[] args) {
    return new File(Country.getStoredDir(), getStoredFileName(args));
  }

  protected abstract InputStream openConnectedInputStream(Object[] args) throws IOException;
  protected abstract void parseContent(BufferedReader reader, Object[] args) throws IOException;
  protected abstract String getStoredFileName(Object[] args);
}
