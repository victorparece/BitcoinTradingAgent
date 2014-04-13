import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Created by vic on 4/7/14.
 */
public class MarketSimulator
{
    private ArrayList<CompletedOrder> completedOrders = new ArrayList<CompletedOrder>();
    private ArrayList<LimitOrder> openOrders = new ArrayList<LimitOrder>();
    private DateTime startDateTime;
    private DateTime endDateTime;
    private DateTime lastMarketUpdate;

    public MarketSimulator(String inputFile, DateTime startDateTime, DateTime endDateTime)
    {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(inputFile));

            //Market data should start a little bit before requested time
            lastMarketUpdate
                    = startDateTime
                    = new DateTime(startDateTime, DateTimeZone.UTC);

            //Read relevant section of file into completedOrders arraylist
            String line = br.readLine();
            while (line != null)
            {
                String[] linePart = line.split("\\s*,\\s*");

                DateTime executionDateTime = new DateTime(Long.parseLong(linePart[0]) * 1000L, DateTimeZone.UTC);

                if ((executionDateTime.isEqual(startDateTime) || executionDateTime.isAfter(startDateTime))
                        && executionDateTime.isBefore(endDateTime))
                {
                    double price = Double.parseDouble(linePart[1]);
                    double volume = Double.parseDouble(linePart[2]);

                    completedOrders.add(new CompletedOrder(executionDateTime, Order.OrderType.Both, price, volume));
                }
                //Don't loop through unused data
                else if (executionDateTime.isAfter(endDateTime))
                    break;

                line = br.readLine();
            }
        } catch (IOException e)
        {
            System.out.println("IOException occurred while reading " + inputFile + ".");
            System.exit(1);
        }
    }

    /**
     * Returns the index of the location with in the completedOrders arraylist
     * @return
     */
    private int FindCurrentLocation(DateTime dateTime)
    {
        for (int i = 0; i < completedOrders.size(); i++)
        {
            CompletedOrder trade = completedOrders.get(i);
            if (dateTime.isEqual(trade.GetExecutionDateTime()) || trade.GetExecutionDateTime().isAfter(dateTime))
                return i;
        }
        return -1;
    }

    public LimitOrder SubmitLimitOrder(DateTime currentDateTime, Order.OrderType orderType, double price, double volume)
    {
        LimitOrder newLimitOrder = new LimitOrder(currentDateTime, orderType, price, volume);
        openOrders.add(newLimitOrder);

        return newLimitOrder;
    }

    public CompletedOrder SubmitMarketOrder(DateTime currentDateTime, Order.OrderType orderType, double totalPrice)
    {
        int currentIndex = FindCurrentLocation(currentDateTime);
        double amountRemaining = totalPrice;
        double volume = 0;
        double baselinePrice = completedOrders.get(currentIndex).GetPrice();

        while (amountRemaining > 0)
        {
            CompletedOrder matchedOrder = completedOrders.get(currentIndex);
            if (matchedOrder.GetPrice() >= baselinePrice)
            {
                if (amountRemaining <= matchedOrder.GetVolume()*matchedOrder.GetPrice())
                {
                    volume += amountRemaining / matchedOrder.GetPrice();
                    amountRemaining = 0;
                }
                else if (amountRemaining > matchedOrder.GetVolume()*matchedOrder.GetPrice())
                {
                    amountRemaining -= matchedOrder.GetVolume() * matchedOrder.GetPrice();
                    volume += matchedOrder.GetVolume();
                    //TODO: Consider making market orders more expensive, by updating baseline
                    //baselinePrice = matchedOrder.GetPrice();
                }
            }
            currentIndex++;
        }

        //Total cost of market order is averaged over volume
        CompletedOrder completedOrder = new CompletedOrder(currentDateTime, orderType, totalPrice/volume, volume);
        completedOrder.UpdateRemainingQuantity(0.0);
        return completedOrder;
    }

    public void UpdateMarketState(DateTime currentDateTime)
    {
        //Sort open orders
        Collections.sort(openOrders); //TODO: This order also needs to take order type into consideration (not sure if this is correct, with the data I have buy/sell orders would be competing for the same completed orders)

        //Attempt to process open orders
        for (LimitOrder openOrder : openOrders)
        {
            int currentIndex = FindCurrentLocation(lastMarketUpdate);
            CompletedOrder matchedOrder = completedOrders.get(currentIndex);

            //Loop for closed orders from last update time to current time, or until order is filled
            while (lastMarketUpdate.isBefore(matchedOrder.GetExecutionDateTime())
                    && matchedOrder.GetExecutionDateTime().isBefore(currentDateTime)
                    && openOrder.GetSubmittedDateTime().isBefore(matchedOrder.GetExecutionDateTime())
                    && openOrder.GetRemainingQuantity() > 0)
            {
                //Don't bother with order if it has no remaining quantity
                if (matchedOrder.GetRemainingQuantity() > 0)
                {
                    //Check if price matches criteria
                    if ((matchedOrder.GetPrice() <= openOrder.GetPrice()
                            && openOrder.GetOrderType() == Order.OrderType.Buy)
                        || (matchedOrder.GetPrice() >= openOrder.GetPrice()
                            && openOrder.GetOrderType() == Order.OrderType.Sell))
                    {
                        //Order cannot be completely filled with this completed order
                        if (matchedOrder.GetRemainingQuantity() < openOrder.GetRemainingQuantity())
                        {
                            openOrder.AddCompletedOrder(matchedOrder, matchedOrder.GetRemainingQuantity());
                            openOrder.UpdateRemainingQuantity(openOrder.GetRemainingQuantity() - matchedOrder.GetRemainingQuantity());
                            matchedOrder.UpdateRemainingQuantity(0.0);
                        }
                        //Entire order can be filled with this completed order
                        else if (matchedOrder.GetRemainingQuantity() >= openOrder.GetRemainingQuantity())
                        {
                            openOrder.AddCompletedOrder(matchedOrder, openOrder.GetRemainingQuantity());
                            matchedOrder.UpdateRemainingQuantity(matchedOrder.GetRemainingQuantity() - openOrder.GetRemainingQuantity());
                            openOrder.UpdateRemainingQuantity(0.0);
                        }
                    }
                }

                currentIndex++;
                matchedOrder = completedOrders.get(currentIndex);
            }
        }
        lastMarketUpdate = currentDateTime;
    }

    public double GetMarketPrice(DateTime currentDateTime)
    {
        return completedOrders.get(FindCurrentLocation(currentDateTime)).GetPrice();
    }

    public DateTime GetStartDateTime()
    {
        return startDateTime;
    }

    public DateTime GetEndDateTime()
    {
        return endDateTime;
    }

}
