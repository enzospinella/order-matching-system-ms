public class Trade
{
    private String buyOrderId;
    private String sellOrderId;
    private double price;
    private int qty;

    public Trade(String buyOrderId, String sellOrderId, double price, int qty)
    {
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.price = price;
        this.qty = qty;
    }

    public String getBuyOrderId()
    {
        return buyOrderId;
    }

    public String getSellOrderId()
    {
        return sellOrderId;
    }

    public double getPrice()
    {
        return price;
    }

    public int getQty()
    {
        return qty;
    }
    
    public String toString()
    {
        return "Trade, price: " + price + ", qty: " + qty + "\n";
    }
}
