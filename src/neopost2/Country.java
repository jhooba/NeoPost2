package neopost2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by jhooba on 2016-09-18.
 */
public class Country implements INode {
  private static final String POST_STR = "menuGubun=A&srhType=LOC&houseType=1&gubunCode=LAND";

  private String[] targetFilters;
  private ArrayList<Sido> sidos;

  public void setTargetFilters(String[] targetFilters) {
    this.targetFilters = targetFilters;
  }

  public void populate() {
    BufferedReader reader = null;
    sidos = new ArrayList<>(2);
    try {
      HttpURLConnection con = (HttpURLConnection) new URL(SRH_PATH + SRH_DO).openConnection();
      con.setRequestMethod("POST");
      con.setDoOutput(true);
      con.setDoInput(true);
      con.setUseCaches(false);
      con.setDefaultUseCaches(false);
      OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
      out.write(POST_STR);
      out.close();
      reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

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
                sidos.add(new Sido(name, code));
              }
            } catch (NumberFormatException e) {
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
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        reader.close();
      } catch (IOException e) {
      }
    }
  }
  public ArrayList<Sido> getSidos() {
    return sidos;
  }
}
