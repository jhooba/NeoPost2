package neopost2;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by jhooba on 2016-09-18.
 */
public class Country extends NodeBase {
  private static final String POST_STR = "menuGubun=A&srhType=LOC&houseType=1&gubunCode=LAND";
  private static File storedDir;

  private String[] targetFilters;
  private ArrayList<Sido> sidos;
  private boolean useStored;

  public Country(boolean useStored) {
    this.useStored = useStored;
  }

  public void setTargetFilters(String[] targetFilters) {
    this.targetFilters = targetFilters;
  }

  public void populate() {
    sidos = new ArrayList<>(2);
    populateSub(new Object[0]);
  }

  @Override
  public boolean useStored() {
    return useStored;
  }

  public ArrayList<Sido> getSidos() {
    return sidos;
  }

  public static File getStoredDir() {
    if (storedDir == null) {
      String t = System.getProperty("java.io.tmpdir");
      storedDir = new File(t + File.separator + "NeoPost2");
      if (!storedDir.isDirectory()) {
        storedDir.mkdir();
      }
    }
    return storedDir;
  }

  @Override
  protected InputStream openConnectedInputStream(Object[] args) throws IOException {
    HttpURLConnection con = (HttpURLConnection) new URL(SRH_PATH + SRH_DO).openConnection();
    con.setRequestMethod("POST");
    con.setDoOutput(true);
    con.setDoInput(true);
    con.setUseCaches(false);
    con.setDefaultUseCaches(false);
    OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
    out.write(POST_STR);
    out.close();
    return con.getInputStream();
  }

  @Override
  protected void parseContent(BufferedReader reader, Object[] args) throws IOException {
    String line;
    boolean in = false;
    while ((line = reader.readLine()) != null) {
      line = line.trim();
      if (line.length() == 0) {
        continue;
      }
      if (in) {
        if (line.startsWith("<option value=\"")) {
          try {
            int code = Integer.parseInt(line.substring(15, 17));
            String name = line.substring(line.indexOf('>') + 1, line.length() - 9);
            if (Arrays.stream(targetFilters).anyMatch(f -> f.equals(name))) {
              sidos.add(new Sido(this, name, code));
            }
          } catch (NumberFormatException ignored) {
          }
        }
        if (line.startsWith("</select>")) {
          in = false;
        }
      } else {
        if (line.startsWith("<select name=\"sidoCode\"")) {
          in = true;
        }
      }
    }
  }

  @Override
  protected String getStoredFileName(Object[] args) {
    return "ct";
  }
}
