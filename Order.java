public abstract class Order
{
    private String id;
    private String type;
    private String side;
    private int qty;

    public Order(String type, String side, int qty)
    {
        if ((!type.equals("limit") && !type.equals("market")) || 
            (!side.equals("buy") && !side.equals("sell")) ||
            qty <= 0)
            throw new IllegalArgumentException("Invalid arguments");

        this.id = java.util.UUID.randomUUID().toString();
        this.type = type;
        this.side = side;
        this.qty = qty;
    }

    public String getId()
    {
        return id;
    }

    public String getType()
    {
        return type;
    }

    public String getSide()
    {
        return side;
    }

    public int getQty()
    {
        return qty;
    }
    public void setQty(int qty)
    {
        if (qty < 0)
            throw new IllegalArgumentException("Invalid arguments");
        this.qty = qty;
    }
}