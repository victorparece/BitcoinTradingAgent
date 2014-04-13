import com.sun.org.apache.xpath.internal.operations.Bool;
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

    public void AddCompletedOrder(CompletedOrder co, double quantityUsed)
    {
        completedOrders.put(co, quantityUsed);
    }

    public Map<CompletedOrder, Double> GetCompletedOrders()
    {
        return completedOrders;
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
}
