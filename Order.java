public abstract class Order
{
    private String type;
    private String side;
    private int qty;

    public Order(String type, String side, int qty)
    {
        if ((!type.equals("limit") && !type.equals("market")) || 
            (!side.equals("buy") && !side.equals("sell")) ||
            qty <= 0)
            throw new IllegalArgumentException("Invalid arguments");

        this.type = type;
        this.side = side;
        this.qty = qty;
    }
}

public class MarketOrder extends Order
{
    public MarketOrder(String side, int qty)
    {
        super("market", side, qty);
    }
}

public class LimitOrder extends Order
{
    private double price;

    public LimitOrder(String side, double price, int qty)
    {
        super("limit", side, qty);
        if (price <= 0.0)
            throw new IllegalArgumentException("Invalid arguments");
        
        this.price = price;
    }
}