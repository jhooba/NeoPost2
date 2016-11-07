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
  private List<Apartment> filtered = apartments;
  private float interest;
  private int minTradeCount;
  private float trimmedMinPrice;
  private int minPyong ;
  private int minRentDealCount ;
  private float minFeeOverDeposit;
  private float minDepositOverPrice;

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
      RentMetric rm = a.getTrimmedRentMetric();
      if (rm.getFeeOverDeposit() < minFeeOverDeposit) {
        continue;
      }
      if (rm.getDepositOverPrice() < minDepositOverPrice) {
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
    if (minTradeCount <= 0 &&
            trimmedMinPrice <= 0 &&
            minPyong <= 0 &&
            minRentDealCount <= 0 &&
            minFeeOverDeposit <= 0 &&
            minDepositOverPrice <= 0) {
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

  public float getMinFeeOverDeposit() {
    return minFeeOverDeposit;
  }

  public void setMinFeeOverDeposit(float value, boolean apply) {
    if (minFeeOverDeposit == value) {
      return;
    }
    minFeeOverDeposit = value;
    if (!apply) {
      return;
    }
    applyFilters();
  }

  public float getMinDepositOverPrice() {
    return minDepositOverPrice;
  }

  public void setMinDepositOverPrice(float value, boolean apply) {
    if (minDepositOverPrice == value) {
      return;
    }
    minDepositOverPrice = value;
    if (!apply) {
      return;
    }
    applyFilters();
  }

  public void loadFilters(Properties props) {
    String str = props.getProperty("Interest", "2.23");
    float f = Float.parseFloat(str);
    setInterest(f);
    str = props.getProperty("MinTradeCount", "0");
    int i = Integer.parseInt(str);
    setMinTradeCount(i, false);
    str = props.getProperty("TrimmedMinPrice", "0");
    f = Float.parseFloat(str);
    setTrimmedMinPrice(f, false);
    str = props.getProperty("MinPyong", "0");
    i = Integer.parseInt(str);
    setMinPyong(i, false);
    str = props.getProperty("MinRentDealCount", "0");
    i = Integer.parseInt(str);
    setMinRentDealCount(i, false);
    str = props.getProperty("MinFeeOverDeposit", "0");
    f = Float.parseFloat(str);
    setMinFeeOverDeposit(f, false);
  }

  public void storeFilters(Properties props) {
    props.setProperty("Interest", "" + getInterest());
    props.setProperty("MinTradeCount", "" + getMinTradeCount());
    props.setProperty("TrimmedMinPrice", "" + getTrimmedMinPrice());
    props.setProperty("MinPyong", "" + getMinPyong());
    props.setProperty("MinRentDealCount", "" + getMinRentDealCount());
    props.setProperty("MinFeeOverDeposit", "" + getMinFeeOverDeposit());
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
