package neopost2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    applyFilter();
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
    applyFilter();
  }

  private void applyFilter() {
    if (minTradeCount <= 0 && trimmedMinPrice <= 0 && minPyong <= 0 && minRentDealCount <= 0) {
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
    applyFilter();
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
    applyFilter();
  }
}
