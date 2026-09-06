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

    public double getPrice()
    {
        return price;
    }

    public String toString()
    {
        return "Order created: " + super.getSide() + " " + super.getQty() + " @ " + price + " " + super.getId();
    }
}