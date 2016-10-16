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
  private int minDealCount = 1;
  private List<Apartment> filtered = apartments;
  private float trimmedMinPrice = 0;
  private int minPyong = 0;

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

  public void setMinDealCount(int minDealCount, boolean apply) {
    if (this.minDealCount == minDealCount) {
      return;
    }
    this.minDealCount = minDealCount;
    if (!apply) {
      return;
    }
    applyFilter();
  }

  private List<Apartment> filter() {
    List<Apartment> fd = new ArrayList<>();
    for (Apartment a : apartments) {
      if (a.getDealCount() < minDealCount) {
        continue;
      }
      if (a.getTrimmedPrice() < trimmedMinPrice) {
        continue;
      }
      if (a.getPyong() < minPyong) {
        continue;
      }
      fd.add(a);
    }
    return fd;
  }

  public int getMinDealCount() {
    return minDealCount;
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
    if (minDealCount <= 1 && trimmedMinPrice <= 0 && minPyong <= 0) {
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
}
