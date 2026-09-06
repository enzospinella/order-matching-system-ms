public class MarketOrder extends Order
{
    public MarketOrder(String side, int qty)
    {
        super("market", side, qty);
    }
}