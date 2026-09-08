public class PeggedOrder extends Order
{
    private double price;

    public PeggedOrder(String side, int qty)
    {
        super("limit", side, qty);
    }

    public double getPrice()
    {
        return price;
    }

    public String toString()
    {
        if(super.getSide().equals("buy"))
            return "Order created: " + super.getSide() + " " + super.getQty() + " @ best bid " + super.getId();
        else
            return "Order created: " + super.getSide() + " " + super.getQty() + " @ best offer " + super.getId();
    }
}