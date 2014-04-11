import org.joda.time.DateTime;

/**
 * Created by vic on 4/7/14.
 */
public class CompletedOrder extends Order
{
    private DateTime executionDateTime;

    public CompletedOrder(DateTime executionDateTime, OrderType orderType, double price, double volume)
    {
        super();
        this.executionDateTime = executionDateTime;
        this.orderType = orderType;
        this.price = price;
        this.volume = volume;
        this.filledQuantity = 0;
    }

    public DateTime GetExecutionDateTime()
    {
        return executionDateTime;
    }

}
