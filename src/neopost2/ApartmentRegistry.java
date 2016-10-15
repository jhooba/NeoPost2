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
    if (minDealCount <= 1) {
      filtered = apartments;
    } else {
      filtered = filter();
    }
  }

  private List<Apartment> filter() {
    List<Apartment> fd = new ArrayList<Apartment>();
    for (Apartment a : apartments) {
      if (a.getDealCount() < minDealCount) {
        continue;
      }
      fd.add(a);
    }
    return fd;
  }

  public int getMinDealCount() {
    return minDealCount;
  }
}
