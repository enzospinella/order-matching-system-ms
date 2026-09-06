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

    private List<Trade> processMarketOrder(MarketOrder order)
    {
        List<Trade> trades = new ArrayList<>();
        if (order.getSide().equals("buy"))
        {
            int qty_bought = 0;
            for (Map.Entry<Double, Queue<Order>> entry : sells.entrySet())
            {
                Queue<Order> sellOrders = entry.getValue();
                while (!sellOrders.isEmpty() && order.getQty() > 0)
                {
                    Order sellOrder = sellOrders.peek();
                    int matchedQty = Math.min(order.getQty(), sellOrder.getQty());
                    qty_bought += matchedQty;
                    order.setQty(order.getQty() - matchedQty);
                    sellOrder.setQty(sellOrder.getQty() - matchedQty);
                    Trade trade = new Trade(order.getId(), sellOrder.getId(), entry.getKey(), matchedQty);
                    trades.add(trade);
                    if (sellOrder.getQty() == 0)
                        sellOrders.poll();
                }
                if (order.getQty() == 0)
                    break;
            }
        }
        else
        {
            // Process sell market order
        }
        return trades;
    }
    private void processLimitOrder(LimitOrder order)
    {
        if (order.getSide().equals("buy"))
        {
            // Process buy limit order
        }
        else
        {
            // Process sell limit order
        }
    }
}