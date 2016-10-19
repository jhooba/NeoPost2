package neopost2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Created by jhooba on 2016-09-19.
 */
public class ApartmentRegistry {
  private static final ApartmentRegistry instance = new ApartmentRegistry();
  private List<Apartment> apartments = new ArrayList<>();
  private List<Apartment> syncApartments = Collections.synchronizedList(apartments);
  private int minTradeCount = 0;
  private List<Apartment> filtered = apartments;
  private float trimmedMinPrice = 0;
  private int minPyong = 0;
  private int minRentDealCount = 0;
  private float minRentRatio = 0;
  private float interest;

  public static ApartmentRegistry getInstance() {
    return instance;
  }

  public void addApartment(Apartment apartment) {
    syncApartments.add(apartment);
  }

  public List<Apartment> getApartments() {
    return apartments;
  }

  public List<Apartment> getFiltered() {
    return filtered;
  }

  public void sortApartments() {
    Collections.sort(apartments);
  }

  public void clearApartments() {
    apartments.clear();
  }

  public void setMinTradeCount(int minTradeCount, boolean apply) {
    if (this.minTradeCount == minTradeCount) {
      return;
    }
    this.minTradeCount = minTradeCount;
    if (!apply) {
      return;
    }
    applyFilters();
  }

  private List<Apartment> filter() {
    List<Apartment> fd = new ArrayList<>();
    for (Apartment a : apartments) {
      if (a.getTradeCount() < minTradeCount) {
        continue;
      }
      if (a.getTrimmedTradePrice() < trimmedMinPrice) {
        continue;
      }
      if (a.getPyong() < minPyong) {
        continue;
      }
      if (a.getRentCount() < minRentDealCount) {
        continue;
      }
      if (a.getTrimmedRentFee()[2] < minRentRatio) {
        continue;
      }
      fd.add(a);
    }
    return fd;
  }

  public int getMinTradeCount() {
    return minTradeCount;
  }

  public float getTrimmedMinPrice() {
    return trimmedMinPrice;
  }

  public void setTrimmedMinPrice(float minPrice, boolean apply) {
    if (this.trimmedMinPrice == minPrice) {
      return;
    }
    this.trimmedMinPrice = minPrice;
    if (!apply) {
      return;
    }
    applyFilters();
  }

  public void applyFilters() {
    if (minTradeCount <= 0 && trimmedMinPrice <= 0 && minPyong <= 0 && minRentDealCount <= 0 && minRentRatio <= 0) {
      filtered = apartments;
    } else {
      filtered = filter();
    }
  }

  public int getMinPyong() {
    return minPyong;
  }

  public void setMinPyong(int minPyong, boolean apply) {
    if (this.minPyong == minPyong) {
      return;
    }
    this.minPyong = minPyong;
    if (!apply) {
      return;
    }
    applyFilters();
  }

  public int getMinRentDealCount() {
    return minRentDealCount;
  }

  public void setMinRentDealCount(int minRentDealCount, boolean apply) {
    if (this.minRentDealCount == minRentDealCount) {
      return;
    }
    this.minRentDealCount = minRentDealCount;
    if (!apply) {
      return;
    }
    applyFilters();
  }

  public float getMinRentRatio() {
    return minRentRatio;
  }

  public void setMinRentRatio(float minRentRatio, boolean apply) {
    if (this.minRentRatio == minRentRatio) {
      return;
    }
    this.minRentRatio = minRentRatio;
    if (!apply) {
      return;
    }
    applyFilters();
  }

  public void loadFilters(Properties props) {
    String str = props.getProperty("MinTradeCount", "0");
    int i = Integer.parseInt(str);
    setMinTradeCount(i, false);
    str = props.getProperty("TrimmedMinPrice", "0");
    float f = Float.parseFloat(str);
    setTrimmedMinPrice(f, false);
    str = props.getProperty("MinPyong", "0");
    i = Integer.parseInt(str);
    setMinPyong(i, false);
    str = props.getProperty("MinRentDealCount", "0");
    i = Integer.parseInt(str);
    setMinRentDealCount(i, false);
    str = props.getProperty("MinRentRatio", "0");
    f = Float.parseFloat(str);
    setMinRentRatio(f, false);
    str = props.getProperty("Interest", "2.23");
    f = Float.parseFloat(str);
    setInterest(f);
  }

  public void storeFilters(Properties props) {
    props.setProperty("MinTradeCount", "" + getMinTradeCount());
    props.setProperty("TrimmedMinPrice", "" + getTrimmedMinPrice());
    props.setProperty("MinPyong", "" + getMinPyong());
    props.setProperty("MinRentDealCount", "" + getMinRentDealCount());
    props.setProperty("MinRentRatio", "" + getMinRentRatio());
    props.setProperty("Interest", "" + getInterest());
  }

  public void setInterest(float interest) {
    if (this.interest == interest) {
      return;
    }
    this.interest = interest;
  }

  public float getInterest() {
    return interest;
  }
}
