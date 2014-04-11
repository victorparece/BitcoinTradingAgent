import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.print.attribute.standard.DateTimeAtCompleted;
import java.util.ArrayList;

/**
 * Created by vic on 4/7/14.
 */
public class StrategyEvaluator
{
//    private Strategy strategy;

//    public StrategyEvaluator(Strategy strategy)
//    {
//        this.strategy = strategy;
//    }

    public static void main(String[ ] args)
    {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
        DateTime currentDateTime = new DateTime(formatter.parseDateTime("01/01/2012 12:00:00"), DateTimeZone.UTC);
        DateTime endDateTime = new DateTime(formatter.parseDateTime("01/05/2012 12:00:00"), DateTimeZone.UTC);

        MarketSimulator marketSimulator = new MarketSimulator("data/btceUSD.csv", currentDateTime, endDateTime);

        ArrayList<Order> submittedOrders = new ArrayList<Order>();

        //Loop until end date is reached
        while (currentDateTime.isBefore(endDateTime))
        {
            submittedOrders.add(marketSimulator.SubmitMarketOrder(currentDateTime, Order.OrderType.Buy, 4.0));
            submittedOrders.add(marketSimulator.SubmitLimitOrder(currentDateTime, Order.OrderType.Sell, 4.935, 4.0));
            submittedOrders.add(marketSimulator.SubmitLimitOrder(currentDateTime.plusSeconds(5), Order.OrderType.Buy, 5.30, 4.0));
            submittedOrders.add(marketSimulator.SubmitLimitOrder(currentDateTime.plusSeconds(15), Order.OrderType.Sell, 5.30, 4.0));

            currentDateTime = currentDateTime.plusMinutes(15);
            marketSimulator.UpdateMarketState(currentDateTime);
        }
    }
}
