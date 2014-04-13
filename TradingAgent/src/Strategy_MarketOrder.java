/**
 * Created by vic on 4/12/14.
 */
public class Strategy_MarketOrder extends Strategy
{
    public Strategy_MarketOrder(MarketSimulator marketSimulator)
    {
        this.name = "Market Order";
        this.marketSimulator = marketSimulator;
        this.currentDateTime = marketSimulator.GetStartDateTime();
    }

    public void DoNextAction(Order.OrderType orderType)
    {
        if (GetTicks() == 0)
            SubmitMarketOrder(orderType, GetAvailableMoney());
    }
}
