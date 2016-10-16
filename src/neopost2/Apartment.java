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
  private List<Deal> deals = new ArrayList<>();

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

  public void addDeal(Deal deal) {
    deals.add(deal);
  }

  public Danji getDanji() {
    return danji;
  }

  public int getPyong() {
    return pyong;
  }

  public int getAveragePrice() {
    float sum = 0;
    for (Deal d : deals) {
      sum += d.getPrice();
    }
    return Math.round(sum / deals.size());
  }

  public int getTrimmedPrice() {
    int count = deals.size();
    int margin = (int)(count * 0.1f);
    float sum = 0;
    for (int i = margin; i < count - margin; ++i) {
      sum += deals.get(i).getPrice();
    }
    return Math.round(sum / (count - margin * 2));
  }

  public int getDealCount() {
    return deals.size();
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
    Collections.sort(deals);
  }
}
