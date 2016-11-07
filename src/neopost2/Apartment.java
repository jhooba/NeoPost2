package neopost2;

import java.util.ArrayList;
import java.util.Collections;
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

  public RentMetric getRentMetric() {
    int depositSum = 0;
    int feeSum = 0;
    for (RentDeal d : rentDeals) {
      depositSum += d.getDepositPrice();
      feeSum += d.getRentFee();
    }
    float feeOverDeposit = depositSum == 0 ? 0 : feeSum * 12.f / depositSum;
    float interest = ApartmentRegistry.getInstance().getInterest() / 100.f;
    float conversedAverageDeposit = rentDeals.size() == 0 ? 0 : (feeSum * 12.f / interest + depositSum) / rentDeals.size();
    float depositOverPrice = conversedAverageDeposit /  getAverageTradePrice();
    return new RentMetric(
            rentDeals.size() == 0 ? 0 : depositSum / rentDeals.size(),
            rentDeals.size() == 0 ? 0 : feeSum / rentDeals.size(),
            feeOverDeposit,
            depositOverPrice);
  }

  public int getRentCount() {
    return rentDeals.size();
  }

  public RentMetric getTrimmedRentMetric() {
    int count = rentDeals.size();
    int margin = Math.round(count * 0.1f);
    if (margin == 0) {
      return getRentMetric();
    }
    int depositSum = 0;
    Collections.sort(rentDeals, (o1, o2) -> o1.getDepositPrice() - o2.getDepositPrice());
    for (int i = margin; i < count - margin; ++i) {
      depositSum += rentDeals.get(i).getDepositPrice();
    }
    int feeSum = 0;
    Collections.sort(rentDeals, (o1, o2) -> o1.getRentFee() - o2.getRentFee());
    for (int i = margin; i < count - margin; ++i) {
      feeSum += rentDeals.get(i).getRentFee();
    }
    int sampleCount = count - margin * 2;
    float feeOverDeposit = depositSum == 0 ? 0 : feeSum * 12.f / depositSum;
    float interest = ApartmentRegistry.getInstance().getInterest() / 100.f;
    float conversedAverageDeposit = (feeSum * 12.f / interest + depositSum) / sampleCount;
    float depositOverPrice = conversedAverageDeposit /  getTrimmedTradePrice();
    return new RentMetric(depositSum / sampleCount,
            feeSum / sampleCount,
            feeOverDeposit,
            depositOverPrice);
  }
}
