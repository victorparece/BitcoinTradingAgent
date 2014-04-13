/**
 * Created by vic on 4/13/14.
 */
public class Strategy_SubmitAndLeave extends Strategy
{
    private static double PERCENT = 0.05;

    public Strategy_SubmitAndLeave(MarketSimulator marketSimulator)
    {
        this.name = "Submit and Leave";
        this.marketSimulator = marketSimulator;
        this.currentDateTime = marketSimulator.GetStartDateTime();
    }

    public void DoNextAction(Order.OrderType orderType)
    {
        double price = GetMarketPrice();

        //Buy at PERCENT below market price
        if (orderType == Order.OrderType.Buy && GetTicks() == 0)
        {
            price -= price*PERCENT;
            SubmitLimitOrder(orderType, price, GetAvailableMoney()/price);
        }
        //Sell at PERCENT above market price
        else if (orderType == Order.OrderType.Sell && GetTicks() == 0)
        {
            price += price*PERCENT;
            SubmitLimitOrder(orderType, price, GetAvailableVolume());
        }
    }
}
