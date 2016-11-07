package neopost2;

/**
 * Created by jhooba on 2016-11-07.
 */
public class RentMetric {
  private int averageDeposit;
  private int monthlyRentFee;
  private float feeOverDeposit;
  private float depositOverPrice;

  public RentMetric(int averageDeposit, int monthlyRentFee, float feeOverDeposit, float depositOverPrice) {
    this.averageDeposit = averageDeposit;
    this.monthlyRentFee = monthlyRentFee;
    this.feeOverDeposit = feeOverDeposit;
    this.depositOverPrice = depositOverPrice;
  }

  public int getAverageDeposit() {
    return averageDeposit;
  }

  public int getMonthlyRentFee() {
    return monthlyRentFee;
  }

  public float getFeeOverDeposit() {
    return feeOverDeposit;
  }

  public float getDepositOverPrice() {
    return depositOverPrice;
  }
}
