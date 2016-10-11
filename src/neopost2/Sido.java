package neopost2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by jhooba on 2016-09-18.
 */
public class Sido extends NodeBase implements Comparable<Sido> {

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

  private final Country country;
  private final String name;
  private final int code;
  private ArrayList<Gugun> guguns;

  public Sido(Country country, String name, int code) {
    this.country = country;
    this.name = name;
    this.code = code;
  }

  @Override
  public void populate() {
    guguns = new ArrayList<>();
    populateSub();
  }

  @Override
  public boolean useStored() {
    return country.useStored();
  }

  @Override
  protected InputStream openConnectedInputStream(Object[] args) throws IOException {
    HttpURLConnection con = (HttpURLConnection) new URL(SRH_PATH + GUGUN_DO).openConnection();
    con.setConnectTimeout(5000);
    con.setRequestMethod("POST");
    con.setDoOutput(true);
    con.setDoInput(true);
    con.setUseCaches(false);
    con.setDefaultUseCaches(false);
    OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
    out.write(String.format(POST_STR, code));
    out.close();
    return con.getInputStream();
  }

  @Override
  protected void parseContent(BufferedReader reader, Object[] args) throws IOException {
    Gson gson = new GsonBuilder().create();
    NameCodeListJson list = gson.fromJson(reader, NameCodeListJson.class);
    guguns.ensureCapacity(list.getJsonList().size());
    for (NameCodeJson j : list.getJsonList()) {
      guguns.add(new Gugun(this, j.getNAME(), Integer.parseInt(j.getCODE())));
    }
  }

  @Override
  protected String getStoredFileName(Object[] args) {
    return "sd" + code;
  }

  public String getName() {
    return name;
  }

  public ArrayList<Gugun> getGuguns() {
    return guguns;
  }

  @Override
  public int compareTo(@NotNull Sido s) {
    return code - s.code;
  }
}
