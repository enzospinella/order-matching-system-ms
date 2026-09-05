import java.util.*;

public class MatchingEngine
{
    private TreeMap<Double, Queue<Order>> buys;
    private TreeMap<Double, Queue<Order>> sells;
    private HashMap<String, Order> orders;

    public MatchingEngine(int qty_asset)
    {
        buys = new TreeMap<>();
        sells = new TreeMap<>();
        orders = new HashMap<>();
    }

    public void processOrder(MarketOrder order)
    {
        processMarketOrder(order);
    }
    public void processOrder(LimitOrder order)
    {
        processLimitOrder(order);
    }

    private void processMarketOrder(MarketOrder order)
    {

    }
    private void processLimitOrder(LimitOrder order)
    {
        
    }
}