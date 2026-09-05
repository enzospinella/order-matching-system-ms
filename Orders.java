

public class Order
{
    private String type;
    private String side;
    private int qty;

    public Ordem(String type, String side, int qty)
    {
        if ((type != "limit" && type != "market") || 
            (side != "buy" && side != "sell") ||
            qty <= 0)
            throw new IllegalArgumentException("Invalid arguments");

        this.tipo = tipo;
        this.side = side;
        this.qty = qty;
    }
}

public class MarketOrder extends Order
{
    public MarketOrder(String type, String side, int qty)
    {
        super(type, side, qty);
    }
}

public class LimitOrder extends Order
{
    private double price;

    public LimitOrder(String type, String side, double price, int qty)
    {
        super(type, side, qty);
        if (price <= 0.0)
            throw new IllegalArgumentException("Invalid arguments");
        
        this.price = price;
    }
}