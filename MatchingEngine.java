import java.util.*;

public class MatchingEngine
{
    private TreeMap<Double, Queue<Order>> buys;
    private TreeMap<Double, Queue<Order>> sells;
    private HashMap<String, Order> orders;
    private HashMap<String, Order> peggedBuys;
    private HashMap<String, Order> peggedSells;
    private double bid;
    private double offer;

    private double getBid()
    {
        return buys.isEmpty() ? 0.0 : buys.lastKey();
    }
    private double getOffer()
    {
        return sells.isEmpty() ? 0.0 : sells.firstKey();
    }
    private void trySetBid(double bid)
    {
        if(bid < 0.0)
            throw new IllegalArgumentException("Invalid bid");
        if (bid > this.getBid())
        {
            for (Order peggedOrder : peggedBuys.values())
            {
                alterOrder(peggedOrder.getId(), bid);
            }
            this.bid = bid;
        }
    }
    private void trySetOffer(double offer)
    {
        if (offer < 0.0)
            throw new IllegalArgumentException("Invalid offer");
        if (offer > this.getOffer())
        {
            for (Order peggedOrder : peggedSells.values())
            {
                alterOrder(peggedOrder.getId(), offer);
            }
            this.offer = offer;
        }
    }

    public MatchingEngine()
    {
        buys = new TreeMap<>();
        sells = new TreeMap<>();
        orders = new HashMap<>();
        peggedBuys = new HashMap<>();
        peggedSells = new HashMap<>();

        bid = 0.0;
        offer = 0.0;
    }

    public void cancelOrder(String orderId)
    {
        Order order = orders.get(orderId);
        if (order == null)
            throw new IllegalArgumentException("Order not found");
        LimitOrder limitOrder = (LimitOrder) order;

        if (order.getSide().equals("buy"))
        {
            Queue<Order> buyOrders = buys.get(limitOrder.getPrice());
            if (buyOrders != null)
            {
                buyOrders.remove(order);
                if (buyOrders.isEmpty())
                    buys.remove(limitOrder.getPrice());
            }
        }
        else
        {
            Queue<Order> sellOrders = sells.get(limitOrder.getPrice());
            if (sellOrders != null)
            {
                sellOrders.remove(order);
                if (sellOrders.isEmpty())
                    sells.remove(limitOrder.getPrice());
            }
        }
        orders.remove(orderId);
        if(peggedBuys.containsKey(orderId))
            peggedBuys.remove(orderId);
        if(peggedSells.containsKey(orderId))
            peggedSells.remove(orderId);
    }

    public HashMap<Double, Trade> processOrder(MarketOrder order)
    {
        HashMap<Double, Trade> tradesMap = new HashMap<>();
        if (order.getSide().equals("buy"))
        {
            for (Map.Entry<Double, Queue<Order>> entry : sells.entrySet())
            {
                Queue<Order> sellOrders = entry.getValue();
                while (!sellOrders.isEmpty() && order.getQty() > 0)
                {
                    Order sellOrder = sellOrders.peek();
                    int matchedQty = Math.min(order.getQty(), sellOrder.getQty());
                    order.setQty(order.getQty() - matchedQty);
                    sellOrder.setQty(sellOrder.getQty() - matchedQty);

                    if (tradesMap.containsKey(entry.getKey()))
                    {
                        Trade existingTrade = tradesMap.get(entry.getKey());
                        int newQty = existingTrade.getQty() + matchedQty;
                        Trade updatedTrade = new Trade(existingTrade.getBuyOrderId(), existingTrade.getSellOrderId(), existingTrade.getPrice(), newQty);
                        tradesMap.put(entry.getKey(), updatedTrade);
                    }
                    else
                    {
                        Trade trade = new Trade(order.getId(), sellOrder.getId(), entry.getKey(), matchedQty);
                        tradesMap.put(entry.getKey(), trade);
                    }

                    if (sellOrder.getQty() == 0)
                        sellOrders.poll();
                }
                if (order.getQty() == 0)
                    break;
            }
        }
        else
        {
            for (Map.Entry<Double, Queue<Order>> entry : buys.descendingMap().entrySet())
            {
                Queue<Order> buyOrders = entry.getValue();
                while (!buyOrders.isEmpty() && order.getQty() > 0)
                {
                    Order buyOrder = buyOrders.peek();
                    int matchedQty = Math.min(order.getQty(), buyOrder.getQty());
                    order.setQty(order.getQty() - matchedQty);
                    buyOrder.setQty(buyOrder.getQty() - matchedQty);

                    if (tradesMap.containsKey(entry.getKey()))
                    {
                        Trade existingTrade = tradesMap.get(entry.getKey());
                        int newQty = existingTrade.getQty() + matchedQty;
                        Trade updatedTrade = new Trade(existingTrade.getBuyOrderId(), existingTrade.getSellOrderId(), existingTrade.getPrice(), newQty);
                        tradesMap.put(entry.getKey(), updatedTrade);
                    }
                    else
                    {
                        Trade trade = new Trade(buyOrder.getId(), order.getId(), entry.getKey(), matchedQty);
                        tradesMap.put(entry.getKey(), trade);
                    }

                    if (buyOrder.getQty() == 0)
                        buyOrders.poll();
                }
                if (order.getQty() == 0)
                    break;
            }
        }
        return tradesMap;
    }

    public HashMap<Double, Trade> processOrder(LimitOrder order)
    {
        HashMap<Double, Trade> tradesMap = new HashMap<>();
        if (order.getSide().equals("buy"))
        {
            for (Map.Entry<Double, Queue<Order>> entry : sells.entrySet())
            {
                if (entry.getKey() <= order.getPrice())
                {
                    Queue<Order> sellOrders = entry.getValue();
                    while (!sellOrders.isEmpty() && order.getQty() > 0)
                    {
                        Order sellOrder = sellOrders.peek();
                        int matchedQty = Math.min(order.getQty(), sellOrder.getQty());
                        order.setQty(order.getQty() - matchedQty);
                        sellOrder.setQty(sellOrder.getQty() - matchedQty);

                        if (tradesMap.containsKey(entry.getKey()))
                        {
                            Trade existingTrade = tradesMap.get(entry.getKey());
                            int newQty = existingTrade.getQty() + matchedQty;
                            Trade updatedTrade = new Trade(existingTrade.getBuyOrderId(), existingTrade.getSellOrderId(), existingTrade.getPrice(), newQty);
                            tradesMap.put(entry.getKey(), updatedTrade);
                        }
                        else
                        {
                            Trade trade = new Trade(order.getId(), sellOrder.getId(), entry.getKey(), matchedQty);
                            tradesMap.put(entry.getKey(), trade);
                        }

                        if (sellOrder.getQty() == 0)
                            sellOrders.poll();
                    }
                    if (order.getQty() == 0)
                        break;
                }
            }
            if (order.getQty() > 0)
            {
                trySetBid(order.getPrice());

                buys.putIfAbsent(order.getPrice(), new LinkedList<>());
                buys.get(order.getPrice()).add(order);
                orders.put(order.getId(), order);
            }
        }
        else
        {
            for (Map.Entry<Double, Queue<Order>> entry : buys.descendingMap().entrySet())
            {
                if (entry.getKey() >= order.getPrice())
                {
                    Queue<Order> buyOrders = entry.getValue();
                    while (!buyOrders.isEmpty() && order.getQty() > 0)
                    {
                        Order buyOrder = buyOrders.peek();
                        int matchedQty = Math.min(order.getQty(), buyOrder.getQty());
                        order.setQty(order.getQty() - matchedQty);
                        buyOrder.setQty(buyOrder.getQty() - matchedQty);

                        if (tradesMap.containsKey(entry.getKey()))
                        {
                            Trade existingTrade = tradesMap.get(entry.getKey());
                            int newQty = existingTrade.getQty() + matchedQty;
                            Trade updatedTrade = new Trade(existingTrade.getBuyOrderId(), existingTrade.getSellOrderId(), existingTrade.getPrice(), newQty);
                            tradesMap.put(entry.getKey(), updatedTrade);
                        }
                        else
                        {
                            Trade trade = new Trade(buyOrder.getId(), order.getId(), entry.getKey(), matchedQty);
                            tradesMap.put(entry.getKey(), trade);
                        }

                        if (buyOrder.getQty() == 0)
                            buyOrders.poll();
                    }
                    if (order.getQty() == 0)
                        break;
                }
            }
            if (order.getQty() > 0)
            {
                trySetOffer(order.getPrice());

                sells.putIfAbsent(order.getPrice(), new LinkedList<>());
                sells.get(order.getPrice()).add(order);
                orders.put(order.getId(), order);
            }
        }
        return tradesMap;
    }

    public HashMap<Double, Trade> processOrder(PeggedOrder order)
    {
        LimitOrder limitOrder;
        if (order.getSide().equals("buy")) {
            double referenceBid = getBid();
            if (referenceBid == 0.0)
                throw new IllegalArgumentException("No reference bid available for peg order.");
            limitOrder = new LimitOrder(order.getId(), order.getSide(), referenceBid, order.getQty());
            peggedBuys.put(order.getId(), order);
        }
        else {
            double referenceOffer = getOffer();
            if (referenceOffer == 0.0)
                throw new IllegalArgumentException("No reference offer available for peg order.");
            limitOrder = new LimitOrder(order.getId(), order.getSide(), referenceOffer, order.getQty());
            peggedSells.put(order.getId(), order);
        }
        return processOrder(limitOrder);
    }

    public HashMap<Double, Trade> alterOrder(String orderId, int newQty)
    {
        Order order = orders.get(orderId);
        if (order == null)
            throw new IllegalArgumentException("Order not found");
        LimitOrder limitOrder = (LimitOrder) order;

        cancelOrder(orderId);
        LimitOrder alteredOrder = new LimitOrder(orderId, limitOrder.getSide(), limitOrder.getPrice(), newQty);
        return processOrder(alteredOrder);
    }

    public HashMap<Double, Trade> alterOrder(String orderId, double newPrice)
    {
        Order order = orders.get(orderId);
        if (order == null)
            throw new IllegalArgumentException("Order not found");
        LimitOrder limitOrder = (LimitOrder) order;

        cancelOrder(orderId);
        LimitOrder alteredOrder = new LimitOrder(orderId, limitOrder.getSide(), newPrice, limitOrder.getQty());
        return processOrder(alteredOrder);
    }

    public HashMap<Double, Trade> alterOrder(String orderId, int newQty, double newPrice)
    {
        Order order = orders.get(orderId);
        if (order == null)
            throw new IllegalArgumentException("Order not found");
        LimitOrder limitOrder = (LimitOrder) order;

        cancelOrder(orderId);
        LimitOrder alteredOrder = new LimitOrder(orderId, limitOrder.getSide(), newPrice, newQty);
        return processOrder(alteredOrder);
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        List<String> buyRows = new ArrayList<>();
        List<String> sellRows = new ArrayList<>();

        for (Map.Entry<Double, Queue<Order>> entry : buys.descendingMap().entrySet())
            for (Order order : entry.getValue())
                buyRows.add(order.getQty() + " @ " + entry.getKey());

        for (Map.Entry<Double, Queue<Order>> entry : sells.entrySet())
            for (Order order : entry.getValue())
                sellRows.add(order.getQty() + " @ " + entry.getKey());

        sb.append(String.format("%-17s| %s%n", "Ordens de Compra", "Ordens de Venda"));
        sb.append("-----------------|-----------------\n");

        int rowCount = Math.max(buyRows.size(), sellRows.size());
        for (int i = 0; i < rowCount; i++)
        {
            String buy = i < buyRows.size() ? buyRows.get(i) : "";
            String sell = i < sellRows.size() ? sellRows.get(i) : "";
            sb.append(String.format("%-17s| %s%n", buy, sell));
        }
        return sb.toString();
    }
}