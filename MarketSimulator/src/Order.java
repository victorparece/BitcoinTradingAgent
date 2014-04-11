import org.joda.time.DateTime;

/**
 * Created by vic on 4/7/14.
 */
public abstract class Order
{

    protected OrderType orderType;
    protected double price;
    protected double volume;
    protected double filledQuantity;

    public enum OrderType {
        Buy, Sell, Both //Both is used for historical trade data
    }

    public double GetPrice()
    {
        return price;
    }

    public double GetVolume()
    {
        return volume;
    }

    public OrderType GetOrderType()
    {
        return orderType;
    }

    public double GetRemainingQuantity()
    {
        return volume - filledQuantity;
    }

    public void UpdateRemainingQuantity(double remainingQuantity)
    {
        filledQuantity = volume - remainingQuantity;
    }

}
