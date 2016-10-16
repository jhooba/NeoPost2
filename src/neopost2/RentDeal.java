package neopost2;

/**
 * Created by jhooba on 2016-10-16.
 */
public class RentDeal implements Comparable<RentDeal> {
  private final int depositPrice;
  private final int rentFee;

  public RentDeal(int depositPrice, int rentFee) {
    this.depositPrice = depositPrice;
    this.rentFee = rentFee;
  }

  @Override
  public int compareTo(RentDeal o) {
    return 0;
  }

  public int getDepositPrice() {
    return depositPrice;
  }

  public int getRentFee() {
    return rentFee;
  }
}
