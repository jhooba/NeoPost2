package neopost2;

import java.io.*;

/**
 * Created by jhooba on 2016-10-11.
 */
abstract public class NodeBase implements INode {

  protected void populateSub(Object... args) {
    BufferedReader reader = null;
    if (useStored()) {
      if (!getStoredFile(args).isFile()) {
        return;
      }
    }
    try {
      reader = openReader(args);
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

  private BufferedReader openReader(Object... args) throws IOException {
    if (useStored()) {
      return new BufferedReader(new InputStreamReader(new FileInputStream(getStoredFile(args))));
    }
    BufferedReader reader = new BufferedReader(new InputStreamReader(openConnectedInputStream(args)));
    StringBuilder buffer = new StringBuilder();
    char [] cb = new char [1024];
    int len;
    while ((len = reader.read(cb, 0, cb.length)) != -1) {
      buffer.append(cb, 0, len);
    }
    String str = buffer.toString();
    BufferedWriter w = null;
    try {
      w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getStoredFile(args))));
      w.write(str);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (w != null) {
        try {
          w.close();
        } catch (IOException ignored) {
        }
      }
    }
    return new BufferedReader(new StringReader(str));
  }

  private File getStoredFile(Object[] args) {
    return new File(Country.getStoredDir(), getStoredFileName(args));
  }

  protected abstract InputStream openConnectedInputStream(Object[] args) throws IOException;
  protected abstract void parseContent(BufferedReader reader, Object[] args) throws IOException;
  protected abstract String getStoredFileName(Object[] args);
}
