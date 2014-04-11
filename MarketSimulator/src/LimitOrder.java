import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by vic on 4/7/14.
 */
public class LimitOrder extends Order implements Comparable<LimitOrder>
{
    private DateTime submittedDateTime;
    private Map<CompletedOrder, Double> completedOrders = new LinkedHashMap<CompletedOrder, Double>();

    public LimitOrder(DateTime submittedDateTime, OrderType orderType, double price, double volume)
    {
        super();
        this.submittedDateTime = submittedDateTime;
        this.orderType = orderType;
        this.price = price;
        this.volume = volume;
        this.filledQuantity = 0;
    }

    public DateTime GetSubmittedDateTime()
    {
        return submittedDateTime;
    }

    public boolean IsOrderCompleted()
    {
        return !completedOrders.isEmpty();
    }

    public void AddCompletedOrder(CompletedOrder co, double quantityUsed)
    {
        completedOrders.put(co, quantityUsed);
    }

    @Override
    public int compareTo(LimitOrder limitOrder)
    {
        if (this.GetPrice() != limitOrder.GetPrice())
            return Double.compare(this.GetPrice(), limitOrder.GetPrice());

        //TODO: Make sure this is sorted correctly
        if (this.GetSubmittedDateTime().isBefore(limitOrder.GetSubmittedDateTime()))
            return -1;
        else if (this.GetSubmittedDateTime().isAfter(limitOrder.GetSubmittedDateTime()))
            return 1;
        else
            return 0;
    }

    public double GetFilledQuantityCost()
    {
        double total = 0;

        for (Map.Entry<CompletedOrder, Double> entry : completedOrders.entrySet())
        {
            //If we are buying, we get the price of the sell order
            if (orderType == OrderType.Buy)
                total += entry.getValue() * entry.getKey().GetPrice();
            //If we are selling, we get our posted sell price
            else if (orderType == OrderType.Sell)
                total += entry.getValue() * price;
        }

        return total;
    }
}
