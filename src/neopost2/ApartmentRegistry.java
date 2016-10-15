package neopost2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jhooba on 2016-09-19.
 */
public class ApartmentRegistry {
  private static final ApartmentRegistry instance = new ApartmentRegistry();
  private List<Apartment> syncApartments = Collections.synchronizedList(new ArrayList<>());

  public static ApartmentRegistry getInstance() {
    return instance;
  }

  public void addApartment(Apartment apartment) {
    syncApartments.add(apartment);
  }

  public List<Apartment> getApartments() {
    return syncApartments;
  }

  public void sortApartments() {
    Collections.sort(syncApartments);
  }

  public void clear() {
    syncApartments = Collections.synchronizedList(new ArrayList<>());
  }
}
