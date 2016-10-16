package neopost2;

/**
 * Created by jhooba on 2016-09-19.
 */
public class Deal implements Comparable<Deal> {
  private final int price;

  public Deal(int price) {
    this.price = price;
  }

  public int getPrice() {
    return price;
  }

  @Override
  public int compareTo(Deal o) {
    return price - o.price;
  }
}
