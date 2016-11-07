package neopost2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * Created by jhooba on 2016-09-18.
 */
public class Danji extends NodeBase implements Comparable<Danji> {
  private static final String MONTHLY_POST_STR = "menuGubun=A&houseType=%d&gugunCode=%d&danjiCode=%d&srhYear=%d";

  private static class DealObj {
    private String BLDG_AREA;
    private String BOBN;  // 번지일까?
//    private String APTFNO;  // 층수
//    private String DEAL_DD;  // 거래일시
//    private String DEAL_MM;  // 거래월
//    private String BLDG_NM;  // 이름
//    private String BUBN;  // ?
//    private String BLDG_CD;  // 단지 코드
    private String BUILD_YEAR;
    private String RENT_AMT;
    private String SUM_AMT;

    public String getBLDG_AREA() {
      return BLDG_AREA;
    }

    public String getBOBN() {
      return BOBN;
    }

    public String getBUILD_YEAR() {
      return BUILD_YEAR;
    }

    public String getRENT_AMT() {
      return RENT_AMT;
    }

    public String getSUM_AMT() {
      return SUM_AMT;
    }
  }

  private static class MonthListObj {
    private List<DealObj> month1List;
    private List<DealObj> month2List;
    private List<DealObj> month3List;
    private List<DealObj> month4List;
    private List<DealObj> month5List;
    private List<DealObj> month6List;
    private List<DealObj> month7List;
    private List<DealObj> month8List;
    private List<DealObj> month9List;
    private List<DealObj> month10List;
    private List<DealObj> month11List;
    private List<DealObj> month12List;

    public List<List<DealObj>> getMonthList() {
      List<List<DealObj>> monthList = new ArrayList<>(12);
      monthList.add(month1List);
      monthList.add(month2List);
      monthList.add(month3List);
      monthList.add(month4List);
      monthList.add(month5List);
      monthList.add(month6List);
      monthList.add(month7List);
      monthList.add(month8List);
      monthList.add(month9List);
      monthList.add(month10List);
      monthList.add(month11List);
      monthList.add(month12List);
      return monthList;
    }
  }

  private static class MonthListListObj {
    private List<MonthListObj> jsonList;

    public List<MonthListObj> getJsonList() {
      return jsonList;
    }
  }

  private final Dong dong;
  private final String name;
  private final long code;
  private String bobn;  // 번지일까?
  private int buildYear = -1;
  private final TreeMap<Integer, Apartment> apartmentMap = new TreeMap<>();

  public Danji(Dong dong, String name, long code) {
    super(dong.zip);
    this.dong = dong;
    this.name = name;
    this.code = code;
  }

  @Override
  protected InputStream openConnectedInputStream(Object... args) throws IOException {
    int type = (Integer)args[0];
    int y = (Integer)args[1];

    HttpURLConnection con = (HttpURLConnection) new URL(SRH_PATH + MONTHLY_DO).openConnection();
    con.setConnectTimeout(5000);
    con.setRequestMethod("POST");
    con.setRequestProperty("Referer", "http://rt.molit.go.kr/srh/dtl.do");
    con.setDoOutput(true);
    con.setDoInput(true);
    con.setUseCaches(false);
    con.setDefaultUseCaches(false);
    OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
    out.write(String.format(MONTHLY_POST_STR, type, dong.getGugun().getCode(), code, y));
    out.close();
    return con.getInputStream();
  }

  @Override
  protected void parseContent(BufferedReader reader, Object... args) {
    int type = (Integer)args[0];
    int y = (Integer)args[1];

    int thisYear = Calendar.getInstance().get(Calendar.YEAR);
    int thisMonth = Calendar.getInstance().get(Calendar.MONTH);

    Gson gson = new GsonBuilder().create();
    Danji.MonthListListObj list = gson.fromJson(reader, Danji.MonthListListObj.class);
    if (list.getJsonList() == null) {
      return;
    }
    for (Danji.MonthListObj j : list.getJsonList()) {
      int startMonth = (y == thisYear) ? 0 : thisMonth - 1;
      for (int m = startMonth; m < j.getMonthList().size(); ++m) {
        List<Danji.DealObj> ds = j.getMonthList().get(m);
        for (Danji.DealObj dl : ds) {
          Area a = new Area(dl.getBLDG_AREA());
          int p = a.getPyong();
          Apartment apartment = apartmentMap.get(p);
          if (apartment == null) {
            apartment = new Apartment(this, a);
            apartmentMap.put(p, apartment);
          } else {
            apartment.addArea(a);
          }

          if (bobn == null) {
            bobn = dl.getBOBN();
          }
          if (buildYear == -1) {
            buildYear = Integer.parseInt(dl.getBUILD_YEAR());
          }
          if (type == 1) {
            apartment.addTradeDeal(new TradeDeal(Integer.parseInt(dl.getSUM_AMT().replaceAll(",", ""))));
          } else {
            apartment.addRentDeal(
                new RentDeal(Integer.parseInt(dl.getSUM_AMT().replaceAll(",", "")),
                    Integer.parseInt(dl.getRENT_AMT().replaceAll(",", ""))));
          }
        }
      }
    }
  }

  @Override
  protected String getStoredFileName(Object... args) {
    int type = (Integer)args[0];
    int y = (Integer)args[1];
    return "dj" + code + "." + type + "." + y;
  }

  @Override
  public void populate() {
    int thisYear = Calendar.getInstance().get(Calendar.YEAR);
    for (int y = thisYear - 1; y <= thisYear; ++y) {
      populateSub(1, y);
      populateSub(2, y);
    }
    for (Apartment ap : apartmentMap.values()) {
      ap.sortAreas();
      ap.sortDeals();
      ApartmentRegistry.getInstance().addApartment(ap);
    }
  }

  public Dong getDong() {
    return dong;
  }

  public String getName() {
    return name;
  }

  public long getCode() {
    return code;
  }

  @Override
  public int compareTo(@NotNull Danji d) {
    int delta = dong.compareTo(d.dong);
    if (delta == 0) {
      return name.compareTo(d.name);
    }
    return delta;
  }
}
