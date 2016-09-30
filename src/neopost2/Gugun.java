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
public class Gugun implements INode, Comparable<Gugun> {
  private static final String POST_STR = "gugunCode=%d";

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

  private final Sido sido;
  private final String name;
  private final int code;
  private ArrayList<Dong> dongs;

  public Gugun(Sido sido, String name, int code) {
    this.sido = sido;
    this.name = name;
    this.code = code;
  }

  @Override
  public void populate() {
    BufferedReader reader = null;
    dongs = new ArrayList<>();
    try {
      HttpURLConnection con = (HttpURLConnection) new URL(SRH_PATH + DONG_DO).openConnection();
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
      dongs.ensureCapacity(list.getJsonList().size());
      for (NameCodeJson j : list.getJsonList()) {
        dongs.add(new Dong(this, j.getNAME(), Long.parseLong(j.getCODE())));
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

  public int getCode() {
    return code;
  }

  public Sido getSido() {
    return sido;
  }

  public String getName() {
    return name;
  }

  public ArrayList<Dong> getDongs() {
    return dongs;
  }

  @Override
  public int compareTo(Gugun g) {
    int delta = sido.compareTo(g.sido);
    if (delta == 0) {
      return name.compareTo(g.name);
    }
    return delta;
  }
}