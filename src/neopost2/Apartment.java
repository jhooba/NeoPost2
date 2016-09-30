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
    if (deals.size() <= 2) {
      return -1;
    }
    Deal top = null;
    Deal bot = null;
    for (Deal d : deals) {
      if (top == null) {
        top = d;
      } else if (top.getPrice() < d.getPrice()) {
        top = d;
      }
      if (bot == null) {
        bot = d;
      } else if (bot.getPrice() > d.getPrice()) {
        bot = d;
      }
    }
    float sum = 0;
    for (Deal d : deals) {
      if (top == d || bot == d) {
        continue;
      }
      sum += d.getPrice();
    }
    return Math.round(sum / deals.size());
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
}
