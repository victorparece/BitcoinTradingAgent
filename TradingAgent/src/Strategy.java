import org.joda.time.DateTime;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by vic on 4/12/14.
 */
public abstract class Strategy
{
    protected String name;
    protected MarketSimulator marketSimulator;
    private ArrayList<Order> submittedOrders = new ArrayList<Order>();
    protected DateTime currentDateTime;

    private double money = 50;
    private double volume = 50;

    private static int TIME_STEP = 15;

    private int ticks = 0;

    public void Evaluate()
    {
        while (currentDateTime.isBefore(marketSimulator.GetEndDateTime()))
        {
            DoNextAction(Order.OrderType.Buy);
            marketSimulator.UpdateMarketState(currentDateTime);
            UpdateMoneyAndVolume();
            currentDateTime = currentDateTime.plusMinutes(TIME_STEP);
            ticks++;
        }
    }

    protected void SubmitMarketOrder(Order.OrderType orderType, double totalPrice)
    {
        if (totalPrice > 0.0)
        {
            CompletedOrder completedOrder = marketSimulator.SubmitMarketOrder(currentDateTime, orderType, totalPrice);
            submittedOrders.add(completedOrder);
            UpdateMoneyAndVolume();
        }
    }

    protected void SubmitLimitOrder(Order.OrderType orderType, double price, double volume)
    {
        LimitOrder limitOrder = marketSimulator.SubmitLimitOrder(currentDateTime, orderType, price, volume);
        submittedOrders.add(limitOrder);
        UpdateMoneyAndVolume();
    }

    private void UpdateMoneyAndVolume()
    {
        for (Order order : submittedOrders)
        {
            //Market order
            if (order instanceof CompletedOrder && !((CompletedOrder) order).IsHandled())
            {
                CompletedOrder completedOrder = (CompletedOrder)order;

                if (completedOrder.GetOrderType() == Order.OrderType.Buy)
                {
                    money -= completedOrder.GetFilledQuantity() * completedOrder.GetPrice();
                    volume += completedOrder.GetFilledQuantity();
                }
                else if (completedOrder.GetOrderType() == Order.OrderType.Sell)
                {
                    money += completedOrder.GetFilledQuantity() * completedOrder.GetPrice();
                    volume -= completedOrder.GetFilledQuantity();
                }

                completedOrder.SetHandled();
            }
            //Limit order
            else if (order instanceof LimitOrder)
            {
                LimitOrder limitOrder = (LimitOrder)order;

                for (Map.Entry<CompletedOrder, Double> entry : limitOrder.GetCompletedOrders().entrySet())
                {
                    CompletedOrder completedOrder = entry.getKey();

                    if (!entry.getKey().IsHandled())
                    {
                        double orderVolume = entry.getValue();

                        //If we are buying, we get the price of the sell order
                        if (limitOrder.GetOrderType() == Order.OrderType.Buy)
                        {
                            money -= orderVolume * completedOrder.GetPrice();
                            volume += orderVolume;
                        }
                        //If we are selling, we get our posted sell price
                        else if (limitOrder.GetOrderType() == Order.OrderType.Sell)
                        {
                            money += orderVolume * limitOrder.GetPrice();
                            volume -= orderVolume;
                        }

                        completedOrder.SetHandled();
                    }
                }
            }
        }
    }

    protected double GetAvailableMoney()
    {
        return money;
    }

    protected double GetAvailableVolume()
    {
        return volume;
    }

    protected int GetTicks()
    {
        return ticks;
    }

    protected int GetTotalTicks()
    {
        DateTime dateTime = marketSimulator.GetStartDateTime();
        int totalTicks = 0;
        while (dateTime.isBefore(marketSimulator.GetEndDateTime()))
        {
            dateTime = dateTime.plusMinutes(TIME_STEP);
            totalTicks++;
        }
        return totalTicks;
    }

    protected double GetMarketPrice()
    {
        return marketSimulator.GetMarketPrice(currentDateTime);
    }

    protected abstract void DoNextAction(Order.OrderType orderType);
}
