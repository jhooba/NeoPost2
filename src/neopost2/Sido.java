package neopost2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by jhooba on 2016-09-18.
 */
public class Sido implements INode, Comparable<Sido> {

  private static final String POST_STR = "sidoCode=%d";

  private static class NameCodeJson {
    private String NAME;
    private String CODE;

    public String getNAME() {
      return NAME;
    }

    public String getCODE() {
      return CODE;
    }
  }

  private static class NameCodeListJson {
    private ArrayList<NameCodeJson> jsonList;

    public ArrayList<NameCodeJson> getJsonList() {
      return jsonList;
    }
  }

  private final String name;
  private final int code;
  private ArrayList<Gugun> guguns;

  public Sido(String name, int code) {
    this.name = name;
    this.code = code;
  }

  @Override
  public void populate() {
    BufferedReader reader = null;
    guguns = new ArrayList<>();
    try {
      HttpURLConnection con = (HttpURLConnection) new URL(SRH_PATH + GUGUN_DO).openConnection();
      con.setRequestMethod("POST");
      con.setDoOutput(true);
      con.setDoInput(true);
      con.setUseCaches(false);
      con.setDefaultUseCaches(false);
      OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
      out.write(String.format(POST_STR, code));
      out.close();
      reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

      Gson gson = new GsonBuilder().create();
      NameCodeListJson list = gson.fromJson(reader, NameCodeListJson.class);
      guguns.ensureCapacity(list.getJsonList().size());
      for (NameCodeJson j : list.getJsonList()) {
        guguns.add(new Gugun(this, j.getNAME(), Integer.parseInt(j.getCODE())));
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
        }
      }
    }
  }

  public String getName() {
    return name;
  }

  public ArrayList<Gugun> getGuguns() {
    return guguns;
  }

  @Override
  public int compareTo(Sido s) {
    return code - s.code;
  }
}
