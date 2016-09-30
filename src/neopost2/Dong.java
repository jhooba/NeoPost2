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
import java.util.Calendar;
import java.util.Collection;
import java.util.TreeMap;

/**
 * Created by jhooba on 2016-09-18.
 */
public class Dong implements INode, Comparable<Dong> {
  private static final String POST_STR = "menuGubun=A&houseType=1&srhYear=%d&srhPeriod=%d&gubunCode=LAND&gugunCode=%d&dongCode=%d";
  private static final int[] PERIODS = new int[] {1, 2, 3, 4};

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

  private final Gugun gugun;
  private final String name;
  private final long dongCode;
  private TreeMap<String, Danji> danjiMap;

  public Dong(Gugun gugun, String name, long dongCode) {
    this.gugun = gugun;
    this.name = name;
    this.dongCode = dongCode;
  }

  @Override
  public void populate() {
    BufferedReader reader = null;
    danjiMap = new TreeMap<>();
    int thisYear = Calendar.getInstance().get(Calendar.YEAR);
    for (int y = thisYear - 1; y <= thisYear; ++y) {
      for (int p : PERIODS) {
        try {
          HttpURLConnection con = (HttpURLConnection) new URL(SRH_PATH + DANJI_DO).openConnection();
          con.setRequestMethod("POST");
          con.setDoOutput(true);
          con.setDoInput(true);
          con.setUseCaches(false);
          con.setDefaultUseCaches(false);
          OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
          out.write(String.format(POST_STR, y, p, gugun.getCode(), dongCode));
          out.close();
          reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

          Gson gson = new GsonBuilder().create();
          NameCodeListJson list = gson.fromJson(reader, NameCodeListJson.class);
          for (NameCodeJson j : list.getJsonList()) {
            String name = j.getNAME();
            Danji dj = danjiMap.get(name);
            if (dj == null) {
              dj = new Danji(this, j.getNAME(), Long.parseLong(j.getCODE()));
              danjiMap.put(dj.getName(), dj);
            }
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
    }
  }

  public Gugun getGugun() {
    return gugun;
  }

  public String getName() {
    return name;
  }

  public Collection<Danji> getDanjis () {
    return danjiMap.values();
  }

  @Override
  public int compareTo(Dong d) {
    int delta = gugun.compareTo(d.gugun);
    if (delta == 0) {
      return name.compareTo(d.name);
    }
    return delta;
  }
}
