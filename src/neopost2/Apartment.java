package neopost2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by jhooba on 2016-09-18.
 */
public class Apartment implements Comparable<Apartment> {
  private final Danji danji;
  private final int pyong;
  private List<Area> areas = new ArrayList<>();
  private List<TradeDeal> tradeDeals = new ArrayList<>();
  private List<RentDeal> rentDeals = new ArrayList<>();

  public Apartment(Danji danji, Area a) {
    this.danji = danji;
    this.pyong = a.getPyong();
    addArea(a);
  }

  public void addArea(Area a) {
    areas.add(a);
  }

  public void sortAreas() {
    Collections.sort(areas);
  }

  public void addTradeDeal(TradeDeal deal) {
    tradeDeals.add(deal);
  }

  public void addRentDeal(RentDeal deal) {
    rentDeals.add(deal);
  }

  public Danji getDanji() {
    return danji;
  }

  public int getPyong() {
    return pyong;
  }

  public int getAverageTradePrice() {
    float sum = 0;
    for (TradeDeal d : tradeDeals) {
      sum += d.getPrice();
    }
    return Math.round(sum / tradeDeals.size());
  }

  public int getTrimmedTradePrice() {
    int count = tradeDeals.size();
    int margin = (int)(count * 0.1f);
    float sum = 0;
    for (int i = margin; i < count - margin; ++i) {
      sum += tradeDeals.get(i).getPrice();
    }
    return Math.round(sum / (count - margin * 2));
  }

  public int getTradeCount() {
    return tradeDeals.size();
  }

  @Override
  public int compareTo(Apartment a) {
    int delta = danji.compareTo(a.danji);
    if (delta == 0) {
      return pyong - a.pyong;
    }
    return delta;
  }

  public void sortDeals() {
    Collections.sort(tradeDeals);
  }

  public float[] getRentFee() {
    float depositSum = 0;
    float rentSum = 0;
    for (RentDeal d : rentDeals) {
      depositSum += d.getDepositPrice();
      rentSum += d.getRentFee();
    }
    return new float[] { Math.round(depositSum / rentDeals.size()), Math.round(rentSum / rentDeals.size()) };
  }

  public int getRentCount() {
    return rentDeals.size();
  }

  public float[] getTrimmedRentFee() {
    int count = rentDeals.size();
    int margin = (int)(count * 0.1f);
    float depositSum = 0;
    Collections.sort(rentDeals, (o1, o2) -> o1.getDepositPrice() - o2.getDepositPrice());
    for (int i = margin; i < count - margin; ++i) {
      depositSum += rentDeals.get(i).getDepositPrice();
    }
    float rentSum = 0;
    Collections.sort(rentDeals, (o1, o2) -> o1.getRentFee() - o2.getRentFee());
    for (int i = margin; i < count - margin; ++i) {
      rentSum += rentDeals.get(i).getRentFee();
    }
    float rentRatio = depositSum == 0 ? 0 : rentSum * 12 / depositSum;
    return new float[] { Math.round(depositSum / (count - margin * 2)), Math.round(rentSum / (count - margin * 2)), rentRatio };
  }
}
