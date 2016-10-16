package neopost2;

/**
 * Created by jhooba on 2016-09-19.
 */
public class TradeDeal implements Comparable<TradeDeal> {
  private final int price;

  public TradeDeal(int price) {
    this.price = price;
  }

  public int getPrice() {
    return price;
  }

  @Override
  public int compareTo(TradeDeal o) {
    return price - o.price;
  }
}
